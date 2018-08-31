package com.letv.woodpecker.wpserver.service.impl;


import com.letv.woodpecker.wpdatamodel.dao.AlarmConfigDao;
import com.letv.woodpecker.wpdatamodel.dao.AlarmHistoryDao;
import com.letv.woodpecker.wpdatamodel.dao.ExceptionInfoDao;
import com.letv.woodpecker.wpdatamodel.model.*;
import com.letv.woodpecker.wpdatamodel.service.RuleConfigService;
import com.letv.woodpecker.wpserver.message.MessageBean;
import com.letv.woodpecker.wpserver.service.ConsumeServer;
import com.letv.woodpecker.wpserver.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhusheng on 17/3/15.
 *
 * @author zhusheng modify by meijunjie
 * TODO 动态调节异常阈值
 */

@Slf4j
@Service("consumeServer")
@SuppressWarnings("unchecked")
public class ConsumeServerImpl implements ConsumeServer {

    private static final String EXCEPTION = "Exception";
    private static final String ERROR = "Error:";
    private static final String ALL = "all";
    private static final String EACH = "each";

    @Resource
    private ExceptionInfoDao exceptionInfoDao;
    @Resource
    private AlarmConfigDao alarmConfigDao;
    @Resource
    private AlarmHistoryDao alarmHistoryDao;
    @Resource
    private RuleConfigService ruleConfigService;
    @Resource
    private ThreadPoolManageUtil threadPoolManageUtil;


    private RedisTemplate redisTemplate;

    /**
     * StringRedisSerializer 用于序列化 字符
     * RedisTemplate默认使用JdkSerializationRedisSerializer 进行序列化，它有个缺点就是生成的序列化文件可读性差，而且占用空间大
     * GenericJackson2JsonRedisSerializer，可以将对象序列化成JSON格式存储在redis中
     *
     * @param redisTemplate redis操作封装类
     */
    @Autowired(required = false)
    @SuppressWarnings(value = "unchecked")
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void doConsume(ExceptionInfo exceptionInfo) {
        // 无效异常信息过滤
        if (exceptionInfo == null) {
            return;
        }
        List<AlarmConfig> configs = new ArrayList<>(4);
        // 获取全局异常
        AlarmConfig globalConfig = alarmConfigDao.queryGlobalAlarmCinfig(exceptionInfo.getAppName());
        // 检测是否需要报警
        if (globalConfig != null) {
            // 执行报警
            if (isAutoAlarm(exceptionInfo, globalConfig)) {
                configs.add(globalConfig);
            }
        } else {
            configs = alarmConfigDao.queryList(exceptionInfo.getAppName(), exceptionInfo.getIp(), exceptionInfo.getExceptionType());
        }
        for (AlarmConfig config : configs) {
            exceptionAlarm(config, exceptionInfo);
        }

        try {
            exceptionInfoDao.save(exceptionInfo);
        } catch (MailException e) {
            log.error("save exceptionInfo fail! msg {}", exceptionInfo);
        }
    }

    /**
     * 解析异常信息
     *
     * @param exceptionInfo 异常信息
     * @return ExceptionInfo实体
     */
    @Override
    public ExceptionInfo parseExceptionInfo(MessageBean exceptionInfo) {
        if (exceptionInfo == null) {
            return null;
        }
        ExceptionInfo msgObject = new ExceptionInfo();
        String msg = exceptionInfo.getMsg();
        String exceptionName = getExceptionString(msg);

        if (exceptionName == null) return null;

        String md5Str = msg.substring(msg.indexOf(" - ") + 3);
        msgObject.setContentMd5(Md5Util.getMd5(md5Str));
        msgObject.setExceptionType(exceptionName);
        msgObject.setAppName(exceptionInfo.getAppName());
        msgObject.setIp(exceptionInfo.getIp());
        msgObject.setLogTime(exceptionInfo.getCreateTime());
        msgObject.setCreateTime(timeForNow());
        msgObject.setMsg(msg);

        // 异步线程处理异常信息 记录存储redis
        threadPoolManageUtil.getThreadPoolByKey("realTimeExceptionStatics").execute(() -> saveRealExceptionInfo(msgObject, redisTemplate));
        return msgObject;
    }


    /**
     * 细化当前异常类型
     *
     * @param exceptionInfo
     * @param globalConfig
     */
    @SuppressWarnings("unchecked")
    private boolean isAutoAlarm(ExceptionInfo exceptionInfo, AlarmConfig globalConfig) {
        if (exceptionInfo == null || globalConfig == null) {
            return false;
        }
        int[] alarmData = getAlarmData(exceptionInfo);

        Double alarmThresholdValue = alarmData[1] * globalConfig.getMultiple();

        return alarmData[0] > alarmThresholdValue;
    }


    private int[] getAlarmData(ExceptionInfo exceptionInfo) {
        if (exceptionInfo == null) {
            return null;
        }
        int[] alarmData = new int[2];
        String appName = exceptionInfo.getAppName();
        String exceptionType = exceptionInfo.getExceptionType();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        int max = 0;
        int compareDay = 4;
        for (int i = 0; i < compareDay; i++) {
            String now = DateUtil.getStringDateByNum(new Date(), -i);
            String key = (appName + "_" + exceptionType + "_" + now.substring(now.indexOf("-") + 1, now.indexOf(" ") + 1)).replace(" ", "-");
            if (i == 0) {
                alarmData[0] = valueOperations.get(key) == null ? 0 : Integer.valueOf(valueOperations.get(key));
            } else {
                // 获取前三内当前异常的最大值
                int temp = valueOperations.get(key) == null ? 0 : Integer.valueOf(valueOperations.get(key));
                if (max < temp) {
                    max = temp;
                }
            }
        }
        alarmData[1] = max;
        return alarmData;
    }


    /**
     * 比较两个异常的消息的相似性 采用Jaro距离
     * TODO 后期可以考虑的方案 LDA 词向量
     */
    private boolean computeTextSimilarity(String source, String target) {
        boolean result = false;
        double flag = 0.75;
        if (StringUtils.getJaroWinklerDistance(source, target) > flag) {
            result = true;
        }
        return result;
    }


    /**
     * 实时记录异常数据
     */
    @SuppressWarnings(value = "unchecked")
    private void saveRealExceptionInfo(ExceptionInfo exceptionInfo, RedisTemplate redisTemplate) {
        SetOperations setOperations = redisTemplate.opsForSet();
        // 按应用存储其异常类型
        setOperations.add(exceptionInfo.getAppName() + "_exception_set", exceptionInfo.getExceptionType());
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String now = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        String appName = exceptionInfo.getAppName();
        String exceptionType = exceptionInfo.getExceptionType();
        String keyPerMinute = appName + "_" + exceptionType + "_" + now.substring(now.indexOf("-") + 1, now.lastIndexOf(":")).replace(" ", "-");
        String keyPerHour = (appName + "_" + exceptionType + "_" + now.substring(now.indexOf("-") + 1, now.indexOf(":"))).replace(" ", "-");
        String keyPerDay = (appName + "_" + exceptionType + "_" + now.substring(now.indexOf("-") + 1, now.indexOf(" ") + 1)).replace(" ", "-");
        // 存储每分钟内的异常数，失效时间20分钟
        if (valueOperations.get(keyPerMinute) == null) {
            valueOperations.set(keyPerMinute, "1", 20, TimeUnit.MINUTES);
        } else {
            valueOperations.increment(keyPerMinute, 1);
        }
        // 存储每小时内的异常数，失效时间24小时
        if (valueOperations.get(keyPerHour) == null) {
            valueOperations.set(keyPerHour, "1", 24, TimeUnit.HOURS);
        } else {
            valueOperations.increment(keyPerHour, 1);
        }
        // 存储每天内的异常数，失效时间20天
        if (valueOperations.get(keyPerDay) == null) {
            valueOperations.set(keyPerDay, "1", 20, TimeUnit.DAYS);
        } else {
            valueOperations.increment(keyPerDay, 1);
        }
    }


    private String getExceptionString(String msg) {
        String exceptionName = "";

        if (msg != null && msg.contains(EXCEPTION)) {
            exceptionName = msg.substring(0, msg.indexOf(EXCEPTION) + EXCEPTION.length());
            if (exceptionName.lastIndexOf(".") > 0) {
                exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1, exceptionName.length());
            }
        }
        if (msg != null && msg.contains(ERROR)) {
            exceptionName = msg.substring(0, msg.indexOf(ERROR) + ERROR.length());
            exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1, exceptionName.length());
        }
        if (msg == null) {
            return null;
        }

        if (exceptionName.endsWith(":")) {
            exceptionName = exceptionName.replace(":", "");
        }
        if (StringUtils.isBlank(exceptionName)) {
            exceptionName = "others";
        }
        return exceptionName;
    }


    /**
     * 异常警报
     *
     * @param config        告警配置
     * @param exceptionInfo 异常信息
     */
    private void exceptionAlarm(final AlarmConfig config, final ExceptionInfo exceptionInfo) {
        if (config == null) {
            return;
        }
        //判断是否关联规则过滤
        boolean isFilter = isFilter(config, exceptionInfo);

        //为true过滤不发送报警
        if (isFilter) {
            return;
        }

        final String appName = config.getAppName();
        if (null != config.getConfigType() && config.getConfigType().equals("GLOBAL")) {
            final String ip = exceptionInfo.getIp();
            String latestAlarmTime = alarmHistoryDao.getLatestAlarmTime(appName, "all", exceptionInfo.getExceptionType());
            int current = (int) exceptionInfoDao.getExceptionCount(appName, "all", exceptionInfo.getExceptionType(), latestAlarmTime);
            if (current + 1 >= config.getThreshold()) {
                config.setIp("all");
                if (!checkAlarmFreq(config)) {
                    threadPoolManageUtil.getThreadPoolByKey("mailSend").execute(() -> doAlarm(appName, ip, exceptionInfo.getIp(), exceptionInfo.getExceptionType(), config, exceptionInfo.getMsg()));
                }
            }
        } else {
            final String ip = ALL.equals(config.getIp()) ? ALL : exceptionInfo.getIp();

            // 报警中最好不要出现all 这种全部异常输出的（后期在报警配置中需要完善）
            final String exceptionType = ALL.equals(config.getExceptionType()) ? ALL : exceptionInfo.getExceptionType();
            //获取最近的告警时间
            String latestAlarmTime = alarmHistoryDao.getLatestAlarmTime(appName, ip, exceptionType);
            //获取最近告警次数
            int current = (int) exceptionInfoDao.getExceptionCount(appName, ip, exceptionType, latestAlarmTime);

            //判断告警是否超过阈值，是就进行邮件通知
            //针对appName ip exceptionType 进行过滤，对同一个appName+ip+exceptionType的配置最好发送报警人一样，不能多份，多份会导致被同样规则过滤
            if (current + 1 >= config.getThreshold()) {
                // 控制发送邮件的频率,从告警配置中获取参数
                boolean flag = checkAlarmFreq(config);
                if (!flag) {
                    threadPoolManageUtil.getThreadPoolByKey("mailSend").execute(()
                            -> doAlarm(appName, ip, exceptionInfo.getIp(), exceptionType, config, exceptionInfo.getMsg()));
                }
            }
        }
    }


    /**
     * 为ture过滤不发送 为false发送报警
     *
     * @param config
     * @param exceptionInfo
     * @return
     */
    private boolean isFilter(AlarmConfig config, ExceptionInfo exceptionInfo) {
        //默认发送报警
        boolean isFilter = false;
        try {
            if (null != config.getRuleId()) {
                isFilter = dealRule(config.getRuleId(), exceptionInfo);
            }
        } catch (Exception e) {
            log.info("获取规则发生异常!e={}", e);
        }
        return isFilter;
    }


    private boolean dealRule(String ruleId, ExceptionInfo exceptionInfo) {
        boolean isFilter = false;
        if (StringUtils.isNotEmpty(ruleId)) {
            //获取规则配置脚本
            RuleConfig ruleConfig = ruleConfigService.queryRuleConfig(ruleId);
            if (null != ruleConfig) {
                Object resObject = GroovyTool.getInstance().runGroovyScript(ruleConfig.getRuleConfig(),
                        "validate", new String[]{exceptionInfo.getMsg()});
                if (null == resObject) {
                    //不进行过滤 还需要发报警
                    return false;
                }
                if (resObject.toString().equals("true")) {
                    //规则为true不发送报警
                    log.info("规则为ture不发送报警,ruleid=" + ruleConfig.get_id() + "::" + exceptionInfo.getMsg());
                    isFilter = true;
                } else if (resObject.toString().equals("false")) {
                    //规则返回为false发送报警
                    log.info("规则返回为false发送报警,ruleid=" + ruleConfig.get_id() + "::" + exceptionInfo.getMsg());
                    isFilter = false;
                }
            }

        }
        return isFilter;
    }


    /**
     * 执行报警操作
     *
     * @param appName       应用名
     * @param ip            IP地址
     * @param realIP        真实IP
     * @param exceptionType 异常类型
     * @param config        告警配置
     * @param msg           告警信息
     */
    private void doAlarm(String appName, String ip, String realIP, String exceptionType, AlarmConfig config, String msg) {
        String alarmMsg = "ip=" + realIP + ",异常信息: " + msg;
        //发送告警短信
        if (StringUtils.isNotBlank(config.getPhoneNum())) {
            String[] phoneNums = config.getPhoneNum().split(";");
            String alarmMsgForSms = alarmMsg.substring(0, alarmMsg.indexOf("Exception") + "Exception".length());
            for (String phone : phoneNums) {
                GlobalSendSmsUtil.sendMessage(phone, alarmMsgForSms);
            }
        }
        // 发送告警邮件
        if (StringUtils.isNotBlank(config.getEmail())) {
            String[] emails = config.getEmail().split(";");
            for (String email : emails) {
                JavaLetvMail.doSendHtmlEmail("【" + appName + "异常告警】", alarmMsg, email, email, null);
            }
        }
        //发送微信公众号报警
        if (StringUtils.isNotBlank(config.getCorpid()) && StringUtils.isNotBlank(config.getSecret())
                && StringUtils.isNotBlank(config.getToparty()) && StringUtils.isNotBlank(config.getAgentid())) {
            WeChatUtil.getInstance().sendMessage(config.getCorpid(),
                    config.getSecret(), config.getToparty(), config.getAgentid(), "@all", alarmMsg);
        }

        //写到告警历史表
        AlarmHistory history = new AlarmHistory();
        history.setAppName(appName);
        history.setIp(ip);
        history.setExceptionType(exceptionType);
        history.setAlarmTime(timeForNow());
        alarmHistoryDao.save(history);
    }


    private String timeForNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return format.format(now);
    }

    /**
     * 检测当前异常类型的发送频率
     * 在设置的时间内只能发送一次
     *
     * @param alarmConfig 告警配置
     * @return 检测结果
     */
    @SuppressWarnings(value = "unchecked")
    private Boolean checkAlarmFreq(AlarmConfig alarmConfig) {
        Boolean isAlarmed = false;
        if (alarmConfig != null && alarmConfig.getAlarmFrequency() != null) {

            String key = alarmConfig.getAppName() + "_" + alarmConfig.getIp() + "_" + alarmConfig.getExceptionType();
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Long inc = valueOperations.increment(key, 1);

            if (null != inc && inc == 1) {
                redisTemplate.expire(key, Long.valueOf(alarmConfig.getAlarmFrequency()), TimeUnit.MINUTES);
                return false;
            } else {
                return true;
            }
        } else {
            return isAlarmed;
        }
    }


}

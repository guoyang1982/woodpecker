package com.letv.woodpecker.wpserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.letv.woodpecker.wpdatamodel.model.RuleConfig;
import com.letv.woodpecker.wpdatamodel.service.RuleConfigService;
import com.letv.woodpecker.wpserver.message.MessageBean;
import com.letv.woodpecker.wpserver.service.ConsumeServer;
import com.letv.woodpecker.wpserver.utils.*;
import com.letv.woodpecker.wpdatamodel.dao.AlarmConfigDao;
import com.letv.woodpecker.wpdatamodel.dao.AlarmHistoryDao;
import com.letv.woodpecker.wpdatamodel.dao.ExceptionInfoDao;
import com.letv.woodpecker.wpdatamodel.model.AlarmConfig;
import com.letv.woodpecker.wpdatamodel.model.AlarmHistory;
import com.letv.woodpecker.wpdatamodel.model.ExceptionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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
        //获取该条异常所有的报警规则
        List<AlarmConfig> configs = alarmConfigDao.queryList(exceptionInfo.getAppName(), exceptionInfo.getIp(), exceptionInfo.getExceptionType());
        for (AlarmConfig config : configs) {
            //报警
            exceptionAlarm(config, exceptionInfo);
        }
        try {
            //将报警信息插入MongoDB
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
        // JSONObject jsonObject = JSON.parseObject(exceptionInfo);
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
        return msgObject;
    }

    private String getExceptionString(String msg) {
        String exceptionName = "";

        if (msg != null && msg.contains(EXCEPTION)) {
            exceptionName = msg.substring(0, msg.indexOf(EXCEPTION) + EXCEPTION.length());
            if(exceptionName.lastIndexOf(".")>0){
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

        if(exceptionName.endsWith(":")){
            exceptionName = exceptionName.replace(":","");
        }
        if(StringUtils.isBlank(exceptionName)){
            exceptionName = "others";
        }
        return exceptionName;
    }

//    public static void main(String args[]){
//        String s = "VipServiceImpl--> Get user vip info error! userId=264271933,exceptionInfo=java.lang.NullPointerException\n";
//    System.out.println(new ConsumeServerImpl().getExceptionString(s));
//
//    }

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
        boolean isfilter = isfilter(config, exceptionInfo);

        //为true过滤不发送报警
        if (isfilter) {
            return;
        }

        final String appName = config.getAppName();
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

    /**
     * 为ture过滤不发送 为false发送报警
     * @param config
     * @param exceptionInfo
     * @return
     */
    private boolean isfilter(AlarmConfig config, ExceptionInfo exceptionInfo) {
        //默认发送报警
        boolean isfilter = false;
        try{
            if (null != config.getRuleId()) {
                //获取规则配置脚本
                RuleConfig ruleConfig = ruleConfigService.queryRuleConfig(config.getRuleId());
                if (null != ruleConfig) {
                    Object resObject = GroovyTool.getInstance().runGroovyScript(ruleConfig.getRuleConfig(),
                            "validate", new String[]{exceptionInfo.getMsg()});
                    if (null == resObject) {
                        //不进行过滤 还需要发报警
                        return false;
                    }
                    if (resObject.toString().equals("true")) {
                        //规则为true不发送报警
                        log.info("规则为ture不发送报警,ruleid="+ruleConfig.get_id()+"::"+exceptionInfo.getMsg());
                        isfilter = true;
                    } else if (resObject.toString().equals("false")) {
                        //规则返回为false发送报警
                        log.info("规则返回为false发送报警,ruleid="+ruleConfig.get_id()+"::"+exceptionInfo.getMsg());
                        isfilter = false;
                    }
                }
            }
        }catch (Exception e){
            log.info("获取规则发生异常!e={}",e);
        }
        return isfilter;
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
        }
        return isAlarmed;
    }


}

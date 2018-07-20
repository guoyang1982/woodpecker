package com.letv.woodpecker.log;

import com.alibaba.fastjson.JSONObject;
import com.letv.woodpecker.bean.MessageBean;
import com.letv.woodpecker.tools.IPUtile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by zhusheng on 17/3/10.
 */
@Data
@Slf4j
public abstract class LoggerFacility {


    protected RedisTemplate jedisTemplate;

    @SuppressWarnings(value = "unchecked")
    @Autowired
    public void setRedisTemplate(RedisTemplate jedisTemplate) {
        jedisTemplate.setKeySerializer(new StringRedisSerializer());
        jedisTemplate.setValueSerializer(new StringRedisSerializer());

        this.jedisTemplate = jedisTemplate;
    }


    private ExecutorService executorPools = new ThreadPoolExecutor(8, 8, 10L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(5000),
            new MessageRejectedExecutionHandler());

    protected String appName;

    private static volatile boolean f = true;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public abstract void load();

    public void init(){
        load();
    }


    /**
     * send error messages to redis cluster
     * @param msg
     */
    @SuppressWarnings(value = "unchecked")
    public void sendToRedis(final String msg) {
        log.info("发送异常日志消息!");

        if (!f) {
            log.info("redis集群不健康, 不处理操作!");
            return;
        }
        executorPools.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageBean messageBean = new MessageBean();
                    messageBean.setAppName(appName);
                    messageBean.setIp(IPUtile.getIntranetIP());
                    messageBean.setMsg(msg);
                    messageBean.setCreateTime(timeForNow());


                    jedisTemplate.opsForList().rightPush(appName, JSONObject.toJSONString(messageBean));

                } catch (Exception e) {
                    log.info("发送异常日志消息失败!",e);
                }

            }
        });
    }
    private String timeForNow(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return format.format(now);
    }

    /**
     * 打印拒绝任务时 线程池的详细信息
     */
    private static class MessageRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 监控
            if (log.isInfoEnabled()) {
                log.info("LoggerFacility rejectedExecution, ThreadPoolExecutor:{}", executor.toString());
            }
        }
    }

    {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        long delay = 10000;
        long initDelay = 0;
        executor.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("执行redis健康检查!");
                        try{
                            if(null != jedisTemplate){
                                jedisTemplate.opsForValue().set(appName+"-ping","1",1l,TimeUnit.SECONDS);
                            }
                            f = true;
                        }catch (Exception e){
                            log.info("redis健康检查异常,{}",e);
                            f = false;
                        }
                    }
                },
                initDelay,
                delay,
                TimeUnit.MILLISECONDS);
    }
}

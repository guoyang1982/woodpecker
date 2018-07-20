package com.letv.woodpecker.wpserver.redis;


import com.letv.woodpecker.wpdatamodel.dao.AppInfoDao;
import com.letv.woodpecker.wpdatamodel.model.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhusheng on 17/3/16.
 */
@Slf4j
@Configuration
public class InitializeQueue implements InitializingBean, DisposableBean {

    @Resource
    private AppInfoDao appInfoDao;

    @Resource
    private QueueListener listener;



    private RedisTemplate redisTemplate;

    /**
     * StringRedisSerializer 用于序列化 字符
     * RedisTemplate默认使用JdkSerializationRedisSerializer 进行序列化，它有个缺点就是生成的序列化文件可读性差，而且占用空间大
     * GenericJackson2JsonRedisSerializer，可以将对象序列化成JSON格式存储在redis中
     * @param redisTemplate
     */
    @Autowired(required = false)
    @SuppressWarnings(value = "unchecked")
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }

    private static List<RedisQueue> listQ = new ArrayList<>();


    /**
     * 创建启动redis连接并监听
     */
    public void createQueue() {

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set("hello","world",1000,TimeUnit.SECONDS);
        System.out.println(redisTemplate.hasKey("hello123"));
        System.out.println(operations.get("hello"));
        List<AppInfo> apps = appInfoDao.queryList(null);
        if (apps != null) {
            for (AppInfo app : apps) {
                RedisQueue queue = new RedisQueue();
                queue.setListener(listener);
                queue.setRedisTemplate(redisTemplate);
                queue.setKey(app.getAppName());
                listQ.add(queue);
                try {
                    queue.start();
                } catch (Exception e) {
                    log.error("启动redis异常!{}", e);
                }
            }
        }
    }

    public QueueListener getListener() {
        return listener;
    }

    public void setListener(QueueListener listener) {
        this.listener = listener;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }



    /**
     * 销毁redis连接
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        for (RedisQueue queue : listQ) {
            queue.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //创建队列
        createQueue();
        //启动定时添加队列
        addQueueTimer();
    }

    public void addQueueTimer() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        long delay = 60000;
        long initDelay = 0;
        executor.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("执行定时任务,检查是否有新应用!");
                        List<AppInfo> apps = appInfoDao.queryList(null);
                        if (null != apps) {
                            for (AppInfo app : apps) {
                                RedisQueue queue = new RedisQueue();
                                queue.setListener(listener);
                                queue.setRedisTemplate(redisTemplate);
                                queue.setKey(app.getAppName());
                                if (!listQ.contains(queue)) {
                                    listQ.add(queue);
                                    try {
                                        log.info("启动新的监听队列,appName={}", app.getAppName());
                                        queue.start();
                                    } catch (Exception e) {
                                        log.error("启动redis异常!{}", e);
                                    }
                                } else {
                                    queue = null;
                                }
                            }
                        }
                    }
                },
                initDelay,
                delay,
                TimeUnit.MILLISECONDS);
    }
}

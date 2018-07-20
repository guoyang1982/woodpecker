package com.letv.woodpecker.wpserver.redis;


import com.letv.woodpecker.wpserver.message.MessageBean;
import com.letv.woodpecker.wpserver.utils.MsgPackUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhusheng on 17/3/14.
 * @author zhusheng
 */
@Setter
@Slf4j
@SuppressWarnings(value = "unchecked")
public class RedisQueue<T>
{

    private RedisTemplate redisTemplate;
    private String key;
    /**
     * 最大阻塞的容量，超过容量将会导致清空旧数据
     */
    private byte[] rawKey;
    private RedisConnectionFactory factory;
    private RedisConnection connection;//for blocking
    private BoundListOperations<String, T> listOperations;//noblocking
    private Lock lock = new ReentrantLock();//基于底层IO阻塞考虑
    private QueueListener listener;//异步回调
    private Thread listenerThread;
    private boolean isClosed = false;
    //组塞10分钟
    private int  timeout = 600;
    //休息时间
    private int sleepTime = 1000;
    //获取为null 次数
    private int failCount = 30;




    /**
     * 启动监听
     * @throws Exception
     */
    void start() throws Exception
    {
        factory = redisTemplate.getConnectionFactory();
        connection = RedisConnectionUtils.getConnection(factory);
        rawKey = redisTemplate.getKeySerializer().serialize(key);
        listOperations = redisTemplate.boundListOps(key);
        if(listener != null){
            listenerThread = new ListenerThread();
           // listenerThread.setDaemon(false);
            listenerThread.start();
        }
    }


    /**
     * blocking
     * 队列尾部取数据
     * @return
     */
    public T takeFromTail(int timeout) throws InterruptedException{
        lock.lockInterruptibly();
        try{
            List<byte[]> results = connection.bRPop(timeout, rawKey);
            if(CollectionUtils.isEmpty(results)){
                return null;
            }
            return (T)redisTemplate.getValueSerializer().deserialize(results.get(1));
        }finally{
            lock.unlock();
        }
    }

    public T takeFromTail() throws InterruptedException{
        return takeFromTail(timeout);
    }

    /**
     * 从队列的头，插入
     */
    public void pushFromHead(T value){
        listOperations.leftPush(value);
    }

    public void pushFromTail(T value){
        listOperations.rightPush(value);
    }

    /**
     * noblocking
     * @return null if no item in queue
     */
    public T removeFromHead(){
        return listOperations.leftPop();
    }

    public T removeFromTail(){
        return listOperations.rightPop();
    }

    /**
     * blocking
     * remove and get first item from queue:BLPOP
     * @return
     */
    public T takeFromHead(int timeout) throws InterruptedException{
        lock.lockInterruptibly();
        try{
            List<byte[]> results = connection.bLPop(timeout, rawKey);
            if(CollectionUtils.isEmpty(results)){
                return null;
            }

            byte[] msg = results.get(1);
            MessageBean msgBean = MsgPackUtil.toObject(msg, MessageBean.class);
            log.info("message:"+new String(msg));
            return (T)msgBean;
            //return (T)redisTemplate.getValueSerializer().deserialize(results.get(1));
        }catch (Exception e){
            System.out.println(e);
            log.error("get the value fail!",e);
            return null;
        }
        finally{
            lock.unlock();
        }
    }

    public T takeFromHead() throws InterruptedException{
        return takeFromHead(timeout);
    }

    public void destroy() throws Exception {
        if(isClosed){
            return;
        }
        shutdown();
        RedisConnectionUtils.releaseConnection(connection, factory);
    }

    private void shutdown(){
        try{
            listenerThread.interrupt();
        }catch(Exception e){
            //注销失败
            log.error("注销失败!",e);
        }
    }



    class ListenerThread extends Thread {
        int count = 0;
        @Override
        public void run(){
            try{
                while(true){
                    //超过一定次数 休眠一段时间再去获取信息
                    if(count > failCount){
                        Thread.sleep(sleepTime);
                        count = 0;
                    }
                    log.info("开始消费!");
                    T value = takeFromHead();
                    //逐个执行
                    if(value != null){
                        log.info(((MessageBean)value).getAppName());

                        try{
                            listener.onMessage(value);
                        }catch(Exception e){
                            log.error("处理消息错误!",e);
                        }
                    }else{
                        count ++;
                    }
                }
            }catch(Exception e){
                //处理消息错误
                log.error("线程获取消息错误!",e);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof RedisQueue) {
            RedisQueue queue = (RedisQueue) obj;
            return queue.key.equals(this.key);
        }
        return super.equals(obj);
    }
}

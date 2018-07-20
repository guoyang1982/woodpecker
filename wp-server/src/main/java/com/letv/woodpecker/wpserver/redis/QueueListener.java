package com.letv.woodpecker.wpserver.redis;

/**
 * Created by zhusheng on 17/3/14.
 */
public interface QueueListener<T>
{
    void onMessage(T message);
}

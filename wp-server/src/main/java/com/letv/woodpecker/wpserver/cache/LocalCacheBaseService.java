package com.letv.woodpecker.wpserver.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhusheng on 17/2/15.
 */
public abstract class LocalCacheBaseService<K,V> {
    private LoadingCache<K,V> cache;

    public LocalCacheBaseService(){
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception {
                        return loadData(k);
                    }
                });
    }

    public LocalCacheBaseService(long time, TimeUnit timeUnit){
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(time, timeUnit)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception {
                        return loadData(k);
                    }
                });
    }

    protected abstract V loadData(K k);

    public void refresh(K k){
        cache.refresh(k);
    }

    public V getCache (K k){
        return cache.getUnchecked(k);
    }

    public V get(K k) throws ExecutionException {
        return cache.get(k);
    }
}

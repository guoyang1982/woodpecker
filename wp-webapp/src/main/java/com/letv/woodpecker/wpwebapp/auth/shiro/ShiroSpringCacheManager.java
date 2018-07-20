package com.letv.woodpecker.wpwebapp.auth.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;


/**
 * 使用spring-cache作为shiro的缓存管理器 Ehcache Redis Guava
 * 本次使用shiro自带的缓存管理器
 * TODO 后续优化可能会采用其他缓存机制
 * @author meijunjie 2018/7/3
 */
@Slf4j
public class ShiroSpringCacheManager implements CacheManager, Destroyable {

    private org.springframework.cache.CacheManager cacheManager;

    public ShiroSpringCacheManager() {
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return null;
    }

    @Override
    public void destroy() throws Exception {

    }
}

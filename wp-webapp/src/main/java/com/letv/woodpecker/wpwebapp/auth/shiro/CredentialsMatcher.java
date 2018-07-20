package com.letv.woodpecker.wpwebapp.auth.shiro;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 凭证验证
 * @author meijunjie @date 2018/7/6
 */
@Slf4j
public class CredentialsMatcher extends HashedCredentialsMatcher implements InitializingBean {

    /** 重试缓存，记录用户重试密码次数*/
    private static final String DEFAULT_CACHE_NAME = "retryLimitCache";

    /** 缓存管理器*/
    private CacheManager cacheManager;

    @Getter
    @Setter
    private String retryLimitCacheName;

    /** 密码重试缓存*/
    private Cache<String, AtomicInteger> passwordRetryCache;

    /** 密码生成器*/
    @Getter
    @Setter
    private PasswordHash passwordHash;

    public CredentialsMatcher(CacheManager cacheManager){
        this.cacheManager = cacheManager;
        this.retryLimitCacheName = DEFAULT_CACHE_NAME;
    }

    /**
     * 对输入的用户和密码进行校验
     * @param token          用户携带的凭证
     * @param info           认证身份信息
     * @return               true / false
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        // 获取凭证信息 wp-web采用用户密码模式进行登录认证
        String userName = (String) token.getPrincipal();
        // 获取当前用户重试密码计数
        AtomicInteger retryCount = passwordRetryCache.get(userName);
        if(retryCount == null){
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(userName,retryCount);
        }
        // 如果用户连续输入10次密码则锁定30分钟
        if(retryCount.incrementAndGet() > 10){
            throw new ExcessiveAttemptsException("用户名: " + userName + " 密码连续输入错误十次，锁定30分钟！");
        }else {
            // 记录用户重试密码次数
            passwordRetryCache.put(userName,retryCount);
        }
        boolean matches = super.doCredentialsMatch(token,info);
        if(matches){
            // 密码校验成功，移除重试缓存
            passwordRetryCache.remove(userName);
        }
        return matches;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(passwordHash, "Password must not be null!");
        // 设置加密算法
        super.setHashAlgorithmName(passwordHash.getAlgorithmName());
        // 设置加密次数
        super.setHashIterations(passwordHash.getHashIterations());
        // 获取重试缓存
        this.passwordRetryCache = cacheManager.getCache(retryLimitCacheName);

    }
}

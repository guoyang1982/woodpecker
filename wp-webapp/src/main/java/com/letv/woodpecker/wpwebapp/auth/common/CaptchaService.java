package com.letv.woodpecker.wpwebapp.auth.common;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 验证码服务
 * @author meijunjie 2018/7/3
 */
@Slf4j
@Setter
@Getter
public class CaptchaService implements InitializingBean {

    private static final String DEFAULT_COOKIE_NAME = "wp_captcha";
    private static final String DEFAULT_CAPTCHA_NAME = "wpCaptchaCache";
    /** cookie超时默认为Session的会话状态*/
    private static final int DEFAULT_MAX_AGE = -1;

    /** 内部维护了一个并发map*/
    private CacheManager cacheManager;
    private String cacheName;
    private String cookieName;

    private Cache<String, String> wpCaptchaCache;

    public CaptchaService() {
        this.cacheName = DEFAULT_CAPTCHA_NAME;
        this.cookieName = DEFAULT_COOKIE_NAME;
    }

    public CaptchaService(CacheManager cacheManager){
        this();
        this.cacheManager = cacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cacheManager, "cacheManager must not be null!");
        Assert.hasText(cacheName, "cacheName must not be empty!");
        Assert.hasText(cookieName, "cookieName must not be empty!");
        this.wpCaptchaCache = cacheManager.getCache(cacheName);
    }

    /**
     * 生成验证码
     * @param request                 HttpServletRequest
     * @param response                HttpServletResponse
     */
    public void generateCaptchaCode(HttpServletRequest request, HttpServletResponse response) {
        String cookieValue = WebUtils.getCookieValue(request, cookieName);
        boolean hasCookie = true;
        if(StringUtils.isBlank(cookieValue)){
            hasCookie = false;
            cookieValue = UUID.randomUUID().toString();
        }
        String captchaCode = CaptchaUtils.generateCode().toUpperCase();
        if(!hasCookie){
            WebUtils.setCookie(response, cookieName, cookieValue, DEFAULT_MAX_AGE);
        }
        // 生成验证码
        CaptchaUtils.generate(response, captchaCode);
        wpCaptchaCache.put(cookieValue, captchaCode);
    }

    /**
     * 验证码仅能验证一次 验证后立即删除
     * @param request                 HttpServletRequest
     * @param response                HttpServletResponse
     * @param userInputCaptcha        用户输入的验证码
     * @return                        true/false
     */
    public boolean validate(HttpServletRequest request, HttpServletResponse response, String userInputCaptcha){
        if(log.isDebugEnabled()){
            log.debug("validate captcha userInputCaptcha is {}",userInputCaptcha);
        }
        // 获取cookie值
        String cookieValue = WebUtils.getCookieValue(request, cookieName);
        if(StringUtils.isBlank(cookieValue)){
            return false;
        }

        // 从缓存中获取验证码
        String captchaCode = wpCaptchaCache.get(cookieValue);
        if(StringUtils.isBlank(captchaCode)){
            return false;
        }

        userInputCaptcha = userInputCaptcha.toUpperCase();
        // 判断当前的验证码与用户输入的是否一致
        boolean result = userInputCaptcha.endsWith(captchaCode);
        if(result){
            wpCaptchaCache.remove(cookieValue);
            WebUtils.removeCookie(response, cookieName);
        }
        return result;
    }
}

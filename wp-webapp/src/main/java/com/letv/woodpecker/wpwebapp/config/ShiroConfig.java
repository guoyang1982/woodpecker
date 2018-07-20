package com.letv.woodpecker.wpwebapp.config;

import com.letv.woodpecker.wpwebapp.auth.common.CaptchaService;
import com.letv.woodpecker.wpwebapp.auth.shiro.CredentialsMatcher;
import com.letv.woodpecker.wpwebapp.auth.shiro.PasswordHash;
import com.letv.woodpecker.wpwebapp.auth.shiro.ShiroAjaxSessionFilter;
import com.letv.woodpecker.wpwebapp.auth.shiro.WpWebRealm;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro相关配置
 * @author meijunjie 2018/7/3
 */
@Configuration
public class ShiroConfig {

    /**
     * 缓存管理器
     * @return    使用shiro自带的内存缓存
     */
    @Bean
    public AbstractCacheManager cacheManager(){
        return new MemoryConstrainedCacheManager();
    }

    /**
     * 验证码服务
     * @return     CaptchaService
     */
    @Bean
    public CaptchaService captchaService(){
        CaptchaService captchaService = new CaptchaService();
        captchaService.setCacheManager(cacheManager());
        return captchaService;
    }

    /**
     * 自定义Realm
     * @return     WpWebRealm用于认证及授权
     */
    @Bean
    public WpWebRealm wpWebRealm(){
        // 注入缓存管理器 凭证匹配器
        WpWebRealm wpWebRealm = new WpWebRealm(cacheManager(),credentialsMatcher());
        // 启用认证缓存
        wpWebRealm.setAuthenticationCachingEnabled(true);
        // 缓存AuthenticationInfo的缓存名称
        wpWebRealm.setAuthenticationCacheName("authenticationCache");
        // 缓存AuthorizationInfo的缓存名称
        wpWebRealm.setAuthorizationCacheName("authorizationCache");
        return wpWebRealm;
    }

    /**
     * 记住密码Cookie
     * @return      SimpleCookie
     */
    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(7 * 24 * 60 * 60);
        return simpleCookie;
    }

    /**
     * rememberMe管理器
     * @return      RememberMeManager
     */
    @Bean
    public CookieRememberMeManager cookieRememberMeManager(){
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setCipherKey(Base64.decode("5aaC5qKm5oqA5pyvAAAAAA=="));
        rememberMeManager.setCookie(simpleCookie());
        return rememberMeManager;
    }
    /**
     * 默认的安全管理器
     * @return      DefaultWebSecurityManager
     */
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(){
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        webSecurityManager.setRealm(wpWebRealm());
        webSecurityManager.setCacheManager(cacheManager());
        webSecurityManager.setRememberMeManager(cookieRememberMeManager());
        webSecurityManager.setSessionManager(defaultWebSessionManager());
        return webSecurityManager;
    }

    /**
     * 操作session的DAO 用与会话的增删改查
     * @return        EnterpriseCacheSessionDao
     */
    @Bean
    public EnterpriseCacheSessionDAO cacheSessionDAO(){
        EnterpriseCacheSessionDAO cacheSessionDAO = new EnterpriseCacheSessionDAO();
        cacheSessionDAO.setActiveSessionsCacheName("activeSessionCache");
        cacheSessionDAO.setCacheManager(cacheManager());
        return cacheSessionDAO;
    }
    /**
     * 默认的会话理器
     * @return      DefaultWebSessionManager
     */
    @Bean
    public DefaultWebSessionManager defaultWebSessionManager(){
        DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
        // 设置全局会话超时时间 半小时
        webSessionManager.setGlobalSessionTimeout(30 * 60 * 1000);
        webSessionManager.setSessionIdUrlRewritingEnabled(false);
        webSessionManager.setSessionDAO(cacheSessionDAO());
        return webSessionManager;
    }

    /**
     * shiro密码处理
     * @return       PasswordHash
     */
    @Bean
    public PasswordHash passwordHash(){
        PasswordHash passwordHash = new PasswordHash();
        passwordHash.setAlgorithmName("md5");
        passwordHash.setHashIterations(1);
        return passwordHash;
    }


    /**
     * 用户认证凭证匹配器
     * @return      CredentialsMatcher
     */
    @Bean
    public CredentialsMatcher credentialsMatcher(){
        CredentialsMatcher credentialsMatcher = new CredentialsMatcher(cacheManager());
        credentialsMatcher.setRetryLimitCacheName("30m");
        credentialsMatcher.setPasswordHash(passwordHash());
        return credentialsMatcher;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shirofilter");
        filterRegistrationBean.setFilter(proxy);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST,DispatcherType.FORWARD);
        filterRegistrationBean.setName("shirofilter");
        return filterRegistrationBean;
    }
    /**
     * shiro过滤器
     * @return       shiro过滤器
     */
    @Bean("shirofilter")
    public ShiroFilterFactoryBean shiroFilter() throws Exception {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager());
        // 登录访问的url
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        Map<String,Filter> filterMap = new LinkedHashMap<>(1);
        filterMap.put("user",shiroAjaxSessionFilter());
        // 登录成功后访问的url
        Map<String,String> urls = new LinkedHashMap<>(4);
        urls.put("/captcha.jpg","anon");
        urls.put("/static/**","anon");
        urls.put("/js/**","anon");
        urls.put("/h2-console","anon");
        urls.put("/register","anon");
        urls.put("/login","anon");
        urls.put("/woodpecker/register","anon");
        urls.put("/woodpecker/login","anon");
        urls.put("/woodpecker/user/saveUser","anon");
        urls.put("/woodpecker/user/editUser","anon");
        urls.put("/**","user");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(urls);
        return shiroFilterFactoryBean;
    }

    /**
     * Controller 授权shiro拦截器注解生效
     * @return       AuthorizationAttributeSourceAdvisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public ShiroAjaxSessionFilter shiroAjaxSessionFilter(){
        return new ShiroAjaxSessionFilter();
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(){
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        methodInvokingFactoryBean.setArguments(new DefaultWebSecurityManager[]{defaultWebSecurityManager()});
        return methodInvokingFactoryBean;
    }

}

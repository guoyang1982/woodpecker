package com.letv.woodpecker.wpwebapp.config;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.letv.woodpecker.wpwebapp.auth.interceptor.UserInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by meijunjie on 2017/9/29.
 * @author meijunjie
 */
@Configuration
@EnableWebMvc
public class WpWebConfiguration extends WebMvcConfigurerAdapter
{


    /**
     * 访问拦截，鉴权
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new UserInfoInterceptor()).addPathPatterns("/","/woodpecker/**");
        super.addInterceptors(registry);
    }


    /**
     * 静态资源拦截
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/META-INF/resources/static/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/META-INF/resources/js/");
        super.addResourceHandlers(registry);
    }

    /**
     * 配置视图解析
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry)
    {
        registry.jsp("/WEB-INF/views/",".jsp");
        registry.jsp("/WEB-INF/layouts",".jsp");
        registry.enableContentNegotiation(new FastJsonJsonView());
        super.configureViewResolvers(registry);
    }


    /**
     * 媒体视图支持
     * @param configurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
    {
        Map<String, MediaType> mediaTypeMap = new HashMap<>();
        mediaTypeMap.put("json",MediaType.APPLICATION_JSON_UTF8);
        mediaTypeMap.put("xml",MediaType.APPLICATION_XHTML_XML);
        configurer.mediaTypes(mediaTypeMap);
        super.configureContentNegotiation(configurer);

    }

}

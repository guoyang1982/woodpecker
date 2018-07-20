package com.letv.woodpecker.wpwebapp.config;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by meijunjie on 2017/9/29.
 * @author meijunjie
 */

@Configuration
public class FilterConfig
{

    @Bean
    public FilterRegistrationBean someFilterRegistration()
    {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SiteMeshFilter());
        registration.addUrlPatterns("/woodpecker/*","/","/index");
        return registration;
    }
}

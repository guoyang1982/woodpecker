package com.letv.woodpecker.wpwebapp.config;


import org.jfaster.mango.datasource.SimpleDataSourceFactory;
import org.jfaster.mango.operator.Mango;
import org.jfaster.mango.plugin.spring.MangoDaoScanner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Mango的配置类
 * @author meijunjie @date 2018/7/4
 */
@Configuration
public class MangoConfig  {
    // 获取数据源
    @Bean(value= "wp_user")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }
    // 数据源工厂
    @Bean
    public SimpleDataSourceFactory simpleDataSourceFactory(){
        SimpleDataSourceFactory simpleDataSourceFactory = new SimpleDataSourceFactory();
        simpleDataSourceFactory.setDataSource(dataSource());
        return simpleDataSourceFactory;
    }

    // 配置mango对象
    @Bean
    public Mango mango(){
        Mango mango = Mango.newInstance();
        mango.setDataSourceFactory(simpleDataSourceFactory());
        return mango;
    }

    // 配置扫描包
    @Bean
    public MangoDaoScanner mangoDaoScanner() {
        MangoDaoScanner mangoDaoScanner = new MangoDaoScanner();
        List<String> packages = new ArrayList<>(1);
        packages.add("com.letv.woodpecker.wpwebapp.dao");
        mangoDaoScanner.setPackages(packages);
        return mangoDaoScanner;
    }

}

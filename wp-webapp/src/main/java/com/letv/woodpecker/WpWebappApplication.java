package com.letv.woodpecker;

import com.letv.woodpecker.wpwebapp.itf.UserService;
import org.jfaster.mango.plugin.stats.MangoStatServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * Spring Boot启动主类
 * @author meijunjie
 */
@SpringBootApplication
public class WpWebappApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(WpWebappApplication.class, args);

	}
}

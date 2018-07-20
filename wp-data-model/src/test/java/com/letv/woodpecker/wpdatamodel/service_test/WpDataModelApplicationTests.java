package com.letv.woodpecker.wpdatamodel.service_test;

import com.letv.woodpecker.wpdatamodel.model.AppInfo;
import com.letv.woodpecker.wpdatamodel.service.ApplicationService;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WpDataModelApplicationTests {

	@Resource
	private ApplicationService applicationService;

	@Test
	@Ignore
	public void applicationServiceTest()
	{
		AppInfo appInfo = new AppInfo();
		appInfo.setAppName("");
		appInfo.setIp("127.0.0.1");
		appInfo.setUserId("");

		applicationService.saveAppInfo(appInfo);
	}

}

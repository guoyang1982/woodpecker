package com.letv.woodpecker.wpdatamodel.dao_test;

import com.letv.woodpecker.wpdatamodel.dao.AlarmConfigDao;
import com.letv.woodpecker.wpdatamodel.dao.AppInfoDao;
import com.letv.woodpecker.wpdatamodel.dao.ExceptionInfoDao;
import com.letv.woodpecker.wpdatamodel.model.AlarmConfig;
import com.letv.woodpecker.wpdatamodel.model.AppInfo;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest
public class WpDataModelApplicationTests {

	@Autowired
	private AlarmConfigDao alarmConfigDao;
	@Autowired
	private AppInfoDao appInfoDao;
	@Autowired
	private ExceptionInfoDao exceptionInfoDao;



	@Test
	@Ignore
	public void saveConfig(){
		AlarmConfig alarmConfig = new AlarmConfig();
		alarmConfig.setIp("each");
		alarmConfig.setAppName("openecodasdasd");
		alarmConfig.setExceptionType("each");
		alarmConfig.setEmail("");
		alarmConfig.setThreshold(1);
		alarmConfigDao.save(alarmConfig);

	}

	@Test
	@Ignore
	public void addApp(){
		AppInfo appInfo = new AppInfo();
		appInfo.setUserId("xxx");
		appInfo.setAppName("");
		appInfo.setIp("127.0.0.1");
		appInfoDao.save(appInfo);
	}

	@Test
	@Ignore
	public void groupTest(){
		String[] fields = new String[]{"appName","exceptionType"};
		BasicDBList dbList = exceptionInfoDao.mongoGroup(null,null,fields);
		for(int i=0; i<dbList.size(); i++){
			DBObject dbObject = (DBObject) dbList.get(i);

		}
	}

}

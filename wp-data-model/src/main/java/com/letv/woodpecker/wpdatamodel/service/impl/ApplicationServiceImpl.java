package com.letv.woodpecker.wpdatamodel.service.impl;

import com.letv.woodpecker.wpdatamodel.dao.AppInfoDao;
import com.letv.woodpecker.wpdatamodel.model.AppInfo;
import com.letv.woodpecker.wpdatamodel.service.ApplicationService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhusheng on 17/3/29.
 */
@Service("applicationService")
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private AppInfoDao appInfoDao;

    @Override
    public List<AppInfo> queryAllApps(String userId, int pageStart, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        if(pageSize!=Integer.MAX_VALUE){
            query.skip(pageStart).limit(pageSize);
        }
        return appInfoDao.queryList(query);
    }

    @Override
    public long queryAppsCount(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return appInfoDao.getCount(query);
    }

    @Override
    public void saveAppInfo(AppInfo appInfo) {
        appInfoDao.save(appInfo);
    }

    @Override
    public void deleteApp(String userId, String appName) {
        appInfoDao.deleteApp(userId,appName);
    }

    @Override
    public AppInfo getIpByAppName(String userId, String appName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("appName").is(appName));
        return appInfoDao.queryOne(query);
    }
}

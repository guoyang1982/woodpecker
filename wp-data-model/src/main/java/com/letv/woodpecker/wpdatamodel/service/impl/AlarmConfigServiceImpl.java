package com.letv.woodpecker.wpdatamodel.service.impl;


import com.letv.woodpecker.wpdatamodel.dao.AlarmConfigDao;
import com.letv.woodpecker.wpdatamodel.model.AlarmConfig;
import com.letv.woodpecker.wpdatamodel.service.AlarmConfigService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhusheng on 17/3/29.
 */
@Service("alarmConfigService")
public class AlarmConfigServiceImpl implements AlarmConfigService {

    @Resource
    private AlarmConfigDao alarmConfigDao;

    @Override
    public List<AlarmConfig> queryAlarmConfigs(String userId, int pageStart, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").exists(true));
        if(pageSize != Integer.MAX_VALUE){
            query.skip(pageStart).limit(pageSize);
        }

        return alarmConfigDao.queryList(query);
    }

    @Override
    public AlarmConfig queryAlarmConfig(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return alarmConfigDao.queryOne(query);
    }

    @Override
    public long getConfigsCount(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").exists(true));
        return alarmConfigDao.getCount(query);
    }

    @Override
    public long getConfigsCountByAppNames(List<String> appNames) {
        Query  query = new Query();
        query.addCriteria(Criteria.where("appName").in(appNames));
        return alarmConfigDao.getCount(query);
    }

    @Override
    public void saveAlarmConfig(AlarmConfig alarmConfig) {
        alarmConfigDao.save(alarmConfig);
    }

    @Override
    public void deleteConfig(AlarmConfig config) {
        alarmConfigDao.deleteConfig(config);
    }

    @Override
    public void modifyAlarmConfig(AlarmConfig config) {
        alarmConfigDao.modifyById(config);
    }

    @Override
    public List<AlarmConfig> queryListByAppName(String appName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        return alarmConfigDao.queryList(query);
    }
}

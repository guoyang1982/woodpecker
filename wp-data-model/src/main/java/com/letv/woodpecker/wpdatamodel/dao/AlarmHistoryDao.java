package com.letv.woodpecker.wpdatamodel.dao;


import com.letv.woodpecker.wpdatamodel.model.AlarmHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by zhusheng on 17/3/16.
 */
@Repository
public class AlarmHistoryDao extends MongoDao<AlarmHistory>{
    @Override
    public Class getEntityClass() {
        return AlarmHistory.class;
    }

    public String getLatestAlarmTime(String appName,String ip,String exceptionType){
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        query.addCriteria(Criteria.where("ip").is(ip));
        query.addCriteria(Criteria.where("exceptionType").is(exceptionType));
        query.with(new Sort(Sort.Direction.DESC,"alarmTime")).limit(1);
        AlarmHistory history = queryOne(query);
        if(history == null){
            return "";
        }
        return history.getAlarmTime();
    }
}

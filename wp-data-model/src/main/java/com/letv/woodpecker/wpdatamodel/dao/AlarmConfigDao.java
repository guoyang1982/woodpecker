package com.letv.woodpecker.wpdatamodel.dao;

import com.letv.woodpecker.wpdatamodel.model.AlarmConfig;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhusheng on 17/3/16.
 */
@Repository
public class AlarmConfigDao extends MongoDao<AlarmConfig> {
    @Override
    public Class getEntityClass() {
        return AlarmConfig.class;
    }

    public List<AlarmConfig> queryList(String appName,String ip,String exceptionType){
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        query.addCriteria(Criteria.where("ip").in(ip,"all","each"));
        query.addCriteria(Criteria.where("exceptionType").in(exceptionType,"all","each"));
        return queryList(query);
    }

    public void deleteConfig(AlarmConfig config){
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(config.getUserId()));
        query.addCriteria(Criteria.where("appName").is(config.getAppName()));
        query.addCriteria(Criteria.where("ip").is(config.getIp()));
        query.addCriteria(Criteria.where("exceptionType").is(config.getExceptionType()));
        delete(query);
    }

    public void modifyById(AlarmConfig config){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(config.get_id()));
        Update update = Update.update("ruleId", config.getRuleId())
                .set("threshold", config.getThreshold()).set("alarmFrequency",config.getAlarmFrequency())
                .set("email",config.getEmail()).set("phoneNum",config.getPhoneNum())
                .set("corpid",config.getCorpid()).set("secret",config.getSecret())
                .set("toparty",config.getToparty()).set("agentid",config.getAgentid());
        modify(query,update);
    }
}

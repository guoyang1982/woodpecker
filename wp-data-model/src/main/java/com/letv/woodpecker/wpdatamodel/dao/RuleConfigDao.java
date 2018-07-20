package com.letv.woodpecker.wpdatamodel.dao;

import com.letv.woodpecker.wpdatamodel.model.AlarmConfig;
import com.letv.woodpecker.wpdatamodel.model.RuleConfig;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/7/12 下午5:56
 */
@Repository
public class RuleConfigDao extends MongoDao<RuleConfig> {
    @Override
    public Class getEntityClass() {
        return RuleConfig.class;
    }

    public List<RuleConfig> queryList(String userName, int pageStart, int pageSize) {
        Query query = new Query();

        query.addCriteria(Criteria.where("userName").exists(true));
        query.skip(pageStart).limit(pageSize);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"mTime")));

        return queryList(query);
    }

    /**
     * 按应用名查询相应的规则,支持分页查询
     * @param appName         应用名
     * @param pageStart       分页起始值
     * @param pageSize        分页值
     * @return                规则配置
     */
    public List<RuleConfig> queryListByAppName(String appName, int pageStart, int pageSize){
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        query.skip(pageStart).limit(pageSize);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"mTime")));
        return queryList(query);
    }


    public void deleteConfig(RuleConfig config) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(config.get_id()));
        delete(query);
    }

    public void modifyById(RuleConfig config) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(config.get_id()));
        Update update = Update.update("ruleName", config.getRuleName())
                .set("ruleDesc", config.getRuleDesc()).set("ruleConfig",config.getRuleConfig())
                .set("appName",config.getAppName()).set("mTime",config.getMTime());
        modify(query,update);
    }

    public List<RuleConfig> queryList(String appName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"mTime")));
        return queryList(query);
    }
}

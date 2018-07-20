package com.letv.woodpecker.wpdatamodel.service.impl;

import com.letv.woodpecker.wpdatamodel.dao.RuleConfigDao;
import com.letv.woodpecker.wpdatamodel.model.RuleConfig;
import com.letv.woodpecker.wpdatamodel.service.RuleConfigService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/7/12 下午5:55
 */
@Service("ruleConfigService")
public class RuleConfigServiceImpl implements RuleConfigService {
    @Resource
    RuleConfigDao ruleConfigDao;

    @Override
    public List<RuleConfig> queryRuleConfigs(String userId, int pageStart, int pageSize) {
        return ruleConfigDao.queryList(userId,pageStart,pageSize);
    }

    @Override
    public RuleConfig queryRuleConfig(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return ruleConfigDao.queryOne(query);
    }

    @Override
    public long getConfigsCount(String userId) {
        Query query = new Query();

        query.addCriteria(Criteria.where("userName").exists(true));
        return ruleConfigDao.getCount(query);
    }


    @Override
    public void deleteConfig(RuleConfig config) {
        ruleConfigDao.deleteConfig(config);
    }

    @Override
    public void saveRuleConfig(RuleConfig ruleConfig) {
        ruleConfigDao.save(ruleConfig);
    }

    @Override
    public void modifyRuleConfig(RuleConfig ruleConfig) {

        ruleConfigDao.modifyById(ruleConfig);
    }

    @Override
    public List<RuleConfig> queryRuleConfigs(String appName) {
        return ruleConfigDao.queryList(appName);
    }
    @Override
    public long getConfigsCountByAppName(List<String> appNames) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").in(appNames));
        return ruleConfigDao.getCount(query);
    }
}

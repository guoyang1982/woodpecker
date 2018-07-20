package com.letv.woodpecker.wpdatamodel.service;

import com.letv.woodpecker.wpdatamodel.model.RuleConfig;

import java.util.List;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/7/12 下午5:53
 */
public interface RuleConfigService {

    List<RuleConfig> queryRuleConfigs(String userId, int pageStart, int pageSize);

    RuleConfig queryRuleConfig(String id);

    long getConfigsCount(String userId);

    void deleteConfig(RuleConfig config);

    void saveRuleConfig(RuleConfig ruleConfig);

    void modifyRuleConfig(RuleConfig ruleConfig);

    List<RuleConfig> queryRuleConfigs(String appName);
    long getConfigsCountByAppName(List<String> appNames);


}

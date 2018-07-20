package com.letv.woodpecker.wpdatamodel.service;


import com.letv.woodpecker.wpdatamodel.model.AlarmConfig;

import java.util.List;

/**
 * Created by zhusheng on 17/3/29.
 */
public interface AlarmConfigService {

    List<AlarmConfig> queryAlarmConfigs(String userId, int pageStart, int pageSize);

    AlarmConfig queryAlarmConfig(String id);

    long getConfigsCount(String userId);

    long getConfigsCountByAppNames(List<String> appNames);

    void saveAlarmConfig(AlarmConfig alarmConfig);

    void deleteConfig(AlarmConfig config);

    void modifyAlarmConfig(AlarmConfig config);

    List<AlarmConfig> queryListByAppName(String appName);
}

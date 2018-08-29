package com.letv.woodpecker.wpdatamodel.service;


import com.letv.woodpecker.wpdatamodel.model.AlarmConfig;

import java.util.List;

/**
 * Created by zhusheng on 17/3/29.
 */
public interface AlarmConfigService {

    List<AlarmConfig> queryAlarmConfigs(String userId, String configType, int pageStart, int pageSize);

    AlarmConfig queryAlarmConfig(String id);

    long getConfigsCount(String userId, String configType);

    long getConfigsCountByAppNames(List<String> appNames, String configType);

    void saveAlarmConfig(AlarmConfig alarmConfig);

    void deleteConfig(AlarmConfig config);

    void modifyAlarmConfig(AlarmConfig config);

    List<AlarmConfig> queryListByAppName(String appName, String configType);
}

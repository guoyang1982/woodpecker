package com.letv.woodpecker.wpdatamodel.service;



import com.letv.woodpecker.wpdatamodel.model.AppInfo;

import java.util.List;

/**
 * Created by zhusheng on 17/3/29.
 */
public interface ApplicationService {
    List<AppInfo> queryAllApps(String userId, int pageStart, int pageSize);
    long queryAppsCount(String userId);
    void saveAppInfo(AppInfo appInfo);
    void deleteApp(String userId, String appName);
    AppInfo getIpByAppName(String userId, String appName);
}

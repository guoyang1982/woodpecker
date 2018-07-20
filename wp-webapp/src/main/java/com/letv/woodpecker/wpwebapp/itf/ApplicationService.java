package com.letv.woodpecker.wpwebapp.itf;




import com.letv.woodpecker.wpwebapp.entity.AppInfo;
import com.letv.woodpecker.wpwebapp.entity.UserApp;

import java.util.List;

/**
 * @author meijunjie @date 2018/07/12
 */
public interface ApplicationService {
    List<AppInfo> queryAllApps(String userId, int pageStart, int pageSize);

    Integer queryAppsCount(String userId);

    void saveAppInfo(AppInfo appInfo);

    void deleteApp(String userId, String appName);

    AppInfo getIpByAppName(String userId, String appName);

    AppInfo getByAppName(String appName);

    void updateAppInfo(AppInfo appInfo);

    void saveUserAppInfos(UserApp userApp);

    AppInfo getByAppId(String appId);
}

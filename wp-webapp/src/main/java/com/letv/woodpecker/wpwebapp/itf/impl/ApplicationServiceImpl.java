package com.letv.woodpecker.wpwebapp.itf.impl;

import com.letv.woodpecker.wpwebapp.constants.RoleIds;
import com.letv.woodpecker.wpwebapp.dao.AppDao;
import com.letv.woodpecker.wpwebapp.dao.UserAppDao;
import com.letv.woodpecker.wpwebapp.dao.UserDao;
import com.letv.woodpecker.wpwebapp.entity.AppInfo;
import com.letv.woodpecker.wpwebapp.entity.User;
import com.letv.woodpecker.wpwebapp.entity.UserApp;
import com.letv.woodpecker.wpwebapp.itf.ApplicationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO 替换appDao实现，采用mangoDB存储应用信息
 *
 * @author meijunjie @date 2018/7/12
 */
@Service("applicationServiceWeb")
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private UserAppDao userAppDao;
    @Resource
    private AppDao appDao;
    @Resource
    private UserDao userDao;

    @Override
    public List<AppInfo> queryAllApps(String userId, int pageStart, int pageSize) {
        User user = userDao.getByLoginName(userId);
        // 当前用户是超级管理员
        if(user.getUserRole() == RoleIds.SUPER_ADMINER){
            return appDao.queryAll(null, pageStart, pageSize);
        }
        // 当前用户非超级管理员查询用户应用映射表获取其名下的应用信息
        Long userIdTemp = user.getId();
        // 对用户应用映射表做分页查询
        List<UserApp> userApps = userAppDao.queryAllByUserId(userIdTemp, pageStart, pageSize);
        List<AppInfo> result = new ArrayList<>(4);
        for(UserApp userApp : userApps){
            // 查询真正的应用详情,按主键查询
            result.add(appDao.queryByAppId(userApp.getAppId()));
        }
        return result;
    }

    @Override
    public Integer queryAppsCount(String userId) {
        User user = userDao.getByLoginName(userId);
        if(user != null){
            // 判断当前用户角色
            // 1.超级管理员查询全部应用信息
            if(user.getUserRole() == RoleIds.SUPER_ADMINER){
                List<AppInfo> appInfos = appDao.queryAll();
                return appInfos.size();
            }else {
                //2.非管理员权限，只能看到自己名下的应用
                return userAppDao.queryCountByUserId(user.getId());
            }
        }
        return 0;
    }
    @Override
    public void saveAppInfo(AppInfo appInfo) {
        appDao.insertApp(appInfo);
    }

    @Override
    public void deleteApp(String userId, String appName) {
        User user = userDao.getByLoginName(userId);
        // 只有超级管理员具备删除应用的权限
        String appId = appDao.queryByAppName(appName).get_id();
        if(user.getUserRole() == RoleIds.SUPER_ADMINER) {
            // 替换成mongo的实现
            appDao.deleteApp(appName);
            // 删除用户应用表应用 t_user_app中的映射关系
            userAppDao.delete(appId);
        }
    }

    @Override
    public AppInfo getIpByAppName(String userId, String appName) {
        User user = userDao.getByLoginName(userId);
        AppInfo appInfo = appDao.queryByAppName(appName);
        return appInfo;
    }

    @Override
    public AppInfo getByAppName(String appName) {
        return appDao.queryByAppName(appName);
    }

    @Override
    public void updateAppInfo(AppInfo appInfo) {
        appDao.updateApp(appInfo);
    }

    @Override
    public void saveUserAppInfos(UserApp userApp) {
        userAppDao.insertUserAppMapping(userApp);
    }


    @Override
    public AppInfo getByAppId(String appId) {
        return appDao.queryByAppId(appId);
    }
}

package com.letv.woodpecker.wpdatamodel.dao;

import com.letv.woodpecker.wpdatamodel.model.AppInfo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhusheng on 17/3/16.
 */
@Repository
public class AppInfoDao extends MongoDao<AppInfo>{

    @Override
    public Class getEntityClass() {
        return AppInfo.class;
    }

    public void deleteApp(String userId,String appName){
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("appName").is(appName));
        delete(query);
    }

    public List<AppInfo> queryAllAppInUser(String userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return queryList(query);
    }
}

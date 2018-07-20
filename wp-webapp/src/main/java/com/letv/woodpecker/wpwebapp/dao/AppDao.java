package com.letv.woodpecker.wpwebapp.dao;

import com.letv.woodpecker.wpdatamodel.dao.MongoDao;
import com.letv.woodpecker.wpwebapp.entity.AppInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用信息dao
 * @author meijunjie @date 2018/7/12
 */
@Repository
public class AppDao extends MongoDao<AppInfo>{
    /**
     * 保存应用信息
     * @param appInfo    应用信息
     */
    public void insertApp(AppInfo appInfo){
        super.save(appInfo);
    }

    /**
     * 按应用进行更新
     * @param appInfo    应用信息
     */
    public void updateApp(AppInfo appInfo){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(appInfo.get_id()));
        Update update = Update.update("appName",appInfo.getAppName())
                              .set("ip",appInfo.getIp())
                              .set("modifyTime",appInfo.getModifyTime());
        modify(query,update);
    }


    /**
     * 按应用名删除
     * @param appName    应用名
     */
    public void deleteApp(String appName){
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        super.delete(query);
    }

    /**
     * 查询所有应用,支持分页查询
     * @param creator    应用创建者
     * @param pageStart  分页起始值
     * @param pageSize   分页大小
     * @return           所有应用信息
     */
    public List<AppInfo> queryAll(String creator, Integer pageStart, Integer pageSize){
        Query query = new Query();
        if(creator != null) {
            query.addCriteria(Criteria.where("creator").is(creator));
        }else {
            query.addCriteria(Criteria.where("_id").exists(true));
        }
        query.skip(pageStart).limit(pageSize);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"modifyTime")));
        return queryList(query);
    }

    /**
     * 查询所有应用
     * @return          所有应用信息
     */
    public List<AppInfo> queryAll(){
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(1));
        return queryList(query);
    }

    /**
     * 按应用名查询应用详情
     * @param appName    应用名
     * @return           应用信息
     */
    public AppInfo queryByAppName(String appName){
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        return super.queryOne(query);
    }


    /**
     * 按应用ID查询应用详情
     * @param appId      应用Id
     * @return           应用详情
     */

    public AppInfo queryByAppId(String appId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(appId));
        return super.queryOne(query);
    }

    @Override
    public Class getEntityClass() {
        return AppInfo.class;
    }

}

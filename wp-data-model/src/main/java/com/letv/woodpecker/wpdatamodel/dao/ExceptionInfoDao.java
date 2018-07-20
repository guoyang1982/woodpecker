package com.letv.woodpecker.wpdatamodel.dao;

import com.letv.woodpecker.wpdatamodel.model.ExceptionInfo;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhusheng on 17/3/16.
 */
@Repository
public class ExceptionInfoDao extends MongoDao<ExceptionInfo> {

    @Override
    public Class getEntityClass() {
        return ExceptionInfo.class;
    }

    /**
     * 获取满足告警配置的异常数
     * */
    public long getExceptionCount(String appName,String ip,String exceptionType,String latestTime){
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        if(!"all".equals(ip)){
            query.addCriteria(Criteria.where("ip").is(ip));
        }
        if(!"all".equals(exceptionType)){
            query.addCriteria(Criteria.where("exceptionType").is(exceptionType));
        }
        if(!"".equals(latestTime)){
            query.addCriteria(Criteria.where("createTime").gt(latestTime));
        }
        return getCount(query);
    }

    /**
     * 根据查询条件获取异常数
     * */
    public long getCountByDetail(String appName,String exceptionType,String contentMd5,String startTime,String endTime){
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        query.addCriteria(Criteria.where("contentMd5").is(contentMd5));
        if(exceptionType != null){
            query.addCriteria(Criteria.where("exceptionType").is(exceptionType));
        }
        Criteria criteria = new Criteria();
        if(!"".equals(startTime)){
            if(!"".equals(endTime)){
                criteria.andOperator(Criteria.where("logTime").gte(startTime).lt(endTime));
            }else{
                criteria.andOperator(Criteria.where("logTime").gte(startTime));
            }
        }else{
            if(!"".equals(endTime)){
                criteria.andOperator(Criteria.where("logTime").lt(endTime));
            }
        }
        query.addCriteria(criteria);
        return getCount(query);
    }


    public List<ExceptionInfo> queryPage(String appName, String ip, String exceptionType, int pageStart, int pageSize){
        Query query = new Query();
        if(appName != null && !"".equals(appName)){
            query.addCriteria(Criteria.where("appName").is(appName));
        }
        if(ip != null && !"".equals(ip)){
            query.addCriteria(Criteria.where("ip").is(ip));
        }
        if(exceptionType !=null && !"".equals(exceptionType)){
            query.addCriteria(Criteria.where("exceptionType").is(exceptionType));
        }
        query.skip(pageStart).limit(pageSize);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"logTime")));
        return queryList(query);
    }

    public List<ExceptionInfo> queryAllExceptions(List<String> appNames, String appName, String startTime,String endTime, int pageStart, int pageSize){
        Query query = new Query();
        if(appName != null && !"".equals(appName)){
            query.addCriteria(Criteria.where("appName").is(appName));
        }else{
            query.addCriteria(Criteria.where("appName").in(appNames));
        }
        if(!"".equals(startTime)){
            if(!"".equals(endTime)){
                query.addCriteria(Criteria.where("logTime").gte(startTime).lt(endTime));
            }else{
                query.addCriteria(Criteria.where("logTime").gte(startTime));
            }
        }else{
            if(!"".equals(endTime)){
                query.addCriteria(Criteria.where("logTime").lt(endTime));
            }
        }
        query.skip(pageStart).limit(pageSize);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"logTime")));
        return queryList(query);
    }

    public long getAllExceptionCount(List<String> appNames, String appName, String startTime,String endTime){
        Query query = new Query();
        if(appName != null && !"".equals(appName)){
            query.addCriteria(Criteria.where("appName").is(appName));
        }else{
            query.addCriteria(Criteria.where("appName").in(appNames));
        }
        if(!"".equals(startTime)){
            if(!"".equals(endTime)){
                query.addCriteria(Criteria.where("logTime").gte(startTime).lt(endTime));
            }else{
                query.addCriteria(Criteria.where("logTime").gte(startTime));
            }
        }else{
            if(!"".equals(endTime)){
                query.addCriteria(Criteria.where("logTime").lt(endTime));
            }
        }
        return getCount(query);
    }

    public List<ExceptionInfo> queryPageByDetail(String appName,String exceptionType,String contentMd5,String startTime,String endTime, int pageStart, int pageSize){
        Query query = new Query();
        query.addCriteria(Criteria.where("appName").is(appName));
        query.addCriteria(Criteria.where("contentMd5").is(contentMd5));
        if(exceptionType !=null ){
            query.addCriteria(Criteria.where("exceptionType").is(exceptionType));
        }
        Criteria criteria = new Criteria();
        if(!"".equals(startTime)){
            if(!"".equals(endTime)){
                criteria.andOperator(Criteria.where("logTime").gte(startTime).lt(endTime));
            }else{
                criteria.andOperator(Criteria.where("logTime").gte(startTime));
            }
        }else{
            if(!"".equals(endTime)){
                criteria.andOperator(Criteria.where("logTime").lt(endTime));
            }
        }
        query.addCriteria(criteria);
        query.skip(pageStart).limit(pageSize);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"logTime")));
        return queryList(query);
    }


    /**
     * 按照异常类型分组
     * */
    public BasicDBList queryListByScheduler(List<String> appNames,String appName, String startTime, String endTime){
        Criteria criteria = new Criteria();
        String[] fields = new String[]{"appName","exceptionType"};
        String reduceFunc = "function(doc, out){out.count++;}";
        if(appName != null && !"".equals(appName)){
            criteria.andOperator(Criteria.where("appName").is(appName));
        }else {
            criteria.andOperator(Criteria.where("appName").in(appNames));
        }
        if(!"".equals(startTime)){
            if(!"".equals(endTime)){
                criteria.and("logTime").gte(startTime).lt(endTime);
            }else{
                criteria.and("logTime").gte(startTime);
            }
        }else{
            if(!"".equals(endTime)){
                criteria.and("logTime").lt(endTime);
            }
        }
        return mongoGroup(criteria,reduceFunc,fields);
    }

    /**
     * 根据指定异常类型按照md5值分组
     * */
    public BasicDBList classifyByMd5(String appName, String startTime, String endTime, String exceptionType){
        Criteria criteria = new Criteria();
        String[] fields = new String[]{"contentMd5"};
        criteria.andOperator(Criteria.where("appName").is(appName),Criteria.where("exceptionType").is(exceptionType));
        if(!"".equals(startTime)){
            if(!"".equals(endTime)){
                criteria.and("logTime").gte(startTime).lt(endTime);
            }else{
                criteria.and("logTime").gte(startTime);
            }
        }else{
            if(!"".equals(endTime)){
                criteria.and("logTime").lt(endTime);
            }
        }

        String reduceFunc = "function(doc, out){out.count++;out.appName=doc.appName;out.msg=doc.msg}";
        BasicDBList dbList = mongoGroup(criteria,reduceFunc,fields);
        return dbList;
    }

    public BasicDBList findExceptionNumByApp(List<String> appNames, String startTime, String endTime){
        Criteria criteria = new Criteria();
        //criteria.andOperator(Criteria.where("appNames").in(appNames));
        criteria.andOperator(Criteria.where("logTime").gte(startTime).lte(endTime));
        String[] fields = new String[]{"appName"};
        String reduceFunc = "function(doc, out){out.count++;}";
        BasicDBList dbList = mongoGroup(criteria,reduceFunc,fields);
        BasicDBList list = new BasicDBList();
        for(int i = 0; i<dbList.size(); i++){
            DBObject object = (DBObject) dbList.get(i);
            if( appNames.contains(object.get("appName")) ){
                list.add(object);
            }
        }
        return list;
    }

}

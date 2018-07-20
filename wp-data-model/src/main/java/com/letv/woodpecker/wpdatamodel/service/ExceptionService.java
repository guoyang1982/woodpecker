package com.letv.woodpecker.wpdatamodel.service;


import com.letv.woodpecker.wpdatamodel.model.ExceptionInfo;
import com.mongodb.DBObject;

import java.util.List;

/**
 * Created by zhusheng on 17/3/22.
 */
public interface ExceptionService {

    List<ExceptionInfo> queryAllExceptions(String userId, String appName, String startTime, String endTime, int pageStart, int pageSize, List<String> appNames);
    long getAllExceptionCount(String userId, String appName, String startTime, String endTime, List<String> appNames);
    List<ExceptionInfo> queryList(String appName, String exceptionType, String contentMd5, String startTime, String endTime, int pageStart, int pageSize);
    List<DBObject> queryListByScheduler(String useId, String appName, String startTime, String endTime,List<String> appNames);
    List<DBObject> classifyByMd5(String appName, String startTime, String endTime, String exceptionType);
    long getCountByDetail(String appName, String exceptionType, String contentMd5, String startTime, String endTime);
    List<DBObject> findExceptionNumByApp(String userId, String startTime, String endTime, List<String> appNames);
}

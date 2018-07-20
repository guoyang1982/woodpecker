package com.letv.woodpecker.wpwebapp.dao;


import com.letv.woodpecker.wpdatamodel.dao.MongoDao;
import com.letv.woodpecker.wpdatamodel.model.ExceptionInfo;
import com.letv.woodpecker.wpwebapp.utils.DateUtil;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by meijunjie on 2018/7/16.
 */
@Repository
public class CleanMongoException extends MongoDao<ExceptionInfo> {
    @Override
    public Class getEntityClass() {
        return ExceptionInfo.class;
    }

    public void cleanException(){
        // 当前日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Query query = new Query();
        query.addCriteria(Criteria.where("logTime").lt(dateFormat.format(DateUtil.getPreMonth(new Date(),3))));
        super.delete(query);
    }
}

package com.letv.woodpecker.wpdatamodel.dao;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhusheng on 17/3/16.
 * @author zhusheng
 */

@SuppressWarnings(value = "unchecked")
public abstract class MongoDao<T> {

    @Resource
    protected MongoTemplate mongoTemplate;

    public void save(T t){
        mongoTemplate.save(t);
    }



    public List<T> queryList(Query query){
        return mongoTemplate.find(query,getEntityClass());
    }
    public List<T> queryListGroupByType(Criteria criteria,String field){
        return (List<T>) mongoTemplate.group(criteria,mongoTemplate.getCollectionName(getEntityClass()),
                new GroupBy(field),getEntityClass());
    }

    public T queryOne(Query query){
        return (T) mongoTemplate.findOne(query,getEntityClass());
    }

    public long getCount(Query query){
        return mongoTemplate.count(query,getEntityClass());
    }

    public BasicDBList mongoGroup(Criteria criteria,String reduceFunc,String... fields){
        GroupBy groupBy = GroupBy.key(fields).initialDocument("{count:0}").reduceFunction(reduceFunc)
                .finalizeFunction("function(out){return out;}");
        GroupByResults<T> result = mongoTemplate.group(criteria,mongoTemplate.getCollectionName(getEntityClass()),groupBy,getEntityClass());
        DBObject obj = result.getRawResults();
        BasicDBList dbList = (BasicDBList) obj.get("retval");
        return dbList;
    }

    public void delete(Query query){
        mongoTemplate.remove(query,getEntityClass());
    }

    public void delete(T object){
        mongoTemplate.remove(object);
    }

    public void modify(Query query,Update update){
        mongoTemplate.updateFirst(query, update,getEntityClass());
    }
    public abstract Class getEntityClass();
}

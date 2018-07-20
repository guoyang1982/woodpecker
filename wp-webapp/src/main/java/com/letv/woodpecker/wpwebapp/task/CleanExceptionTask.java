package com.letv.woodpecker.wpwebapp.task;

import com.letv.woodpecker.wpwebapp.dao.CleanMongoException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 * @author meijunjie @date 2018/7/16
 */
@Component
public class CleanExceptionTask {

    @Resource
    private CleanMongoException cleanMongException;
    /**
     * "0 15 10 L * ?" 每月最后一天上午10:15执行，清除前三个月的异常信息
     */
    @Scheduled(cron = "0 15 10 L * ?")
    public void cleanException(){
        cleanMongException.cleanException();
    }
}

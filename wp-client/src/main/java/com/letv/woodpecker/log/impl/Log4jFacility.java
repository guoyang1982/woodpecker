package com.letv.woodpecker.log.impl;

import com.letv.woodpecker.log.LoggerFacility;
import com.letv.woodpecker.log.RedisLog4jAppender;
import org.apache.log4j.*;


import java.util.*;

/**
 * Created by guoyang on 17/3/24.
 */
public class Log4jFacility extends LoggerFacility{


    public void load()
    {
        RedisLog4jAppender appender = getCustomizedLog4jAppender();
        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
        if(loggers != null) {
            while (loggers.hasMoreElements()) {
                Logger logger = loggers.nextElement();
                Enumeration<Appender> allAppenders = logger.getAllAppenders();
                if(allAppenders != null && allAppenders.hasMoreElements()) {
                    if(!logger.getAdditivity()) {
                        logger.addAppender(appender);
                    }
                }
            }
        }
        Logger root = LogManager.getRootLogger();
        if(root != null) {
            root.addAppender(appender);
        }
    }

    public static Map<String,List<Appender>> getAllLoggerAppenders(){
        Map<String,List<Appender>> ret = new HashMap<String,List<Appender>>();
        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
        if(loggers != null) {
            while (loggers.hasMoreElements()) {
                Logger logger = loggers.nextElement();
                Enumeration<Appender> allAppenders = logger.getAllAppenders();
                if(allAppenders != null ){
                    ret.put(logger.getName(), new ArrayList<Appender>());
                    while ( allAppenders.hasMoreElements() ){
                        ret.get(logger.getName()).add(allAppenders.nextElement());
                    }
                }
            }
        }
        return ret;
    }

    private RedisLog4jAppender getCustomizedLog4jAppender() {
        RedisLog4jAppender consoleAppender = new RedisLog4jAppender();
        PatternLayout pattern = new PatternLayout();
        pattern.setConversionPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}][%-5p] %c{2} [%F:%L] - %m%n");
        consoleAppender.setLayout(pattern);
        consoleAppender.setThreshold(Level.ERROR);
        consoleAppender.setLog(this);
        return consoleAppender;
    }
}

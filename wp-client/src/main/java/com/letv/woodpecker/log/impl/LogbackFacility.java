package com.letv.woodpecker.log.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import com.letv.woodpecker.log.LoggerFacility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhusheng on 17/3/9.
 */

@Slf4j
public class LogbackFacility extends LoggerFacility {
    public static final String SP = (char)18 + "";

    @Override
    public void load() {
        log.info("加入异常日志监控!");
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if(loggerFactory instanceof LoggerContext){
            LoggerContext loggerContext = (LoggerContext) loggerFactory;
            List<Logger> loggers = loggerContext.getLoggerList();
            if(loggers != null && loggers.size() >0){
                ConsoleAppender appender = getCustomizedLogbackAppender(loggerContext);
                for(Logger logger : loggers) {
                    Iterator<Appender<ILoggingEvent>> appenders = logger.iteratorForAppenders();
                    if(appenders.hasNext()) { // 有appender的logger才处理，过滤掉框架自带的没有log文件输出的logger
                        if("ROOT".equalsIgnoreCase(logger.getName())) {
                            logger.addAppender(appender);
                        } else {
                            if(!logger.isAdditive()) {
                                logger.addAppender(appender);
                            }
                        }
                    }
                }
            }
        }
    }

    private ConsoleAppender getCustomizedLogbackAppender(LoggerContext loggerContext) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
        //Encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder() {
            @Override()
            public void doEncode(ILoggingEvent event) throws IOException {
                Level level = event.getLevel();
                if(Level.ERROR.equals(level)) {
                    String errorMsg = layout.doLayout(event);
                    if(StringUtils.isNotBlank(errorMsg)) {
                       // errorMsg = errorMsg.replace(CoreConstants.LINE_SEPARATOR, SP);
                        try{
                            sendToRedis(errorMsg);
                        }catch (Exception e){
                            log.info("执行发送redis日志异常,{}",e);
                        }
                    }
                }
            }
        };
        encoder.setPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}][%level] %logger{35} [%file:%line] - %m%n");
        encoder.setContext(loggerContext);
        encoder.start();
        //assemble appender
        appender.setEncoder(encoder);
        appender.setContext(loggerContext);
        appender.start();
        return appender;
    }

}

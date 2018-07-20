package com.letv.woodpecker.tools.log;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guoyang on 16/11/11.
 */
@Slf4j(topic = "serviceLog")
@Aspect
@Component
public class ServiceLogAspect {
    public static ThreadLocal<Long> traceId = new <Long>ThreadLocal();
    /**
     * 用于打印日志的线程池
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);

    @Around(value = "@annotation(p)")
    public Object beforeMethod(ProceedingJoinPoint pjp,ServiceLog p) {
        log.info("start,category={}", p.category());
        StopWatch clock = new StopWatch();
        clock.start(); //计时开始
        final String className = pjp.getTarget().getClass().getName();
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Class<?>[] paramTypes = method.getParameterTypes();
        StringBuilder info = new StringBuilder();
        for (Class<?> paramType : paramTypes) {
            info.append(paramType.getSimpleName()).append(",");
        }
        if (info.length() > 0) {
            info.deleteCharAt(info.length() - 1);
        }
        final String methodName = method.getName() + "(" + info.toString() + ")";
        Object c = null;
        String res = "";
        try {
            c = pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }finally {
            clock.stop();
        }
        if(null != c){
            res = JSONObject.toJSONString(c);
        }
        try{
            final String category = p.category();
            final String args = JSONObject.toJSONString(pjp.getArgs());
            final String result = res;
            final long time = clock.getTime();
            final Long traceIds = (Long)traceId.get();
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("{}|traceId={}|category={}|className={}|methodName={}|paramValue={}|result={}|time={}"
                            ,new Object[]{now(),traceIds,category,className,methodName,args,result,time});
                }
            });
        }catch (Exception e){
        }

        return c;
    }

    private String now() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        return sdf.format(now);
    }
}

package com.letv.woodpecker.wpserver.service;


import com.letv.woodpecker.wpdatamodel.model.ExceptionInfo;
import com.letv.woodpecker.wpserver.message.MessageBean;

/**
 * Created by zhusheng on 17/3/15.
 * modified by meijunjie on 17/9/28
 */
public interface ConsumeServer {

    /**
     * 消费异常信息
     * @param exceptionInfo
     */
    void doConsume(ExceptionInfo exceptionInfo);

    /**
     * 解析异常信息
     * @param exceptionInfo
     * @return
     */
    ExceptionInfo parseExceptionInfo(MessageBean exceptionInfo);
}

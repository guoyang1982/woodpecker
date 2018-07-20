package com.letv.woodpecker.wpserver.redis.impl;

import com.letv.woodpecker.wpdatamodel.model.ExceptionInfo;
import com.letv.woodpecker.wpserver.message.MessageBean;
import com.letv.woodpecker.wpserver.redis.QueueListener;
import com.letv.woodpecker.wpserver.service.ConsumeServer;
import com.letv.woodpecker.wpserver.utils.ThreadPoolManageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

/**
 * Created by zhusheng on 17/3/14.
 */
@Slf4j
@Service
public class RedisQueueListener implements QueueListener<MessageBean>
{
    @Resource
    private ConsumeServer consumeServer;

    @Resource
    private ThreadPoolManageUtil threadPoolManageUtil;

    @Override
    public void onMessage(MessageBean value)
    {
        ExceptionInfo exceptionInfo = consumeServer.parseExceptionInfo(value);

        if ( exceptionInfo != null && StringUtils.isNotEmpty(exceptionInfo.getAppName())) {
            threadPoolManageUtil.getThreadPoolByKey(exceptionInfo.getAppName()).execute(new MessageRunnable(exceptionInfo));
        }
    }

    private class MessageRunnable implements Runnable
    {
        private ExceptionInfo exceptionInfo;

        MessageRunnable(ExceptionInfo exceptionInfo)
        {
            this.exceptionInfo = exceptionInfo;
        }
        @Override
        public void run()
        {
            log.info("处理日志消息!");
            consumeServer.doConsume(exceptionInfo);
        }
    }
}

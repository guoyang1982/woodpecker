package com.letv.woodpecker.log;

import com.letv.woodpecker.log.impl.Log4jFacility;
import lombok.Data;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Created by guoyang on 17/3/26.
 */
@Data
public class RedisLog4jAppender extends AppenderSkeleton
{
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    Log4jFacility log = null;

    @Override
    protected void append(LoggingEvent loggingEvent) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.layout.format(loggingEvent));

        if(layout.ignoresThrowable()) {
            String[] s = loggingEvent.getThrowableStrRep();
            if (s != null) {
                int len = s.length;
                for(int i = 0; i < len; i++) {
                    sb.append(s[i]);
                    sb.append(LINE_SEPARATOR);
                }
            }
        }
        String rawMsg = sb.toString();
        log.sendToRedis(rawMsg);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}

package com.letv.woodpecker.wpserver.utils;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

/**
 * Http线程池管理工具
 * 单例模式实现
 * @author meijunjie
 */
class HttpConnPoolManager {

    private static final Logger log = LoggerFactory.getLogger(HttpConnPoolManager.class);

    private static final HttpConnPoolManager INSTANCE = new HttpConnPoolManager();

    private PoolingHttpClientConnectionManager cm = null;



    static HttpConnPoolManager getInstance() {
        return INSTANCE;
    }

    private HttpConnPoolManager() {
        Thread monitorThread;
        try {
            SSLContext sslCtx = org.apache.http.ssl.SSLContexts.createSystemDefault();
            SSLConnectionSocketFactory sslCsf = new SSLConnectionSocketFactory(sslCtx);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslCsf)
                    .build();

            cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 默认设置route最大连接数
        cm.setDefaultMaxPerRoute(500);

        // 连接池最大连接数
        cm.setMaxTotal(1000);
        monitorThread = new IdleConnectionMonitorThread(cm);
        monitorThread.start();
    }

    CloseableHttpClient build() {
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * http链接监控线程
     */
    public static class IdleConnectionMonitorThread extends Thread
    {
        private static final int SCAN_PERIOD = 15000;
        private static final int IDLE_TIME = 30;
        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(SCAN_PERIOD);
                        connMgr.closeExpiredConnections();
                        connMgr.closeIdleConnections(IDLE_TIME, TimeUnit.SECONDS);
                    }
                }
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

}

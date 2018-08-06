package com.letv.woodpecker.wpwebapp.config;

import com.letv.woodpecker.netty.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/8/3 下午7:58
 */
@Configuration
public class WebSocketConfig {

    @Autowired
    private Environment env;

    @Bean
    public WebSocketServer socketServer() {
        WebSocketServer webSocketServer = new WebSocketServer();
        final String port = env.getProperty("websocket.port", "8080");
        final String ip = env.getProperty("websocket.ip", "8080");


        final Thread socketThread = new Thread("webSocket-daemon") {
            @Override
            public void run() {
                try {
                    webSocketServer.run(ip, Integer.parseInt(port));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        socketThread.setDaemon(true);
        socketThread.start();
        return webSocketServer;
    }
}

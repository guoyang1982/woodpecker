package com.letv.woodpecker;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import static org.apache.commons.io.IOUtils.closeQuietly;

public class SocketServer {

    // 5分钟
    private static final int _1MIN = 60 * 1000;

    private final Socket socket;
    private BufferedWriter socketWriter;
    private BufferedReader socketReader;

    private volatile boolean isRunning;
    ChannelHandlerContext ctx;

    public SocketServer(InetSocketAddress address, ChannelHandlerContext ctx) throws IOException {

        this.ctx = ctx;
        this.socket = connect(address);
        this.isRunning = true;

        final Thread socketThread = new Thread("socket-daemon") {
            @Override
            public void run() {
                try {
                    loopForWriter();
                } catch (Exception e) {
                    err("read fail : %s", e.getMessage());
                    shutdown();
                }
            }
        };
        socketThread.setDaemon(true);
        socketThread.start();
    }


    /**
     * 激活网络
     */
    private Socket connect(InetSocketAddress address) throws IOException {
        final Socket socket = new Socket();
        socket.setSoTimeout(0);
        socket.connect(address, _1MIN);
        socket.setKeepAlive(true);
        socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return socket;
    }


    public void write(String msg) {
        try {
            socketWriter.write(msg + "\n");
            socketWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loopForWriter() {

        try {
            StringBuffer ss = new StringBuffer();
            while (isRunning) {

                int c = socketReader.read();
                if (c == 1) {
                    continue;
                }
                if (null == ss) {
                    ss = new StringBuffer();
                }
                if (c == 10) {
                    //System.out.println(ss.toString());
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(ss.toString()));
                    ss = null;
                } else if (c == 0) {
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("over"));
                    ss = null;
                } else {
                    ss.append(String.valueOf((char) c));
                }

//                String rs = socketReader.readLine();
//                System.out.println(rs);
//                if(flag){
//                    ctx.channel().writeAndFlush(new TextWebSocketFrame(rs.trim()));
//                }else {
//                    ctx.channel().writeAndFlush(new TextWebSocketFrame(rs));
//                }

            }
        } catch (IOException e) {
            err("write fail : %s", e.getMessage());
        } finally {
            shutdown();
        }

    }

    private void err(String format, Object... args) {
        System.err.println(String.format(format, args));
    }

    /**
     * 关闭Console
     */
    public void shutdown() {
        isRunning = false;
        closeQuietly(socketWriter);
        closeQuietly(socketReader);
        closeQuietly(socket);
    }

    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            args = new String[2];
            args[0] = "127.0.0.1";
            args[1] = "5901";
        }
        //new WPConsole(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
    }

}

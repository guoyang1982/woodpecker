package com.letv.woodpecker.netty;

import com.letv.woodpecker.SocketServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/8/2 下午7:44
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = Logger
            .getLogger(WebSocketServerHandler.class.getName());

    private WebSocketServerHandshaker handshaker;

    private static ConcurrentHashMap<Integer, SocketServer> socketMap = new ConcurrentHashMap();


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // 传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            //初始化session
            SessionManager.newSession(ctx);
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        // WebSocket接入
        else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) throws Exception {
        // 如果HTTP解码失败，返回HHTP异常
        if (!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1,
                    BAD_REQUEST));
            return;
        }

        // 构造握手响应返回，本机测试。注意，这条地址别被误导了，其实这里填写什么都无所谓，WS协议消息的接收不受这里控制
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8080/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 处理websocket请求
     *
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx,
                                      WebSocketFrame frame) {
        // 判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            //关闭socket链接
            closeSocket(ctx);
            handshaker.close(ctx.channel(),
                    (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }

        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();

        //前端websocket页面客户端关闭
        if (request.equals("close")) {
            //关闭socket链接
            closeSocket(ctx);
            ctx.close();
            return;
        }

        //只有建立websocket链接的时候才会发送这个标示
        if (request.startsWith("connection=")) {
            try {
                request = request.replaceFirst("connection=", "");
                if (null != request) {
                    String ip = request.split(":")[0];
                    String port = request.split(":")[1];
                    //生成socket对象 建立tcp链接
                    SocketServer socketServer = new SocketServer(new InetSocketAddress(ip, Integer.parseInt(port)), ctx);
                    //保存此channel对应的socket对象
                    socketMap.put(SessionManager.getSessionId(ctx), socketServer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("%s received %s", ctx.channel(), request));
        }

        //获取前端传进来的命令，并通过socket传给应用
        writeSocket(ctx, request);
    }

    private void writeSocket(ChannelHandlerContext ctx, String request) {
        SocketServer socketServer = socketMap.get(SessionManager.getSessionId(ctx));
        socketServer.write(request);
    }

    private void closeSocket(ChannelHandlerContext ctx) {
        SocketServer socketServer = socketMap.get(SessionManager.getSessionId(ctx));
        //清理增强的代码 如果有的话
        socketServer.write("kill");
        socketServer.shutdown();
        socketMap.remove(SessionManager.getSessionId(ctx));
    }


    /**
     * 发送http 响应
     *
     * @param ctx
     * @param req
     * @param res
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, FullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            // HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
//        if (!HttpUtil.isKeepAlive(req) || res.getStatus().code() != 200) {
//            f.addListener(ChannelFutureListener.CLOSE);
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        //关闭socket链接
        closeSocket(ctx);
        ctx.close();
    }
}
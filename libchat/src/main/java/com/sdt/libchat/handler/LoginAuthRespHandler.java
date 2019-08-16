package com.sdt.libchat.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.core.NettyClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public LoginAuthRespHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransMessageProtobuf.TransMessage handshakeRespMsg = (TransMessageProtobuf.TransMessage) msg;
        if (handshakeRespMsg == null || handshakeRespMsg.getHeader() == null) {
            return;
        }

        TransMessageProtobuf.TransMessage handshakeMsg = nettyClient.getHandshakeMsg();
        if (handshakeMsg == null || handshakeMsg.getHeader() == null) {
            return;
        }

        int handshakeMsgType = handshakeMsg.getHeader().getMsgType();
        if (handshakeMsgType == handshakeRespMsg.getHeader().getMsgType()) {
            logger.d("收到服务端握手响应消息，message=" + handshakeRespMsg.getHeader().getMsgId());
            int status = -1;
            try {
                JSONObject jsonObj = JSON.parseObject(handshakeRespMsg.getHeader().getExtend());
                status = jsonObj.getIntValue("status");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (status == 1) {
                    handleShakeSuccess();
                    nettyClient.getMsgDispatcher().receivedMsg(handshakeRespMsg);
                } else if (status == -1) {
                    nettyClient.getMsgDispatcher().receivedMsg(handshakeRespMsg);
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleShakeSuccess() {
        // 握手成功，马上先发送一条心跳消息，至于心跳机制管理，交由HeartbeatHandler
        TransMessageProtobuf.TransMessage heartbeatMsg = nettyClient.getHeartbeatMsg();
        if (heartbeatMsg == null) {
            return;
        }

        // 握手成功，检查消息发送超时管理器里是否有发送超时的消息，如果有，则全部重发
        nettyClient.getMsgTimeoutTimerManager().onReSendAll();

        logger.d("发送心跳消息：" + heartbeatMsg.getHeader().getMsgId() + "当前心跳间隔为：" + nettyClient.getHeartbeatInterval() + "ms\n");
        nettyClient.sendMsg(heartbeatMsg);

        // 添加心跳消息管理handler
        nettyClient.addHeartbeatHandler();
    }
}

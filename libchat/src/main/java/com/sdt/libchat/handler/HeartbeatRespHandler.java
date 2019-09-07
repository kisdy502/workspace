package com.sdt.libchat.handler;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.core.NettyClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class HeartbeatRespHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public HeartbeatRespHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransMessageProtobuf.TransMessage handshakeRespMsg = (TransMessageProtobuf.TransMessage) msg;
        if (handshakeRespMsg == null) {
            return;
        }

        TransMessageProtobuf.TransMessage handshakeMsg = nettyClient.getHeartbeatMsg();
        if (handshakeMsg == null ) {
            return;
        }

        int handshakeMsgType = handshakeMsg.getMsgType();
        if (handshakeMsgType == handshakeRespMsg.getMsgType()) {
            logger.i("收到服务端心跳响应,id=" + handshakeRespMsg.getMsgId());
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}

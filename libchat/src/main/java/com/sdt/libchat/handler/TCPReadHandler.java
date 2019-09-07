package com.sdt.libchat.handler;

import com.alibaba.fastjson.JSONObject;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.MessageHelper;
import com.sdt.libchat.core.ImsConfig;
import com.sdt.libchat.core.NettyClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import java.util.UUID;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class TCPReadHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public TCPReadHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransMessageProtobuf.TransMessage transMessage = (TransMessageProtobuf.TransMessage) msg;
        if (transMessage == null) {
            return;
        }

        int msgType = transMessage.getMsgType();
        if (msgType == nettyClient.getSingleChatMsgType()) {
            logger.d("收到聊天消息，message=" + transMessage);
            TransMessageProtobuf.TransMessage receivedReportMsg =
                    MessageHelper.buildReceivedReportMsg(transMessage,
                            nettyClient.getClientReceivedReportMsgType());
            if (receivedReportMsg != null) {
                nettyClient.sendMsg(receivedReportMsg);
            }
            // 接收消息，由消息转发器转发到应用层
            nettyClient.getMsgDispatcher().receivedMsg(transMessage);
        } else {
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.i("TCPReadHandler channelInactive()");
        Channel channel = ctx.channel();
        if (channel != null) {
            channel.close();
            ctx.close();
        }
        // 触发重连 ,考虑到客户端被踢下线的情形，这里不能简单的就重连
        //nettyClient.prepareConnect(false);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.e("TCPReadHandler exceptionCaught()");
        Channel channel = ctx.channel();
        if (channel != null) {
            channel.close();
            ctx.close();
        }
        // 触发重连
        nettyClient.prepareConnect(false);
    }


}

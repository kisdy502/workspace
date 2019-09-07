package com.sdt.libchat.handler;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.MessageHelper;
import com.sdt.libchat.core.NettyClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class HandleAddFriendResultHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public HandleAddFriendResultHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransMessageProtobuf.TransMessage transMessage = (TransMessageProtobuf.TransMessage) msg;
        if (transMessage == null) {
            return;
        }

        int transMessageType = transMessage.getMsgType();
        if (transMessageType == nettyClient.getAgreeAddFriendType()) {
            TransMessageProtobuf.TransMessage receivedReportMsg = MessageHelper.buildReceivedReportMsg(transMessage,
                    nettyClient.getClientReceivedReportMsgType());
            if (receivedReportMsg != null) {
                nettyClient.sendMsg(receivedReportMsg);
            }
            nettyClient.getMsgDispatcher().receivedMsg(transMessage);
        } else if (transMessageType == nettyClient.getRefuseAddFriendType()) {
            TransMessageProtobuf.TransMessage receivedReportMsg = MessageHelper.buildReceivedReportMsg(transMessage,
                    nettyClient.getClientReceivedReportMsgType());
            if (receivedReportMsg != null) {
                nettyClient.sendMsg(receivedReportMsg);
            }
            nettyClient.getMsgDispatcher().receivedMsg(transMessage);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}

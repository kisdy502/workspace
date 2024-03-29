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

public class SystemPushMessageHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public SystemPushMessageHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransMessageProtobuf.TransMessage systemPushMsg = (TransMessageProtobuf.TransMessage) msg;
        if (systemPushMsg == null ) {
            return;
        }
        int pushMsgType = nettyClient.getSystemPushMsgType();
        if (pushMsgType == systemPushMsg.getMsgType()) {
            logger.d("系统推送消息，message=" + systemPushMsg);
            nettyClient.getMsgDispatcher().receivedMsg(systemPushMsg);
            TransMessageProtobuf.TransMessage receivedReportMsg =
                    MessageHelper.buildReceivedReportMsg(systemPushMsg,
                            nettyClient.getClientReceivedReportMsgType());
            if (receivedReportMsg != null) {
                nettyClient.sendMsg(receivedReportMsg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}

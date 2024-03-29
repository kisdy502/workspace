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

public class RequestAddFriendHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public RequestAddFriendHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransMessageProtobuf.TransMessage transMessage = (TransMessageProtobuf.TransMessage) msg;
        if (transMessage == null ) {
            return;
        }

        int transMessageType = nettyClient.getRequestAddFriendType();
        if (transMessageType == transMessage.getMsgType()) {

            TransMessageProtobuf.TransMessage receivedReportMsg =
                    MessageHelper.buildReceivedReportMsg(transMessage,
                            nettyClient.getClientReceivedReportMsgType());
            if (receivedReportMsg != null) {
                nettyClient.sendMsg(receivedReportMsg);
            }

            logger.i("请求加好友:,id=" + transMessage.getMsgId());
            nettyClient.getMsgDispatcher().receivedMsg(transMessage);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}

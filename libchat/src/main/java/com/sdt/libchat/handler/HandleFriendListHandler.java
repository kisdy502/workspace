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

public class HandleFriendListHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public HandleFriendListHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransMessageProtobuf.TransMessage transMessage = (TransMessageProtobuf.TransMessage) msg;
        if (transMessage == null || transMessage.getHeader() == null) {
            return;
        }

        TransMessageProtobuf.TransMessage friendListMsg = nettyClient.getFriendListMsg();
        if (friendListMsg == null || friendListMsg.getHeader() == null) {
            return;
        }

        int transMessageType = transMessage.getHeader().getMsgType();
        if (transMessageType == friendListMsg.getHeader().getMsgType()) {
            logger.i("服务器下发朋友列表:,id=" + transMessage.getBody());
            nettyClient.getMsgDispatcher().receivedMsg(transMessage);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}

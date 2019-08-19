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
        if (transMessage == null || transMessage.getHeader() == null) {
            return;
        }

        int msgType = transMessage.getHeader().getMsgType();
        if (msgType == nettyClient.getServerSentReportMsgType()) {
            int statusReport = transMessage.getHeader().getStatusReport();
            logger.d(String.format("服务端状态报告:[%d],1成功0失败", statusReport));
            if (statusReport == ImsConfig.DEFAULT_REPORT_SERVER_SEND_MSG_SUCCESSFUL) {
                logger.d("收到服务端消息发送状态报告，message=" + transMessage + "，从超时管理器移除");
                nettyClient.getMsgTimeoutTimerManager().remove(transMessage.getHeader().getMsgId());
            }
        } else {
            logger.d("收到消息，message=" + transMessage);
            TransMessageProtobuf.TransMessage receivedReportMsg =
                    MessageHelper.buildReceivedReportMsg(transMessage.getHeader().getMsgId(),
                            nettyClient.getClientReceivedReportMsgType());
            if (receivedReportMsg != null) {
                nettyClient.sendMsg(receivedReportMsg);
            }
        }
        // 接收消息，由消息转发器转发到应用层
        nettyClient.getMsgDispatcher().receivedMsg(transMessage);
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

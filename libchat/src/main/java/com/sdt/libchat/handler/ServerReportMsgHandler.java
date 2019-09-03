package com.sdt.libchat.handler;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.core.ImsConfig;
import com.sdt.libchat.core.NettyClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class ServerReportMsgHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public ServerReportMsgHandler(NettyClient nettyClient) {
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
                nettyClient.getMsgDispatcher().receivedMsg(transMessage);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

}

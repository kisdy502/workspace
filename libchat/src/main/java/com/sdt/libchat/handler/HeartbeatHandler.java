package com.sdt.libchat.handler;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.core.NettyClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private NettyClient nettyClient;

    public HeartbeatHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            switch (state) {
                case READER_IDLE: {
                    logger.d("READER_IDLE");
                    // 规定时间内没收到服务端心跳包响应，进行重连操作
                    nettyClient.prepareConnect(false);
                    break;
                }

                case WRITER_IDLE: {
                    logger.d("WRITER_IDLE");
                    // 规定时间内没向服务端发送心跳包，即发送一个心跳包
                    if (heartbeatTask == null) {
                        heartbeatTask = new HeartbeatTask(ctx);
                    }
                    nettyClient.getLoopGroup().execWorkTask(heartbeatTask);
                    break;
                }
            }
        }
    }

    private HeartbeatTask heartbeatTask;

    private class HeartbeatTask implements Runnable {

        private ChannelHandlerContext ctx;

        public HeartbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (ctx.channel().isActive()) {
                TransMessageProtobuf.TransMessage heartbeatMsg = nettyClient.getHeartbeatMsg();
                if (heartbeatMsg == null) {
                    return;
                }
                logger.d("发送心跳,id=" + heartbeatMsg.getHeader().getMsgId() + "心跳间隔:" + nettyClient.getHeartbeatInterval() / 1000 + "s\n");
                nettyClient.sendMsg(heartbeatMsg, false);
            }
        }
    }

}

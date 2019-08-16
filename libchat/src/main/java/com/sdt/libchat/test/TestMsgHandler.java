package com.sdt.libchat.test;

import com.alibaba.fastjson.JSONObject;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class TestMsgHandler extends ChannelInboundHandlerAdapter {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private ClientTest clientTest;

    public TestMsgHandler(ClientTest clientTest) {
        this.clientTest = clientTest;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            switch (state) {
                case READER_IDLE: {
                    logger.d("READER_IDLE");
                    break;
                }

                case WRITER_IDLE: {
                    logger.d("WRITER_IDLE");
                    TransMessageProtobuf.TransMessage heartbeatMsg = getHeartbeatMsg();
                    ctx.writeAndFlush(heartbeatMsg);
                    break;
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransMessageProtobuf.TransMessage transMessage = (TransMessageProtobuf.TransMessage) msg;
        if (transMessage == null || transMessage.getHeader() == null) {
            return;
        }
        logger.d("read msg:" + transMessage);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.e("TestMsgHandler channelActive()");
        TransMessageProtobuf.TransMessage handshakeMsg = getHandshakeMsg();
        ctx.writeAndFlush(handshakeMsg);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.e("TestMsgHandler channelInactive()");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.e("TestMsgHandler exceptionCaught()");


    }

    public TransMessageProtobuf.TransMessage getHandshakeMsg() {
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(1001);

        headBuilder.setFromId(clientTest.uid);
        headBuilder.setTimestamp(System.currentTimeMillis());

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("token", clientTest.token);
        headBuilder.setExtend(jsonObj.toString());
        builder.setHeader(headBuilder.build());

        return builder.build();
    }


    public TransMessageProtobuf.TransMessage getHeartbeatMsg() {
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(1002);
        headBuilder.setFromId(clientTest.uid);
        headBuilder.setTimestamp(System.currentTimeMillis());
        builder.setHeader(headBuilder.build());
        return builder.build();
    }

}
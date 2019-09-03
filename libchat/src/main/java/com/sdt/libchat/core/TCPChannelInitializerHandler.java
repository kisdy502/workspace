package com.sdt.libchat.core;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.handler.ForceLogoutHandler;
import com.sdt.libchat.handler.HandleFriendListHandler;
import com.sdt.libchat.handler.HandleOutLineMessageListHandler;
import com.sdt.libchat.handler.HeartbeatRespHandler;
import com.sdt.libchat.handler.LoginAuthRespHandler;
import com.sdt.libchat.handler.RequestAddFriendHandler;
import com.sdt.libchat.handler.ServerReportMsgHandler;
import com.sdt.libchat.handler.SystemPushMessageHandler;
import com.sdt.libchat.handler.TCPReadHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * Created by sdt13411 on 2019/7/16.
 */

public class TCPChannelInitializerHandler extends ChannelInitializer<Channel> {

    NettyClient nettyClient;

    public TCPChannelInitializerHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        // netty提供的自定义长度解码器，解决TCP拆包/粘包问题
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535,
                0, 2, 0, 2));

        // 增加protobuf编解码支持
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new ProtobufDecoder(TransMessageProtobuf.TransMessage.getDefaultInstance()));

        // 握手认证消息响应处理handler
        pipeline.addLast(LoginAuthRespHandler.class.getSimpleName(), new LoginAuthRespHandler(nettyClient));
        // 客户端发送消息给服务器，服务器报告给客户端，该消息已经接收的类型的消息
        pipeline.addLast(ServerReportMsgHandler.class.getSimpleName(), new ServerReportMsgHandler(nettyClient));
        // 好友列表消息响应处理handler
        pipeline.addLast(HandleFriendListHandler.class.getSimpleName(), new HandleFriendListHandler(nettyClient));
        //离线消息处理
        pipeline.addLast(HandleOutLineMessageListHandler.class.getSimpleName(), new HandleOutLineMessageListHandler(nettyClient));
        // 心跳消息响应处理handler
        pipeline.addLast(HeartbeatRespHandler.class.getSimpleName(), new HeartbeatRespHandler(nettyClient));
        // 接收消息处理handler
        pipeline.addLast(SystemPushMessageHandler.class.getSimpleName(), new SystemPushMessageHandler(nettyClient));
        pipeline.addLast(ForceLogoutHandler.class.getSimpleName(), new ForceLogoutHandler(nettyClient));
        pipeline.addLast(RequestAddFriendHandler.class.getSimpleName(), new RequestAddFriendHandler(nettyClient));

        pipeline.addLast(TCPReadHandler.class.getSimpleName(), new TCPReadHandler(nettyClient));
    }
}

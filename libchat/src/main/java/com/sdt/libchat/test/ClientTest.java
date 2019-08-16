package com.sdt.libchat.test;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.handler.TCPReadHandler;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by sdt13411 on 2019/7/20.
 */

public class ClientTest {

    String uid;
    String token;

    String currentHost;
    int currentPort;

    public ClientTest(String currentHost, int currentPort) {
        uid = "TestClient" + new Random().nextInt(10000);
        token = "token_" + uid;
        this.currentHost = currentHost;
        this.currentPort = currentPort;

    }

    public void startClient() {
        EventLoopGroup workGroup = new NioEventLoopGroup(4);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup).channel(NioSocketChannel.class);
        // 设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        // 设置禁用nagle算法
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        // 设置连接超时时长
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        // 设置初始化Channel
        bootstrap.handler(new TCPChannelInitializerHandler());
        try {
            Channel channel = bootstrap.connect(currentHost, currentPort).sync().channel();
            /**等待客户端链路关闭*/
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
        }
    }

    private class TCPChannelInitializerHandler extends ChannelInitializer<Channel> {
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

            pipeline.addLast(TCPReadHandler.class.getSimpleName(), new TestMsgHandler(ClientTest.this));
            pipeline.addFirst(IdleStateHandler.class.getSimpleName(), new IdleStateHandler(
                    60000 * 3, 60000, 60000 * 4, TimeUnit.MILLISECONDS));

        }
    }
}

package com.sdt.libchat.core;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.ExecutorServiceFactory;
import com.sdt.libchat.OnConnectStatusCallback;
import com.sdt.libchat.MsgDispatcher;
import com.sdt.libchat.OnEventListener;
import com.sdt.libchat.handler.HeartbeatHandler;
import com.sdt.libchat.handler.TCPReadHandler;
import com.sdt.libchat.timer.MsgTimeoutManager;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.StringUtil;

/**
 * Created by sdt13411 on 2019/7/16.
 */

public class NettyClient implements ImsClient {
    ILogger logger = ILoggerFactory.getLogger(getClass());

    private Bootstrap bootstrap;
    private Channel channel;

    private String currentHost = null;              // 当前连接host
    private int currentPort = -1;                   // 当前连接port

    private boolean closed = false;// 标识ims是否已关闭
    private boolean isReconnecting = false;// 是否正在进行重连

    private int connectStatus = ImsConfig.CONNECT_STATE_FAILURE;// ims连接状态，初始化为连接失败
    private int connectTimeout = ImsConfig.DEFAULT_CONNECT_TIMEOUT;

    private int reconnectInterval = ImsConfig.DEFAULT_RECONNECT_BASE_DELAY_TIME;     // 重连间隔时长
    private int heartbeatInterval = ImsConfig.DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND;     // 心跳间隔时间

    // 消息发送失败重发间隔时长
    private int resendInterval = ImsConfig.DEFAULT_RESEND_INTERVAL;
    // 消息发送超时重发次数
    private int resendCount = ImsConfig.DEFAULT_RESEND_COUNT;
    private Vector<String> serverUrlList;// ims服务器地址组

    private ExecutorServiceFactory loopGroup;// 线程池工厂

    private OnConnectStatusCallback mIMSConnectStatusCallback;// ims连接状态回调监听器
    private OnEventListener mOnEventListener;// 与应用层交互的listener
    private MsgDispatcher msgDispatcher;// 消息转发器

    private MsgTimeoutManager msgTimeoutTimerManager;// 消息发送超时定时器管理

    @Override
    public void init(Vector<String> serverUrlList, OnConnectStatusCallback callback, OnEventListener listener) {
        this.serverUrlList = serverUrlList;
        mIMSConnectStatusCallback = callback;
        mOnEventListener = listener;
        loopGroup = new ExecutorServiceFactory();
        loopGroup.initBossLoopGroup();            // 初始化重连线程组
        msgDispatcher = new MsgDispatcher(mOnEventListener);
        msgTimeoutTimerManager = new MsgTimeoutManager(this);
        closed = false;
        prepareConnect(true);                      // 进行第一次连接
    }

    @Override
    public void prepareConnect(boolean isFirst) {
        if (!isFirst) {
            try {
                Thread.sleep(ImsConfig.DEFAULT_RECONNECT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.d("isReconnecting:" + isReconnecting + ",closed:" + closed);
        // 只有第一个调用者才能赋值并调用重连
        if (!closed && !isReconnecting) {
            synchronized (this) {
                if (!closed && !isReconnecting) {
                    // 标识正在进行重连
                    isReconnecting = true;
                    // 先关闭channel
                    closeChannel();
                    // 执行重连任务
                    loopGroup.execBossTask(new ConnectRunnable(isFirst));
                }
            }
        }
    }


    @Override
    public void setAppStatus(int appStatus) {

    }

    @Override
    public TransMessageProtobuf.TransMessage getHandshakeMsg() {
        if (mOnEventListener != null) {
            return mOnEventListener.getHandshakeMsg();
        }
        return null;
    }

    @Override
    public int getSystemPushMsgType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getSystemPushMsgType();
        }
        return 0;
    }

    @Override
    public TransMessageProtobuf.TransMessage getHeartbeatMsg() {
        if (mOnEventListener != null) {
            return mOnEventListener.getHeartbeatMsg();
        }

        return null;
    }

    @Override
    public TransMessageProtobuf.TransMessage getFriendListMsg() {
        if (mOnEventListener != null) {
            return mOnEventListener.getFriendListMsg();
        }

        return null;
    }


    @Override
    public int getHeartbeatMsgType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getHeartbeatMsgType();
        }

        return 0;
    }

    @Override
    public int getHandshakeMsgType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getHandshakeMsgType();
        }
        return 0;
    }

    @Override
    public int getForceLogoutMsgType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getForceLogoutMsgType();
        }
        return 0;
    }

    @Override
    public int getOutLineMsgListType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getOutLineMsgListType();
        }
        return 0;
    }

    @Override
    public int getRequestAddFriendType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getRequestAddFriendType();
        }
        return 0;
    }

    @Override
    public int getServerSentReportMsgType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getServerSentReportMsgType();
        }
        return 0;
    }


    @Override
    public int getClientReceivedReportMsgType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getClientReceivedReportMsgType();
        }
        return 0;
    }

    @Override
    public int getSingleChatMsgType() {
        if (mOnEventListener != null) {
            return mOnEventListener.getSingleChatMsgType();
        }
        return 0;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public MsgTimeoutManager getMsgTimeoutTimerManager() {
        return msgTimeoutTimerManager;
    }

    @Override
    public int getResendCount() {
        if (mOnEventListener != null && mOnEventListener.getResendCount() != 0) {
            return resendCount = mOnEventListener.getResendCount();
        }

        return resendCount;
    }

    @Override
    public int getResendInterval() {
        if (mOnEventListener != null && mOnEventListener.getReconnectInterval() != 0) {
            return resendInterval = mOnEventListener.getResendInterval();
        }
        return resendInterval;
    }


    @Override
    public int getConnectTimeout() {
        if (mOnEventListener != null && mOnEventListener.getConnectTimeout() > 0) {
            return connectTimeout = mOnEventListener.getConnectTimeout();
        }
        return connectTimeout;
    }


    @Override
    public void sendMsg(TransMessageProtobuf.TransMessage transMessage) {
        sendMsg(transMessage, true);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;

        // 关闭channel
        try {
            closeChannel();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 关闭bootstrap
        try {
            if (bootstrap != null) {
                bootstrap.group().shutdownGracefully();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            // 释放线程池
            if (loopGroup != null) {
                loopGroup.destroy();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (serverUrlList != null) {
                    serverUrlList.clear();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            isReconnecting = false;
            channel = null;
            bootstrap = null;
        }
    }

    private boolean isNetworkAvailable() {
        if (mOnEventListener != null) {
            return mOnEventListener.isNetworkAvailable();
        }
        return false;
    }


    @Override
    public int getReconnectInterval() {
        if (mOnEventListener != null && mOnEventListener.getReconnectInterval() > 0) {
            return reconnectInterval = mOnEventListener.getReconnectInterval();
        }
        return reconnectInterval;
    }


    private void closeChannel() {
        try {
            if (channel != null) {
                channel.close();
                channel.eventLoop().shutdownGracefully();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.e("关闭channel出错，reason:" + e.getMessage());
        } finally {
            channel = null;
        }
    }


    @Override
    public void sendMsg(TransMessageProtobuf.TransMessage transMessage, boolean reSend) {
        if (transMessage == null || transMessage.getHeader() == null) {
            logger.w("发送消息失败，消息为空\tmessage=" + transMessage);
            return;
        }

        if (!StringUtil.isNullOrEmpty(transMessage.getHeader().getMsgId())) {
            if (reSend) {
                msgTimeoutTimerManager.add(transMessage);
            }
        }

        if (channel == null) {
            logger.w("发送消息失败，channel为空\tmessage=" + transMessage);
        }


        try {
            logger.i("real send,message=" + transMessage);
            channel.writeAndFlush(transMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.w("发送消息失败，reason:" + ex.getMessage() + "\tmessage=" + transMessage);
        }
    }


    public void addHeartbeatHandler() {
        if (channel == null || !channel.isActive() || channel.pipeline() == null) {
            return;
        }

        try {
            // 之前存在的读写超时handler，先移除掉，再重新添加
            if (channel.pipeline().get(IdleStateHandler.class.getSimpleName()) != null) {
                channel.pipeline().remove(IdleStateHandler.class.getSimpleName());
            }
            // 3次心跳没响应，代表连接已断开
            channel.pipeline().addFirst(IdleStateHandler.class.getSimpleName(), new IdleStateHandler(
                    heartbeatInterval * 3, heartbeatInterval, heartbeatInterval * 4, TimeUnit.MILLISECONDS));

            // 重新添加HeartbeatHandler
            if (channel.pipeline().get(HeartbeatHandler.class.getSimpleName()) != null) {
                channel.pipeline().remove(HeartbeatHandler.class.getSimpleName());
            }
            if (channel.pipeline().get(TCPReadHandler.class.getSimpleName()) != null) {
                channel.pipeline().addBefore(TCPReadHandler.class.getSimpleName(),
                        HeartbeatHandler.class.getSimpleName(), new HeartbeatHandler(this));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.e("添加心跳消息管理handler失败，reason：" + e.getMessage());
        }
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public MsgDispatcher getMsgDispatcher() {
        return msgDispatcher;
    }

    public ExecutorServiceFactory getLoopGroup() {
        return loopGroup;
    }


    public static NettyClient getInstance() {
        return Holder.client;
    }

    private static class Holder {
        private final static NettyClient client = new NettyClient();
    }


    private class ConnectRunnable implements Runnable {

        boolean isFirst;

        public ConnectRunnable(boolean isFirst) {
            this.isFirst = isFirst;
        }

        @Override
        public void run() {
            logger.e("ConnectRunnable run...");
            onConnectStatusCallback(ImsConfig.CONNECT_STATE_CONNECTING);   // 回调ims连接状态
            try {
                // 重连时，释放工作线程组，也就是停止心跳
                loopGroup.destroyWorkLoopGroup();

                while (!closed) {
                    if (!isNetworkAvailable()) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    // 网络可用才进行连接
                    int status;
                    if ((status = doConnect()) == ImsConfig.CONNECT_STATE_SUCCESSFUL) {
                        onConnectStatusCallback(status);
                        // 连接成功，发送握手消息
                        sendHandshakeMsg();
                        break;  // 连接成功，跳出循环
                    }
                    if (status == ImsConfig.CONNECT_STATE_FAILURE) {
                        onConnectStatusCallback(status);
                        try {
                            Thread.sleep(ImsConfig.DEFAULT_RECONNECT_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                // 标识重连任务停止
                isReconnecting = false;
            }
        }


        private void onConnectStatusCallback(int status) {
            connectStatus = status;
            switch (connectStatus) {
                case ImsConfig.CONNECT_STATE_CONNECTING: {
                    logger.d("ims连接中...");
                    if (mIMSConnectStatusCallback != null) {
                        mIMSConnectStatusCallback.onConnecting();
                    }
                    break;
                }

                case ImsConfig.CONNECT_STATE_SUCCESSFUL: {
                    logger.d(String.format("ims连接成功，host『%s』, port『%s』", currentHost, currentPort));
                    if (mIMSConnectStatusCallback != null) {
                        mIMSConnectStatusCallback.onConnected();
                    }
                    break;
                }

                case ImsConfig.CONNECT_STATE_FAILURE:
                default: {
                    logger.e("ims连接失败");
                    if (mIMSConnectStatusCallback != null) {
                        mIMSConnectStatusCallback.onConnectFailed();
                    }
                    break;
                }
            }
        }


        private void sendHandshakeMsg() {
            TransMessageProtobuf.TransMessage handshakeMsg = getHandshakeMsg();
            if (handshakeMsg != null) {
                logger.d("发送握手消息，message=" + handshakeMsg.getHeader().getMsgId());
                sendMsg(handshakeMsg, false);
            } else {
                logger.d("请应用层构建握手消息！");
            }
        }


        /**
         * 连接操作
         *
         * @return
         */
        private int doConnect() {
            if (!closed) {                                          // 未关闭才去连接
                try {
                    if (bootstrap != null) {
                        bootstrap.group().shutdownGracefully();     // 先释放EventLoop线程组
                    }
                } finally {
                    bootstrap = null;
                }
                // 初始化bootstrap
                initBootstrap();
                return loopConnect();
            }
            return ImsConfig.CONNECT_STATE_FAILURE;
        }


        /**
         * 连接服务器
         *
         * @return
         */
        private int loopConnect() {
            // 如果服务器地址无效，直接回调连接状态，不再进行连接
            // 有效的服务器地址示例：127.0.0.1 8860
            if (serverUrlList == null || serverUrlList.size() == 0) {
                return ImsConfig.CONNECT_STATE_FAILURE;
            }

            for (int i = 0; (!closed && i < serverUrlList.size()); i++) {
                String serverUrl = serverUrlList.get(i);
                // 如果服务器地址无效，直接回调连接状态，不再进行连接
                if (StringUtil.isNullOrEmpty(serverUrl)) {
                    return ImsConfig.CONNECT_STATE_FAILURE;
                }

                String[] address = serverUrl.split(" ");
                for (int j = 1; j <= ImsConfig.DEFAULT_RECONNECT_COUNT; j++) {
                    // 如果ims已关闭，或网络不可用，直接回调连接状态，不再进行连接
                    if (closed || !isNetworkAvailable()) {
                        return ImsConfig.CONNECT_STATE_FAILURE;
                    }

                    // 回调连接状态
                    if (connectStatus != ImsConfig.CONNECT_STATE_CONNECTING) {
                        //onConnectStatusCallback(ImsConfig.CONNECT_STATE_CONNECTING);
                    }
                    logger.d(String.format("正在进行[%s]的第[%d]次连接，当前重连延时时长为[%ds]", serverUrl, j, j * getReconnectInterval() / 1000));

                    try {
                        currentHost = address[0];// 获取host
                        currentPort = Integer.parseInt(address[1]);// 获取port
                        nettyConnect();// 连接服务器

                        // channel不为空，即认为连接已成功
                        if (channel != null) {
                            return ImsConfig.CONNECT_STATE_SUCCESSFUL;
                        }
                        // 连接失败，则线程休眠n * 重连间隔时长
                        Thread.sleep(j * getReconnectInterval());

                    } catch (InterruptedException e) {
                        close();
                        break;// 线程被中断，则强制关闭
                    }
                }
            }
            // 执行到这里，代表连接失败
            return ImsConfig.CONNECT_STATE_FAILURE;
        }


        private void initBootstrap() {
            EventLoopGroup loopGroup = new NioEventLoopGroup(4);
            bootstrap = new Bootstrap();
            bootstrap.group(loopGroup).channel(NioSocketChannel.class);
            // 设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            // 设置禁用nagle算法
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 设置连接超时时长
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeout());
            // 设置初始化Channel
            bootstrap.handler(new TCPChannelInitializerHandler(NettyClient.this));
        }


        /**
         * 真正连接服务器的地方
         */
        private void nettyConnect() {
            try {
                channel = bootstrap.connect(currentHost, currentPort).sync().channel();
            } catch (Exception e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                logger.e(String.format("连接Server(ip[%s], port[%s])失败", currentHost, currentPort));
                channel = null;
            }
        }

    }
}

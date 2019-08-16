package com.sdt.libchat.core;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.IMSConnectStatusCallback;
import com.sdt.libchat.MsgDispatcher;
import com.sdt.libchat.OnEventListener;
import com.sdt.libchat.timer.MsgTimeoutTimerManager;

import java.util.Vector;

/**
 * Created by sdt13411 on 2019/7/16.
 */

public interface ImsClient {

    int APP_FORGROUND = 0;
    int APP_BACKGROUND = 1;

    /**
     * 获取连接超时时长
     *
     * @return
     */
    int getConnectTimeout();

    int getReconnectInterval();

    void sendMsg(TransMessageProtobuf.TransMessage transMessage);

    void sendMsg(TransMessageProtobuf.TransMessage transMessage, boolean reSend);

    void prepareConnect(boolean isFirst);

    void close();

    void init(Vector<String> serverUrlList, IMSConnectStatusCallback callback, OnEventListener listener);

    void setAppStatus(int appStatus);

    TransMessageProtobuf.TransMessage getHeartbeatMsg();

    TransMessageProtobuf.TransMessage getHandshakeMsg();

    TransMessageProtobuf.TransMessage getFriendListMsg();

    int getSystemPushMsgType();

    int getHeartbeatMsgType();

    int getHandshakeMsgType();

    int getOutLineMsgListType();

    int getServerSentReportMsgType();

    int getClientReceivedReportMsgType();

    boolean isClosed();

    MsgTimeoutTimerManager getMsgTimeoutTimerManager();


    /**
     * 获取应用层消息发送超时重发次数
     */
    int getResendCount();

    /**
     * 获取应用层消息发送超时重发间隔
     */
    int getResendInterval();


    MsgDispatcher getMsgDispatcher();
}

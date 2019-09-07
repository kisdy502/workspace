package com.sdt.libchat.core;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.OnConnectStatusCallback;
import com.sdt.libchat.MsgDispatcher;
import com.sdt.libchat.OnEventListener;
import com.sdt.libchat.timer.MsgTimeoutManager;

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

    void init(Vector<String> serverUrlList, OnConnectStatusCallback callback, OnEventListener listener);

    void setAppStatus(int appStatus);

    TransMessageProtobuf.TransMessage getHeartbeatMsg();

    TransMessageProtobuf.TransMessage getHandshakeMsg();

    TransMessageProtobuf.TransMessage getFriendListMsg();

    int getSystemPushMsgType();

    int getHeartbeatMsgType();

    int getHandshakeMsgType();

    int getForceLogoutMsgType();

    int getOutLineMsgListType();

    /**
     * 请求添加好友
     */
    int getRequestAddFriendType();

    /**
     * 同意添加好友结果消息
     */
    int getAgreeAddFriendType();

    /**
     * 拒绝添加好友结果消息
     */
    int getRefuseAddFriendType();

    int getServerSentReportMsgType();

    int getClientReceivedReportMsgType();


    int getCreateGroupResultMgsType();

    //聊天消息类型
    int getSingleChatMsgType();

    boolean isClosed();

    MsgTimeoutManager getMsgTimeoutTimerManager();


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

package com.sdt.nepush.ims;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alibaba.fastjson.JSONObject;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.OnEventListener;
import com.sdt.nepush.App;
import com.sdt.nepush.processor.MessageBuilder;
import com.sdt.nepush.processor.MessageProcessor;

import java.util.UUID;

/**
 * <p>@author:          ${Author}</p>
 * <p>@date:            ${Date}</p>
 * <p>@email:           ${Email}</p>
 * <b>
 * <p>@Description:     ${Description}</p>
 * </b>
 */
public class ImsOnEventListener implements OnEventListener {

    private String userId;
    private String token;

    public ImsOnEventListener(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    @Override
    public void dispatchMsg(TransMessageProtobuf.TransMessage msg) {
        MessageProcessor.getInstance().receiveMsg(MessageBuilder.getMessageByProtobuf(msg));
    }

    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    public int getReconnectInterval() {
        return 0;
    }

    @Override
    public int getConnectTimeout() {
        return 0;
    }

    @Override
    public int getForegroundHeartbeatInterval() {
        return 0;
    }

    @Override
    public int getBackgroundHeartbeatInterval() {
        return 0;
    }

    @Override
    public TransMessageProtobuf.TransMessage getHandshakeMsg() {
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(MessageType.HANDSHAKE.getMsgType());
        headBuilder.setFromId(userId);
        headBuilder.setTimestamp(System.currentTimeMillis());

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("token", token);
        headBuilder.setExtend(jsonObj.toString());
        builder.setHeader(headBuilder.build());
        return builder.build();
    }

    @Override
    public TransMessageProtobuf.TransMessage getHeartbeatMsg() {
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(MessageType.HEARTBEAT.getMsgType());
        headBuilder.setFromId(userId);
        headBuilder.setTimestamp(System.currentTimeMillis());
        builder.setHeader(headBuilder.build());

        return builder.build();
    }

    @Override
    public TransMessageProtobuf.TransMessage getFriendListMsg() {
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(MessageType.GET_USER_FRIEND_LIST.getMsgType());
        headBuilder.setFromId(userId);
        headBuilder.setTimestamp(System.currentTimeMillis());
        builder.setHeader(headBuilder.build());

        return builder.build();
    }

    @Override
    public int getSystemPushMsgType() {
        return MessageType.SYSTEMMESSAGE.getMsgType();
    }

    @Override
    public int getHeartbeatMsgType() {
        return MessageType.HEARTBEAT.getMsgType();
    }

    @Override
    public int getHandshakeMsgType() {
        return MessageType.HANDSHAKE.getMsgType();
    }

    @Override
    public int getForceLogoutMsgType() {
        return MessageType.FORCE_CLIENT_LOGOUT.getMsgType();
    }

    @Override
    public int getOutLineMsgListType() {
        return MessageType.GET_OUTLINE_MESSAGE_LIST.getMsgType();
    }

    @Override
    public int getRequestAddFriendType() {
        return MessageType.MESSAGE_REQUEST_ADD_FRIEND.getMsgType();
    }

    @Override
    public int getServerSentReportMsgType() {
        return MessageType.SERVER_MSG_SENT_STATUS_REPORT.getMsgType();
    }

    @Override
    public int getClientReceivedReportMsgType() {
        return MessageType.CLIENT_MSG_RECEIVED_STATUS_REPORT.getMsgType();
    }

    @Override
    public int getSingleChatMsgType() {
        return MessageType.SINGLE_CHAT.getMsgType();
    }

    @Override
    public int getResendCount() {
        return 0;
    }

    @Override
    public int getResendInterval() {
        return 0;
    }
}

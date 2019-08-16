package com.sdt.nepush.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.ImsManager;
import com.sdt.nepush.MessageType;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.msg.AppMessage;

import java.util.UUID;


/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       ServerReportMessageHandler.java</p>
 * <p>@PackageName:     com.freddy.chat.im.handler</p>
 * <b>
 * <p>@Description:     服务端返回的消息发送状态报告</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/22 19:16</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class HandShakeMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("握手结果，message=" + message);
        if (message != null && message.getHead() != null) {
            JSONObject jsonObj = JSON.parseObject(message.getHead().getExtend());
            int status = jsonObj.getIntValue("status");
            if (status == 1) {
                ImsManager.getInstance().sendMessage(buildMessage(), false);
                ImsManager.getInstance().sendMessage(buildMessage2(), false);
                //握手后需要做的事情，比如获取好友列表,获取离线消息，等等
            } else {
                logger.d("握手失败了...");//怎么做??重新登录??
            }
        }
    }

    private TransMessageProtobuf.TransMessage buildMessage() {
        User2Model user = User2Model.getLoginUser();
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgType(MessageType.GET_USER_FRIEND_LIST.getMsgType());
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setFromId(user.getUserName());
        headBuilder.setTimestamp(System.currentTimeMillis());
        builder.setHeader(headBuilder);
        TransMessageProtobuf.TransMessage message = builder.build();
        return message;
    }

    private TransMessageProtobuf.TransMessage buildMessage2() {
        User2Model user = User2Model.getLoginUser();
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgType(MessageType.GET_OUTLINE_MESSAGE_LIST.getMsgType());
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setFromId(user.getUserName());
        headBuilder.setTimestamp(System.currentTimeMillis());
        builder.setHeader(headBuilder);
        TransMessageProtobuf.TransMessage message = builder.build();
        return message;
    }
}

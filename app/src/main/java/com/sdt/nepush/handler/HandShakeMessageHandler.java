package com.sdt.nepush.handler;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.activity.LoginActivity;
import com.sdt.nepush.db.Message2Model;
import com.sdt.nepush.db.Message2Model_Table;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.ims.ImsManager;
import com.sdt.nepush.ims.MessageType;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.msg.AppMessage;

import java.util.List;
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
        if (message != null) {
            JSONObject jsonObj = JSON.parseObject(message.getExtend());
            int status = jsonObj.getIntValue("status");
            if (status == 1) {
                ImsManager.getInstance().sendMessage(buildMessage(), false);
                ImsManager.getInstance().sendMessage(buildMessage2(), false);
                ImsManager.getInstance().sendMessage(buildMessage3(), false);
                //握手后需要做的事情，比如获取好友列表,获取离线消息,发送失败的消息重发，等等
            } else if (status == -1) {
                logger.d("握手失败了...");//怎么做??重新登录??
                CEventCenter.dispatchEvent(Events.HANDSHAKE_MESSAGE, 0, 0, status);
                User2Model user2Model = User2Model.getLoginUser();
                user2Model.setToken("");
                user2Model.update();

                Intent intent = new Intent(App.getInstance(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getInstance().startActivity(intent);
            } else if (status == 0) {
                logger.d("客户端被踢下线了...");//怎么做??重新登录??
                CEventCenter.dispatchEvent(Events.HANDSHAKE_MESSAGE, 0, 0, status);
                User2Model user2Model = User2Model.getLoginUser();
                user2Model.setToken("");
                user2Model.update();
                Intent intent = new Intent(App.getInstance(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getInstance().startActivity(intent);
                CEventCenter.dispatchEvent(Events.HANDSHAKE_MESSAGE, 0, 0, status);
            }
        }
    }

    private TransMessageProtobuf.TransMessage buildMessage() {
        User2Model user = User2Model.getLoginUser();
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        builder.setMsgType(MessageType.GET_USER_FRIEND_LIST.getMsgType());
        builder.setMsgId(UUID.randomUUID().toString());
        builder.setFromId(user.getUserId());
        builder.setSendTime(System.currentTimeMillis());
        TransMessageProtobuf.TransMessage message = builder.build();
        return message;
    }

    private TransMessageProtobuf.TransMessage buildMessage2() {
        User2Model user = User2Model.getLoginUser();
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        builder.setMsgType(MessageType.GET_OUTLINE_MESSAGE_LIST.getMsgType());
        builder.setMsgId(UUID.randomUUID().toString());
        builder.setFromId(user.getUserId());
        builder.setSendTime(System.currentTimeMillis());
        TransMessageProtobuf.TransMessage message = builder.build();
        return message;
    }

    //失败消息重发
    private TransMessageProtobuf.TransMessage buildMessage3() {
        User2Model user = User2Model.getLoginUser();
        List<Message2Model> failedSendList = SQLite.select().from(Message2Model.class)
                .where(Message2Model_Table.statusReport.eq(-1)).queryList();
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        builder.setMsgType(MessageType.RESEND_FAILED_MESSAGE_LIST.getMsgType());
        builder.setMsgId(UUID.randomUUID().toString());
        builder.setFromId(user.getUserId());
        builder.setSendTime(System.currentTimeMillis());
        builder.setContent(failedSendList.toString());
        TransMessageProtobuf.TransMessage message = builder.build();
        return message;
    }
}

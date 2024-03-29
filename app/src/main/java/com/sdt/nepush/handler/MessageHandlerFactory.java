package com.sdt.nepush.handler;

import android.util.SparseArray;

import com.sdt.nepush.ims.MessageType;


/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       MessageHandlerFactory.java</p>
 * <p>@PackageName:     com.freddy.chat.im.handler</p>
 * <b>
 * <p>@Description:     消息处理handler工厂</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 03:44</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class MessageHandlerFactory {

    private MessageHandlerFactory() {

    }

    private static final SparseArray<IMessageHandler> HANDLERS = new SparseArray<>();

    static {
        /** 服务器发送消息处理handler */
        HANDLERS.put(MessageType.SYSTEMMESSAGE.getMsgType(), new SysPushMessageHandler());
        /** 单聊消息处理handler */
        HANDLERS.put(MessageType.SINGLE_CHAT.getMsgType(), new SingleChatMessageHandler());
        /** 群聊消息处理handler */
        HANDLERS.put(MessageType.GROUP_CHAT.getMsgType(), new GroupChatMessageHandler());
        /** 服务端返回的消息发送状态报告处理handler */
        HANDLERS.put(MessageType.SERVER_MSG_SENT_STATUS_REPORT.getMsgType(), new ServerReportMessageHandler());

        HANDLERS.put(MessageType.HANDSHAKE.getMsgType(), new HandShakeMessageHandler());

        HANDLERS.put(MessageType.GET_USER_FRIEND_LIST.getMsgType(), new FriendListMessageHandler());

        HANDLERS.put(MessageType.GET_OUTLINE_MESSAGE_LIST.getMsgType(), new OutlineListMessageHandler());
        HANDLERS.put(MessageType.FORCE_CLIENT_LOGOUT.getMsgType(), new ForceLogoutMessageHandler());
        HANDLERS.put(MessageType.MESSAGE_REQUEST_ADD_FRIEND.getMsgType(), new HandleAddFriendHandler());
        HANDLERS.put(MessageType.MESSAGE_AGREE_ADD_FRIEND_RESULT.getMsgType(), new HandleAgreeAddFriendHandler());
        HANDLERS.put(MessageType.MESSAGE_REFUSE_ADD_FRIEND_RESULT.getMsgType(), new HandleRefuseAddFriendHandler());
    }

    /**
     * 根据消息类型获取对应的处理handler
     *
     * @param msgType
     * @return
     */
    public static IMessageHandler getHandlerByMsgType(int msgType) {
        return HANDLERS.get(msgType);
    }
}

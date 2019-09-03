package com.sdt.nepush.processor;


import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.nepush.util.StringUtil;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.msg.BaseMessage;
import com.sdt.nepush.msg.ContentMessage;
import com.sdt.nepush.msg.Head;

/**
 * <p>@ProjectName:     BoChat</p>
 * <p>@ClassName:       MessageBuilder.java</p>
 * <p>@PackageName:     com.bochat.app.message</p>
 * <b>
 * <p>@Description:     消息转换</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/02/07 17:26</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class MessageBuilder {

    /**
     * 根据聊天消息，生成一条可以能够传输通讯的消息
     *
     * @param msgId
     * @param type
     * @param subType
     * @param fromId
     * @param toId
     * @param extend
     * @param content
     * @return
     */
    public static AppMessage buildAppMessage(String msgId, int type, int subType, String fromId,
                                             String toId, String extend, String content) {
        AppMessage message = new AppMessage();
        Head head = new Head();
        head.setMessageId(msgId);
        head.setMessageType(type);
        head.setMessageContentType(subType);
        head.setFromId(fromId);
        head.setToId(toId);
        head.setExtend(extend);
        message.setHead(head);
        message.setBody(content);

        return message;
    }

    /**
     * 根据聊天消息，生成一条可以能够传输通讯的消息
     *
     * @param msg
     * @return
     */
    public static AppMessage buildAppMessage(ContentMessage msg) {
        AppMessage message = new AppMessage();
        Head head = new Head();
        head.setMessageId(msg.getMsgId());
        head.setMessageType(msg.getMsgType());
        head.setMessageContentType(msg.getMsgContentType());
        head.setFromId(msg.getFromId());
        head.setToId(msg.getToId());
        head.setSendTime(msg.getSendTime());
        head.setExtend(msg.getExtend());
        message.setHead(head);
        message.setBody(msg.getContent());

        return message;
    }

    /**
     * 根据聊天消息，生成一条可以能够传输通讯的消息
     *
     * @param msg
     * @return
     */
    public static AppMessage buildAppMessage(BaseMessage msg) {
        AppMessage message = new AppMessage();
        Head head = new Head();
        head.setMessageId(msg.getMsgId());
        head.setMessageType(msg.getMsgType());
        head.setMessageContentType(msg.getMsgContentType());
        head.setFromId(msg.getFromId());
        head.setToId(msg.getToId());
        head.setExtend(msg.getExtend());
        head.setSendTime(msg.getSendTime());
        message.setHead(head);
        message.setBody(msg.getContent());

        return message;
    }

    /**
     * 根据业务消息对象获取protoBuf消息对应的builder
     *
     * @param message
     * @return
     */
    public static TransMessageProtobuf.TransMessage.Builder getProtoBufMessageBuilderByAppMessage(AppMessage message) {
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgType(message.getHead().getMessageType());
        headBuilder.setStatusReport(message.getHead().getStatusReport());
        headBuilder.setMsgContentType(message.getHead().getMessageContentType());
        if (!StringUtil.isEmpty(message.getHead().getMessageId()))
            headBuilder.setMsgId(message.getHead().getMessageId());
        if (!StringUtil.isEmpty(message.getHead().getFromId()))
            headBuilder.setFromId(message.getHead().getFromId());
        if (!StringUtil.isEmpty(message.getHead().getToId()))
            headBuilder.setToId(message.getHead().getToId());
        if (message.getHead().getSendTime() != 0)
            headBuilder.setTimestamp(message.getHead().getSendTime());
        if (!StringUtil.isEmpty(message.getHead().getExtend()))
            headBuilder.setExtend(message.getHead().getExtend());
        if (!StringUtil.isEmpty(message.getBody()))
            builder.setBody(message.getBody());
        builder.setHeader(headBuilder);
        return builder;
    }

    /**
     * 通过protobuf消息对象获取业务消息对象
     *
     * @param protobufMessage
     * @return
     */
    public static AppMessage getMessageByProtobuf(TransMessageProtobuf.TransMessage protobufMessage) {
        AppMessage message = new AppMessage();
        Head head = new Head();
        TransMessageProtobuf.MessageHeader protoHead = protobufMessage.getHeader();
        head.setMessageType(protoHead.getMsgType());
        head.setStatusReport(protoHead.getStatusReport());
        head.setMessageContentType(protoHead.getMsgContentType());
        head.setMessageId(protoHead.getMsgId());
        head.setFromId(protoHead.getFromId());
        head.setToId(protoHead.getToId());
        head.setSendTime(protoHead.getTimestamp());
        head.setExtend(protoHead.getExtend());
        message.setHead(head);
        message.setBody(protobufMessage.getBody());
        return message;
    }
}

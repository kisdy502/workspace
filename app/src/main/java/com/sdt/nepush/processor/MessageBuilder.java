package com.sdt.nepush.processor;


import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.util.StringUtil;

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
    public static AppMessage buildAppMessage(String msgId, int type, int subType, Long fromId,
                                             Long toId, String extend, String content) {
        AppMessage message = new AppMessage();
        message.setMsgId(msgId);
        message.setMsgType(type);
        message.setMsgContentType(subType);
        message.setFromId(fromId);
        message.setToId(toId);
        message.setExtend(extend);
        message.setContent(content);
        return message;
    }



    /**
     * 根据业务消息对象获取protoBuf消息对应的builder
     *
     * @param message
     * @return
     */
    public static TransMessageProtobuf.TransMessage getProtoBufMessageBuilderByAppMessage(AppMessage message) {
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        builder.setMsgType(message.getMsgType());
        builder.setStatusReport(message.getStatusReport());
        builder.setMsgContentType(message.getMsgContentType());
        if (!StringUtil.isEmpty(message.getMsgId()))
            builder.setMsgId(message.getMsgId());
        if (!StringUtil.isEmpty(message.getFromId()))
            builder.setFromId(message.getFromId());
        if (!StringUtil.isEmpty(message.getToId()))
            builder.setToId(message.getToId());
        if (message.getSendTime() != 0)
            builder.setSendTime(message.getSendTime());
        if (!StringUtil.isEmpty(message.getExtend()))
            builder.setExtend(message.getExtend());
        if (!StringUtil.isEmpty(message.getContent()))
            builder.setContent(message.getContent());
        return builder.build();
    }

    /**
     * 通过protobuf消息对象获取业务消息对象
     *
     * @param protobufMessage
     * @return
     */
    public static AppMessage getMessageByProtobuf(TransMessageProtobuf.TransMessage protobufMessage) {
        AppMessage message = new AppMessage();
        message.setMsgType(protobufMessage.getMsgType());
        message.setStatusReport(protobufMessage.getStatusReport());
        message.setMsgContentType(protobufMessage.getMsgContentType());
        message.setMsgId(protobufMessage.getMsgId());
        message.setFromId(protobufMessage.getFromId());
        message.setToId(protobufMessage.getToId());
        message.setSendTime(protobufMessage.getSendTime());
        message.setExtend(protobufMessage.getExtend());
        message.setContent(protobufMessage.getContent());
        return message;
    }
}

package com.sdt.libchat;

import com.sdt.im.protobuf.TransMessageProtobuf;

import io.netty.util.internal.StringUtil;

/**
 * <p>@author:          ${Author}</p>
 * <p>@date:            ${Date}</p>
 * <p>@email:           ${Email}</p>
 * <b>
 * <p>@Description:     ${Description}</p>
 * </b>
 */
public class MessageHelper {

    public static TransMessageProtobuf.TransMessage buildReceivedReportMsg(TransMessageProtobuf.TransMessage
                                                                                   transMessage, int reportMsgType) {
        if (StringUtil.isNullOrEmpty(transMessage.getMsgId())) {
            return null;
        }
        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        builder.setMsgId(transMessage.getMsgId());
        builder.setMsgType(reportMsgType);
        builder.setFromId(transMessage.getToId());
        builder.setToId(transMessage.getFromId());
        builder.setSendTime(System.currentTimeMillis());
        return builder.build();
    }
}

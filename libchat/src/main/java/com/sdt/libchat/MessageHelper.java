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

    public static TransMessageProtobuf.TransMessage buildReceivedReportMsg(String msgId, int reportMsgType) {
        if (StringUtil.isNullOrEmpty(msgId)) {
            return null;
        }

        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
        headBuilder.setMsgId(msgId);
        headBuilder.setMsgType(reportMsgType);
        headBuilder.setTimestamp(System.currentTimeMillis());
        builder.setHeader(headBuilder.build());

        return builder.build();
    }
}

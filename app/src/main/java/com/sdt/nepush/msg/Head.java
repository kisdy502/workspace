package com.sdt.nepush.msg;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       Head.java</p>
 * <p>@PackageName:     com.freddy.chat.bean</p>
 * <b>
 * <p>@Description:     消息头</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 00:00</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class Head {

    private String messageId;
    private int messageType;
    private int messageContentType;
    private String fromId;
    private String toId;
    private long sendTime;
    private int statusReport;
    private String extend;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageContentType() {
        return messageContentType;
    }

    public void setMessageContentType(int messageContentType) {
        this.messageContentType = messageContentType;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getStatusReport() {
        return statusReport;
    }

    public void setStatusReport(int statusReport) {
        this.statusReport = statusReport;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    @Override
    public String toString() {
        return "Head{" +
                "messageId='" + messageId + '\'' +
                ", messageType=" + messageType +
                ", messageContentType=" + messageContentType +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", sendTime=" + sendTime +
                ", statusReport=" + statusReport +
                ", extend='" + extend + '\'' +
                '}';
    }
}

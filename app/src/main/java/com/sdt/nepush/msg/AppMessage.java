package com.sdt.nepush.msg;


import com.google.gson.Gson;
import com.sdt.nepush.util.StringUtil;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       AppMessage.java</p>
 * <p>@PackageName:     com.freddy.chat.bean</p>
 * <b>
 * <p>@Description:     消息基类</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 00:02</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class AppMessage {

    protected String msgId;       // 消息id
    protected int msgType;        // 消息类型
    protected int msgContentType; // 消息内容乐行
    protected Long fromId;      // 发送者id
    protected Long toId;        // 接收者id
    protected long sendTime;     // 消息时间戳
    protected int statusReport;   // 消息状态报告
    protected String extend;      // 扩展字段，以key/value形式存放json
    protected String content;     // 消息内容

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgContentType() {
        return msgContentType;
    }

    public void setMsgContentType(int msgContentType) {
        this.msgContentType = msgContentType;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        try {
            return this.msgId.hashCode();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AppMessage)) {
            return false;
        }

        return StringUtil.equals(this.msgId, ((AppMessage) obj).getMsgId());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

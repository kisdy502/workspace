package com.sdt.nepush.db;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by sdt13411 on 2019/7/22.
 */
@Table(database = NetPush.class)
public class Message2Model extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    protected String messageId;       // 消息id
    @Column
    protected int messageType;        // 消息类型
    @Column
    protected int msgContentType; // 消息内容乐行
    @Column
    protected Long fromId;      // 发送者id
    @Column
    protected Long toId;        // 接收者id
    @Column
    protected long sendTime;     // 消息时间戳
    @Column
    protected int statusReport;   // 消息状态报告
    @Column
    protected String extend;      // 扩展字段，以key/value形式存放json
    @Column
    protected String content;     // 消息内容

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
    public String toString() {
        return new Gson().toJson(this);
    }
}

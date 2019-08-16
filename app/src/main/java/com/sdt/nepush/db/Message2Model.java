package com.sdt.nepush.db;

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
    protected String msgId;       // 消息id
    @Column
    protected int msgType;        // 消息类型
    @Column
    protected int msgContentType; // 消息内容乐行
    @Column
    protected String fromId;      // 发送者id
    @Column
    protected String toId;        // 接收者id
    @Column
    protected long timestamp;     // 消息时间戳
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
}

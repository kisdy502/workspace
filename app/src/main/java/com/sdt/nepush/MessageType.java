package com.sdt.nepush;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       MessageType.java</p>
 * <p>@PackageName:     com.freddy.chat.im</p>
 * <b>
 * <p>@Description:     消息类型</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/08 00:04</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public enum MessageType {

    /*
    * 系统推送消息
    */
    SYSTEMMESSAGE(1000),

    /*
     * 握手消息
     */
    HANDSHAKE(1001),

    /*
     * 心跳消息
     */
    HEARTBEAT(1002),

    /*
     * 客户端提交的消息接收状态报告
     */
    CLIENT_MSG_RECEIVED_STATUS_REPORT(1009),

    /*
     * 服务端返回的消息发送状态报告
     */
    SERVER_MSG_SENT_STATUS_REPORT(1010),

    /*
     * 客户端强制下线，在其它地方登录，被踢下线
     */
    FORCE_CLIENT_LOGOUT(1003),

    /*
     * 添加好友
     */
    MESSAGE_REQUEST_ADD_FRIEND(1011),

    /*
     * 同意添加好友
     */
    MESSAGE_AGREE_ADD_FRIEND(1012),


    /*
     * 返回好友列表
     */
    GET_USER_FRIEND_LIST(1101),

    /*
    * 离线消息
    */
    GET_OUTLINE_MESSAGE_LIST(1103),


    /*
    * 上报收到的离线消息列表
    */
    REPORT_RECEIVED_OUTLINE_MESSAGE_LIST(1105),

    /**
     * 单聊消息
     */
    SINGLE_CHAT(2001),

    /**
     * 群聊消息
     */
    GROUP_CHAT(3001);

    private int msgType;

    MessageType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public enum MessageContentType {

        /**
         * 文本消息
         */
        TEXT(101),

        /**
         * 图片消息
         */
        IMAGE(102),

        /**
         * 语音消息
         */
        VOICE(103);

        private int msgContentType;

        MessageContentType(int msgContentType) {
            this.msgContentType = msgContentType;
        }

        public int getMsgContentType() {
            return this.msgContentType;
        }
    }
}

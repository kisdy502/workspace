package com.sdt.libchat;


import com.sdt.im.protobuf.TransMessageProtobuf;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       OnEventListener.java</p>
 * <p>@PackageName:     com.freddy.im.listener</p>
 * <b>
 * <p>@Description:     与应用层交互的listener</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/03/31 20:06</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public interface OnEventListener {

    /**
     * 分发消息到应用层
     *
     * @param msg
     */
    void dispatchMsg(TransMessageProtobuf.TransMessage msg);

    /**
     * 从应用层获取网络是否可用
     *
     * @return
     */
    boolean isNetworkAvailable();

    /**
     * 获取重连间隔时长
     *
     * @return
     */
    int getReconnectInterval();

    /**
     * 获取连接超时时长
     *
     * @return
     */
    int getConnectTimeout();

    /**
     * 获取应用在前台时心跳间隔时间
     *
     * @return
     */
    int getForegroundHeartbeatInterval();

    /**
     * 获取应用在前台时心跳间隔时间
     *
     * @return
     */
    int getBackgroundHeartbeatInterval();

    /**
     * 获取由应用层构造的握手消息
     *
     * @return
     */
    TransMessageProtobuf.TransMessage getHandshakeMsg();

    /**
     * 获取由应用层构造的心跳消息
     *
     * @return
     */
    TransMessageProtobuf.TransMessage getHeartbeatMsg();

    /**
     * 获取朋友列表
     *
     * @return
     */
    TransMessageProtobuf.TransMessage getFriendListMsg();

    /**
     * 服务器推送的消息类型
     *
     * @return
     */
    int getSystemPushMsgType();

    int getHeartbeatMsgType();

    int getHandshakeMsgType();

    /**
     * 强制退出
     *
     * @return
     */
    int getForceLogoutMsgType();

    /**
     * 获取离线消息类型
     */
    int getOutLineMsgListType();

    /**
     * 请求添加好友
     */
    int getRequestAddFriendType();

    /**
     * 同意添加好友结果消息
     */
    int getAgreeAddFriendType();

    /**
     * 拒绝添加好友结果消息
     */
    int getRefuseAddFriendType();


    /**
     * 获取应用层消息发送状态报告消息类型
     */
    int getServerSentReportMsgType();

    /**
     * 获取应用层消息接收状态报告消息类型
     */
    int getClientReceivedReportMsgType();


    /**
     * 群组创建请求返回消息类型
     */
    int getCreateGroupResultMgsType();

    //聊天消息类型
    int getSingleChatMsgType();

    /**
     * 获取应用层消息发送超时重发次数
     *
     * @return
     */
    int getResendCount();

    /**
     * 获取应用层消息发送超时重发间隔
     *
     * @return
     */
    int getResendInterval();


}

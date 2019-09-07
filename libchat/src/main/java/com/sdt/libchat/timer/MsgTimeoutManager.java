package com.sdt.libchat.timer;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.core.ImsClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.StringUtil;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class MsgTimeoutManager {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private Map<String, MsgTimeoutTimer> mMsgTimeoutMap = new ConcurrentHashMap<>();
    private ImsClient imsClient;// ims客户端

    public MsgTimeoutManager(ImsClient imsClient) {
        this.imsClient = imsClient;
    }

    public void add(TransMessageProtobuf.TransMessage msg) {
        if (msg == null) {
            return;
        }

        int handshakeMsgType = -1;
        int heartbeatMsgType = -1;

        int clientReceivedReportMsgType = imsClient.getClientReceivedReportMsgType();
        TransMessageProtobuf.TransMessage handshakeMsg = imsClient.getHandshakeMsg();
        if (handshakeMsg != null ) {
            handshakeMsgType = handshakeMsg.getMsgType();
        }
        TransMessageProtobuf.TransMessage heartbeatMsg = imsClient.getHeartbeatMsg();
        if (heartbeatMsg != null ) {
            heartbeatMsgType = heartbeatMsg.getMsgType();
        }

        int msgType = msg.getMsgType();
        // 握手消息、心跳消息、客户端返回的状态报告消息，不用重发。
        if (msgType == handshakeMsgType || msgType == heartbeatMsgType || msgType == clientReceivedReportMsgType) {
            return;
        }

        String msgId = msg.getMsgId();
        if (!mMsgTimeoutMap.containsKey(msgId)) {
            MsgTimeoutTimer timer = new MsgTimeoutTimer(imsClient, msg);
            mMsgTimeoutMap.put(msgId, timer);
        }

        logger.d("添加消息超发送超时管理器，message=" + msg + "\t当前管理器消息数：" + mMsgTimeoutMap.size());
    }

    public void remove(String msgId) {
        if (StringUtil.isNullOrEmpty(msgId)) {
            return;
        }

        MsgTimeoutTimer timer = mMsgTimeoutMap.remove(msgId);
        TransMessageProtobuf.TransMessage msg = null;
        if (timer != null) {
            msg = timer.getMsg();
            timer.cancel();
            timer = null;
        }

        logger.i("从发送消息管理器移除消息，message=" + msg);
    }

    /**
     * 重连成功回调，重连并握手成功时，重发消息发送超时管理器中所有的消息
     */
    public synchronized void onReSendAll() {
        for(Iterator<Map.Entry<String, MsgTimeoutTimer>> it = mMsgTimeoutMap.entrySet().iterator(); it.hasNext();) {
            it.next().getValue().sendMsg();
        }
    }
}

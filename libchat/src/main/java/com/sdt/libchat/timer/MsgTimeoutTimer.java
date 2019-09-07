package com.sdt.libchat.timer;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.core.ImsClient;
import com.sdt.libchat.core.ImsConfig;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 默认8秒，重试三次，即消息发出去8秒后如果还没收到消息发送成功状态报告，就要重发
 * Created by sdt13411 on 2019/7/17.
 */

public class MsgTimeoutTimer extends Timer {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private ImsClient imsClient;// ims客户端
    private TransMessageProtobuf.TransMessage msg;// 发送的消息
    private int currentResendCount;               // 当前重发次数
    private MsgTimeoutTask task;                  // 消息发送超时任务

    public MsgTimeoutTimer(ImsClient imsclient, TransMessageProtobuf.TransMessage msg) {
        this.imsClient = imsclient;
        this.msg = msg;
        task = new MsgTimeoutTask();
        this.schedule(task, imsClient.getResendInterval(), imsClient.getResendInterval());
    }


    public TransMessageProtobuf.TransMessage getMsg() {
        return msg;
    }

    public void sendMsg() {
        logger.d("正在重发消息，message=" + msg);
        imsClient.sendMsg(msg, false);
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        super.cancel();
    }

    private class MsgTimeoutTask extends TimerTask {

        @Override
        public void run() {
            if (imsClient.isClosed()) {
                if (imsClient.getMsgTimeoutTimerManager() != null) {
                    imsClient.getMsgTimeoutTimerManager().remove(msg.getMsgId());
                }
                return;
            }

            currentResendCount++;
            if (currentResendCount > imsClient.getResendCount()) {
                // 重发次数大于可重发次数，直接标识为发送失败，并通过消息转发器通知应用层
                try {
                    TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
                    builder.setMsgId(msg.getMsgId());
                    builder.setMsgType(imsClient.getServerSentReportMsgType());
                    builder.setSendTime(System.currentTimeMillis());
                    builder.setStatusReport(ImsConfig.DEFAULT_REPORT_SERVER_SEND_MSG_FAILURE);
                    TransMessageProtobuf.TransMessage transMessage = builder.build();
                    // 通知应用层消息发送失败
                    imsClient.getMsgDispatcher().receivedMsg(transMessage);
                } finally {
                    // 从消息发送超时管理器移除该消息
                    imsClient.getMsgTimeoutTimerManager().remove(msg.getMsgId());
                    // 执行到这里，认为连接已断开或不稳定，触发重连
                    //imsClient.prepareConnect(false);  //这里似乎会触发无限重连的bug，先注释掉看看
                    currentResendCount = 0;
                }
            } else {
                // 发送消息，但不再加入超时管理器，达到最大发送失败次数就算了
                sendMsg();
            }
        }
    }
}

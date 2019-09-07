package com.sdt.nepush.processor;

import android.util.Log;

import com.sdt.nepush.ims.ImsManager;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.handler.IMessageHandler;
import com.sdt.nepush.handler.MessageHandlerFactory;
import com.sdt.nepush.util.CThreadPoolExecutor;


/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       MessageProcessor.java</p>
 * <p>@PackageName:     com.freddy.chat.im</p>
 * <b>
 * <p>@Description:     消息处理器</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 03:27</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class MessageProcessor implements IMessageProcessor {

    private static final String TAG = MessageProcessor.class.getSimpleName();

    private MessageProcessor() {

    }

    private static class MessageProcessorInstance {
        private static final IMessageProcessor INSTANCE = new MessageProcessor();
    }

    public static IMessageProcessor getInstance() {
        return MessageProcessorInstance.INSTANCE;
    }

    /**
     * 接收消息
     *
     * @param message
     */
    @Override
    public void receiveMsg(final AppMessage message) {
        CThreadPoolExecutor.runInBackground(new Runnable() {

            @Override
            public void run() {
                try {
                    IMessageHandler messageHandler = MessageHandlerFactory.getHandlerByMsgType(message.getMsgType());
                    if (messageHandler != null) {
                        messageHandler.execute(message);
                    } else {
                        Log.e(TAG, "未找到消息处理handler，messageType=" + message.getMsgType());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "消息处理出错，reason=" + e.getMessage());
                }
            }
        });
    }

    /**
     * 发送消息
     *
     * @param message
     */
    @Override
    public void sendMsg(AppMessage message) {
        boolean isActive = ImsManager.getInstance().isInited();
        if (isActive) {
            ImsManager.getInstance().sendMessage(MessageBuilder.getProtoBufMessageBuilderByAppMessage(message));
        } else {
            Log.e(TAG, "发送消息失败");
        }
    }
}

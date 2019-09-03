package com.sdt.nepush.handler;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.msg.SingleMessage;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.notification.NotificationHelper;


/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       SingleChatMessageHandler.java</p>
 * <p>@PackageName:     com.freddy.chat.im.handler</p>
 * <b>
 * <p>@Description:     类描述</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 03:43</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class SysPushMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private static final String TAG = SysPushMessageHandler.class.getSimpleName();

    @Override
    protected void action(AppMessage message) {

        logger.d(TAG, "收到服务器推送消息，message=" + message);

        SingleMessage msg = new SingleMessage();
        msg.setMsgId(message.getHead().getMessageId());
        msg.setMsgType(message.getHead().getMessageType());
        msg.setMsgContentType(message.getHead().getMessageContentType());
        msg.setFromId(message.getHead().getFromId());
        msg.setToId(message.getHead().getToId());
        msg.setSendTime(message.getHead().getSendTime());
        msg.setExtend(message.getHead().getExtend());
        msg.setContent(message.getBody());

        CEventCenter.dispatchEvent(Events.SYS_PUSH_MESSAGE, 0, 0, msg);
        //TODO 后台发通知
        NotificationHelper.notifyMessage(App.getInstance(), message);
    }
}

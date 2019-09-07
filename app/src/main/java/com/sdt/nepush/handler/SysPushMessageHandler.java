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
        logger.d(TAG, "收到服务器推送消息，message=" + message.toString());
        SingleMessage msg = new SingleMessage();
        msg.setMsgId(message.getMsgId());
        msg.setMsgType(message.getMsgType());
        msg.setMsgContentType(message.getMsgContentType());
        msg.setFromId(message.getFromId());
        msg.setToId(message.getToId());
        msg.setSendTime(message.getSendTime());
        msg.setExtend(message.getExtend());
        msg.setContent(message.getContent());

        CEventCenter.dispatchEvent(Events.SYS_PUSH_MESSAGE, 0, 0, msg);
        //TODO 后台发通知
        NotificationHelper.notifyMessage(App.getInstance(), message);
    }
}

package com.sdt.nepush.handler;


import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.ims.ImsManager;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.notification.NotificationHelper;

public class ForceLogoutMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("下线通知:message=" + message.toString());
        ImsManager.getInstance().close();
        CEventCenter.dispatchEvent(Events.FORCE_LOGOUT_MESSAGE, 0, 0, null);
        //TODO 什么时候需要发送到通知,逻辑
        NotificationHelper.notifyMessage(App.getInstance(), message);
    }

}

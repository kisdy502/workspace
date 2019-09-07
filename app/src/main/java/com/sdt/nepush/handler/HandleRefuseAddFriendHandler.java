package com.sdt.nepush.handler;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;

public class HandleRefuseAddFriendHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("拒绝了你添加好友:message=" + message.toString());
        CEventCenter.dispatchEvent(Events.REFUSE_ADD_FRIEND_MESSAGE, 0, 0, null);


    }

}

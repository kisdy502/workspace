package com.sdt.nepush.handler;

import android.content.Intent;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.activity.HandleAddFriendActivity;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;

public class HandleAddFriendHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("添加好友:message=" + message);
        if (message != null && message.getHead() != null) {
            String fromdId = message.getHead().getFromId();
            CEventCenter.dispatchEvent(Events.ADD_FRIEND_MESSAGE, 0, 0, null);

            Intent intent = new Intent(App.getInstance(), HandleAddFriendActivity.class);
            intent.putExtra("fromId", fromdId);
            intent.putExtra("tipMessage", message.getBody());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(intent);
        }
    }

}

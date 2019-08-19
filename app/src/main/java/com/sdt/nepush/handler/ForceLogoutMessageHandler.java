package com.sdt.nepush.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.ImsManager;
import com.sdt.nepush.bean.FriendBean;
import com.sdt.nepush.db.UserRelation2Model;
import com.sdt.nepush.db.UserRelation2Model_Table;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;

import java.lang.reflect.Type;
import java.util.List;

public class ForceLogoutMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("下线通知:message=" + message);
        if (message != null && message.getHead() != null) {
            ImsManager.getInstance().close();
            CEventCenter.dispatchEvent(Events.FORCE_LOGOUT_MESSAGE, 0, 0, null);
        }
    }

}

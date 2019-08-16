package com.sdt.nepush.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.bean.FriendBean;
import com.sdt.nepush.db.UserRelation2Model;
import com.sdt.nepush.db.UserRelation2Model_Table;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;

import java.lang.reflect.Type;
import java.util.List;

public class OutlineListMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("离线消息:message=" + message);
        if (message != null && message.getHead() != null) {
            String messageListString = message.getBody();

            CEventCenter.dispatchEvent(Events.LIST_OUTLINE_MESSAGE, 0, 0, null);
        }
    }

}

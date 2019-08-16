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

public class FriendListMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("好友列表:message=" + message);
        if (message != null && message.getHead() != null) {
            String friendListString = message.getBody();

            Type type = new TypeToken<List<FriendBean>>() {
            }.getType();
            List<FriendBean> friendBeanList = new Gson().fromJson(friendListString, type);
            if (friendBeanList != null && friendBeanList.size() > 0) {
                for (FriendBean friendBean : friendBeanList) {
                    UserRelation2Model userRelation2Model = SQLite.select().from(UserRelation2Model.class)
                            .where(UserRelation2Model_Table.relationId.eq(friendBean.getId()))
                            .querySingle();
                    if (userRelation2Model == null) {
                        userRelation2Model = new UserRelation2Model();
                        userRelation2Model.setRelationId(friendBean.getId());
                        userRelation2Model.setMyName(friendBean.getMyName());
                        userRelation2Model.setFriendName(friendBean.getFriendName());
                        userRelation2Model.save();
                    }
                }
            }

            CEventCenter.dispatchEvent(Events.LIST_FRIEND_MESSAGE, 0, 0, null);
        }
    }

}

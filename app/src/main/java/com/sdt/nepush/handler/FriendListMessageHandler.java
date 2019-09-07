package com.sdt.nepush.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.bean.Friend;
import com.sdt.nepush.db.Friend2Model;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;

import java.lang.reflect.Type;
import java.util.List;

public class FriendListMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("好友列表:message=" + message.toString());
        String friendListString = message.getContent();

        Type type = new TypeToken<List<Friend>>() {
        }.getType();
        List<Friend> friendBeanList = new Gson().fromJson(friendListString, type);
        if (friendBeanList != null && friendBeanList.size() > 0) {
            for (Friend friendBean : friendBeanList) {
                Friend2Model friend2Model = Friend2Model.findFriendByFriendId(friendBean.getFriendId());
                if (friend2Model == null) {
                    friend2Model = new Friend2Model();
                    friend2Model.setMyId(friendBean.getMyId());
                    friend2Model.setFriendId(friendBean.getFriendId());
                    friend2Model.setUserName(friendBean.getName());
                    friend2Model.setTimeStamp(System.currentTimeMillis());
                    friend2Model.save();
                } else {
                    friend2Model.setUserName(friendBean.getName());
                    friend2Model.setTimeStamp(System.currentTimeMillis());
                    friend2Model.update();
                }
            }
        }

        CEventCenter.dispatchEvent(Events.LIST_FRIEND_MESSAGE, 0, 0, null);
    }
}

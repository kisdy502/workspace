package com.sdt.nepush.handler;

import com.google.gson.Gson;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.bean.Friend;
import com.sdt.nepush.db.Friend2Model;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;

public class HandleAgreeAddFriendHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("同意了添加好友请求:message=" + message.toString());
        String friendStr = message.getContent();
        Friend friend = new Gson().fromJson(friendStr, Friend.class);
        Friend2Model friend2Model =
                Friend2Model.findFriendByMyIdAndFriendId(friend.getMyId(), friend.getFriendId());
        if (friend2Model == null) {
            friend2Model = new Friend2Model();
            friend2Model.setMyId(friend.getMyId());
            friend2Model.setFriendId(friend.getFriendId());
            friend2Model.setUserName(friend.getName());
            friend2Model.setTimeStamp(System.currentTimeMillis());
            friend2Model.save();
        }
        CEventCenter.dispatchEvent(Events.AGREE_ADD_FRIEND_MESSAGE, 0, 0, friend2Model);
    }

}

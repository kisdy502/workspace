package com.sdt.nepush.handler;

import android.content.Intent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
        logger.d("添加好友:message=" + message.toString());
        Long fromdId = message.getFromId();
        String content = message.getContent();
        JsonParser parser = new JsonParser();
        //通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
        JsonElement el = parser.parse(content);
        JsonObject jsonObject = el.getAsJsonObject();
        CEventCenter.dispatchEvent(Events.ADD_FRIEND_MESSAGE, 0, 0, null);
        Long id = jsonObject.get("id").getAsLong();
        String name = jsonObject.get("name").getAsString();
        String mobile = jsonObject.get("mobile").getAsString();
        String tip = jsonObject.get("tip").getAsString();

        Intent intent = new Intent(App.getInstance(), HandleAddFriendActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("mobile", mobile);
        intent.putExtra("tip", tip);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getInstance().startActivity(intent);
    }

    private void testParse() {

    }

}

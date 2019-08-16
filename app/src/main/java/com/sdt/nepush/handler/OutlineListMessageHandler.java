package com.sdt.nepush.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.bean.FriendBean;
import com.sdt.nepush.db.Message2Model;
import com.sdt.nepush.db.Message2Model_Table;
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

            Gson gson = new Gson();

            JsonParser parser = new JsonParser();
            //通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
            JsonElement el = parser.parse(messageListString);
            JsonArray jsonArray = el.getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String msgId = jsonObject.get("messageId").getAsString();

                //避免插入重复数据
                Message2Model dbMessage = SQLite.select().from(Message2Model.class).where(Message2Model_Table.msgId.eq(msgId)).querySingle();
                if (dbMessage != null) {
                    continue;
                }

                int msgType = jsonObject.get("messageType").getAsInt();
                int msgContentType = jsonObject.get("messageContentType").getAsInt();
                String fromId = jsonObject.get("fromId").getAsString();
                String toId = jsonObject.get("toId").getAsString();
                long timestamp = jsonObject.get("sendTime").getAsLong();
                int statusReport = jsonObject.get("messageReportStatus").getAsInt();
                String content = jsonObject.get("content").getAsString();
                Message2Model message2Model = new Message2Model();
                message2Model.setMsgId(msgId);
                message2Model.setFromId(fromId);
                message2Model.setMsgType(msgType);
                message2Model.setMsgContentType(msgContentType);
                message2Model.setToId(toId);
                message2Model.setTimestamp(timestamp);
                message2Model.setStatusReport(statusReport);
                message2Model.setExtend("");
                message2Model.setContent(content);
                message2Model.save();
            }

            CEventCenter.dispatchEvent(Events.LIST_OUTLINE_MESSAGE, 0, 0, null);
        }
    }

}

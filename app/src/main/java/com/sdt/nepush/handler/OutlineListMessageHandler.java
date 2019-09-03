package com.sdt.nepush.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.ims.ImsManager;
import com.sdt.nepush.ims.MessageType;
import com.sdt.nepush.db.Message2Model;
import com.sdt.nepush.db.Message2Model_Table;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;

import java.util.UUID;

public class OutlineListMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("离线消息:message=" + message);
        if (message != null && message.getHead() != null) {
            StringBuilder sbMsgIds = new StringBuilder();           //回复服务器消息送达
            String messageListString = message.getBody();

            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            //通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
            JsonElement el = parser.parse(messageListString);
            JsonArray jsonArray = el.getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String msgId = jsonObject.get("messageId").getAsString();

                sbMsgIds.append(msgId).append(",");

                //避免插入重复数据
                Message2Model dbMessage = SQLite.select().from(Message2Model.class).where(Message2Model_Table.messageId.eq(msgId)).querySingle();
                if (dbMessage != null) {
                    continue;
                }

                int msgType = jsonObject.get("messageType").getAsInt();
                int msgContentType = jsonObject.get("messageContentType").getAsInt();
                String fromId = jsonObject.get("fromId").getAsString();
                String toId = jsonObject.get("toId").getAsString();
                long timestamp = jsonObject.get("sendTime").getAsLong();
                int statusReport = jsonObject.get("statusReport").getAsInt();
                String content = jsonObject.get("content").getAsString();
                Message2Model message2Model = new Message2Model();
                message2Model.setMessageId(msgId);
                message2Model.setFromId(fromId);
                message2Model.setMessageType(msgType);
                message2Model.setMsgContentType(msgContentType);
                message2Model.setToId(toId);
                message2Model.setSendTime(timestamp);
                message2Model.setStatusReport(statusReport);
                message2Model.setExtend("");
                message2Model.setContent(content);
                message2Model.save();
            }

            if (sbMsgIds.length() > 1) {
                //离线消息成功接收,通知服务器改变消息状态
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("received_messageId_list", sbMsgIds.substring(0, sbMsgIds.length() - 1));

                TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
                TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
                headBuilder.setMsgType(MessageType.REPORT_RECEIVED_OUTLINE_MESSAGE_LIST.getMsgType());
                headBuilder.setStatusReport(0);
                headBuilder.setMsgContentType(0);
                headBuilder.setMsgId(UUID.randomUUID().toString());
                headBuilder.setFromId(message.getHead().getToId());
                headBuilder.setToId("");
                headBuilder.setTimestamp(System.currentTimeMillis());
                headBuilder.setExtend("");
                builder.setBody(jsonObject.toString());
                builder.setHeader(headBuilder);
                TransMessageProtobuf.TransMessage transMessage = builder.build();
                ImsManager.getInstance().sendMessage(transMessage, false);
            }
        }
        CEventCenter.dispatchEvent(Events.LIST_OUTLINE_MESSAGE, 0, 0, null);
    }

}

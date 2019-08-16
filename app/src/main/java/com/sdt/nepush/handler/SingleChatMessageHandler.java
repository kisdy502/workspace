package com.sdt.nepush.handler;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.db.Message2Model;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.msg.SingleMessage;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;


/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       SingleChatMessageHandler.java</p>
 * <p>@PackageName:     com.freddy.chat.im.handler</p>
 * <b>
 * <p>@Description:     类描述</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 03:43</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class SingleChatMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private static final String TAG = SingleChatMessageHandler.class.getSimpleName();

    @Override
    protected void action(AppMessage message) {

        logger.d(TAG, "收到单聊消息，message=" + message);

        SingleMessage msg = new SingleMessage();
        msg.setMsgId(message.getHead().getMsgId());
        msg.setMsgType(message.getHead().getMsgType());
        msg.setMsgContentType(message.getHead().getMsgContentType());
        msg.setFromId(message.getHead().getFromId());
        msg.setToId(message.getHead().getToId());
        msg.setTimestamp(message.getHead().getTimestamp());
        msg.setExtend(message.getHead().getExtend());
        msg.setContent(message.getBody());

        Message2Model message2Model = saveMessage2Db(msg);

        CEventCenter.dispatchEvent(Events.CHAT_SINGLE_MESSAGE, 0, 0, message2Model);
    }

    private Message2Model saveMessage2Db(SingleMessage message) {
        Message2Model message2Model = new Message2Model();
        message2Model.setMsgId(message.getMsgId());
        message2Model.setMsgType(message.getMsgType());
        message2Model.setMsgContentType(message.getMsgContentType());
        message2Model.setFromId(message.getFromId());
        message2Model.setToId(message.getToId());
        message2Model.setTimestamp(message.getTimestamp());
        message2Model.setContent(message.getContent());
        message2Model.setStatusReport(message.getStatusReport());
        message2Model.setExtend(message.getExtend());
        message2Model.save();
        return message2Model;
    }
}

package com.sdt.nepush.handler;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.db.Message2Model;
import com.sdt.nepush.db.Message2Model_Table;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.msg.AppMessage;


/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       ServerReportMessageHandler.java</p>
 * <p>@PackageName:     com.freddy.chat.im.handler</p>
 * <b>
 * <p>@Description:     服务端返回的消息发送状态报告</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/22 19:16</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class ServerReportMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void action(AppMessage message) {
        logger.d("收到消息状态报告，message=" + message);
        if (message == null || message.getHead() == null) {
            return;
        }

        if (message.getHead().getStatusReport() == 0) {
            Message2Model message2Model = SQLite.select().from(Message2Model.class).where(
                    Message2Model_Table.messageId.eq(message.getHead().getMessageId())).querySingle();
            if (message2Model != null) {
                message2Model.setStatusReport(-1);
                message2Model.update();
                CEventCenter.dispatchEvent(Events.REPORT_CHAT_MESSAGE_FAILED_STATUS, 0, 0, message2Model);
            }
        } else if (message.getHead().getStatusReport() == 1) {
            Message2Model message2Model = SQLite.select().from(Message2Model.class).where(
                    Message2Model_Table.messageId.eq(message.getHead().getMessageId())).querySingle();
            if (message2Model != null) {
                message2Model.setStatusReport(1);
                message2Model.update();
                CEventCenter.dispatchEvent(Events.REPORT_CHAT_MESSAGE_STATUS, 0, 0, message2Model);
            }
        }


    }
}

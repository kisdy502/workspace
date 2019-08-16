package com.sdt.nepush.handler;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.msg.AppMessage;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       GroupChatMessageHandler.java</p>
 * <p>@PackageName:     com.freddy.chat.im.handler</p>
 * <b>
 * <p>@Description:     类描述</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 03:43</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class GroupChatMessageHandler extends AbstractMessageHandler {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private static final String TAG = GroupChatMessageHandler.class.getSimpleName();

    @Override
    protected void action(AppMessage message) {
        logger.d(TAG, "收到群聊消息，message=" + message);
    }
}

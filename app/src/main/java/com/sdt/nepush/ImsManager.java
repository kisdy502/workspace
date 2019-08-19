package com.sdt.nepush;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.IMSClientFactory;
import com.sdt.libchat.IMSConnectStatusCallback;
import com.sdt.libchat.core.ImsClient;
import com.sdt.libchat.OnEventListener;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.processor.MessageBuilder;
import com.sdt.nepush.processor.MessageProcessor;
import com.sdt.nepush.util.CThreadPoolExecutor;

import java.util.UUID;
import java.util.Vector;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class ImsManager {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private ImsClient imsClient;
    private boolean inited;


    public final static ImsManager getInstance() {
        return Holder.instance;
    }

    private final static class Holder {
        private final static ImsManager instance = new ImsManager();
    }

    public boolean isInited() {
        return inited;
    }

    public synchronized void init(final String userId, final String token, String hostJson, int appStatus) {
        if (inited) {
            logger.i("is inited return!!!");
            return;
        }
        logger.d("first inited");
        Vector<String> serverUrlList = convertHosts(hostJson);
        if (serverUrlList == null || serverUrlList.size() == 0) {
            logger.e("init ImsManager error,ims hosts is null");
            return;
        }
        inited = true;
        logger.d("init ImsManager, servers=" + hostJson);
        if (null != imsClient) {
            imsClient.close();
        }
        imsClient = IMSClientFactory.getIMSClient();
        updateAppStatus(appStatus);
        imsClient.init(serverUrlList, new IMSConnectStatusCallback() {
                    @Override
                    public void onConnecting() {
                        logger.d("回传应用层 onConnecting,");
                        CThreadPoolExecutor.runOnMainThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(App.getInstance(), "正在连接...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onConnected() {
                        logger.e("回传应用层 onConnected,");
                        CThreadPoolExecutor.runOnMainThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(App.getInstance(), "连接成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onConnectFailed() {
                        logger.e("回传应用层 onConnectFailed,");
                        CThreadPoolExecutor.runOnMainThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(App.getInstance(), "连接失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },
                new OnEventListener() {

                    @Override
                    public void dispatchMsg(TransMessageProtobuf.TransMessage msg) {
                        MessageProcessor.getInstance().receiveMsg(MessageBuilder.getMessageByProtobuf(msg));
                    }

                    @Override
                    public boolean isNetworkAvailable() {
                        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo info = cm.getActiveNetworkInfo();
                        return info != null && info.isConnected();
                    }

                    @Override
                    public int getReconnectInterval() {
                        return 0;
                    }

                    @Override
                    public int getConnectTimeout() {
                        return 0;
                    }

                    @Override
                    public int getForegroundHeartbeatInterval() {
                        return 0;
                    }

                    @Override
                    public int getBackgroundHeartbeatInterval() {
                        return 0;
                    }

                    @Override
                    public TransMessageProtobuf.TransMessage getHandshakeMsg() {
                        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
                        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
                        headBuilder.setMsgId(UUID.randomUUID().toString());
                        headBuilder.setMsgType(MessageType.HANDSHAKE.getMsgType());
                        headBuilder.setFromId(userId);
                        headBuilder.setTimestamp(System.currentTimeMillis());

                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("token", token);
                        headBuilder.setExtend(jsonObj.toString());
                        builder.setHeader(headBuilder.build());
                        return builder.build();
                    }

                    @Override
                    public TransMessageProtobuf.TransMessage getHeartbeatMsg() {
                        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
                        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
                        headBuilder.setMsgId(UUID.randomUUID().toString());
                        headBuilder.setMsgType(MessageType.HEARTBEAT.getMsgType());
                        headBuilder.setFromId(userId);
                        headBuilder.setTimestamp(System.currentTimeMillis());
                        builder.setHeader(headBuilder.build());

                        return builder.build();
                    }

                    @Override
                    public TransMessageProtobuf.TransMessage getFriendListMsg() {
                        TransMessageProtobuf.TransMessage.Builder builder = TransMessageProtobuf.TransMessage.newBuilder();
                        TransMessageProtobuf.MessageHeader.Builder headBuilder = TransMessageProtobuf.MessageHeader.newBuilder();
                        headBuilder.setMsgId(UUID.randomUUID().toString());
                        headBuilder.setMsgType(MessageType.GET_USER_FRIEND_LIST.getMsgType());
                        headBuilder.setFromId(userId);
                        headBuilder.setTimestamp(System.currentTimeMillis());
                        builder.setHeader(headBuilder.build());

                        return builder.build();
                    }

                    @Override
                    public int getSystemPushMsgType() {
                        return MessageType.SYSTEMMESSAGE.getMsgType();
                    }

                    @Override
                    public int getHeartbeatMsgType() {
                        return MessageType.HEARTBEAT.getMsgType();
                    }

                    @Override
                    public int getHandshakeMsgType() {
                        return MessageType.HANDSHAKE.getMsgType();
                    }

                    @Override
                    public int getForceLogoutMsgType() {
                        return MessageType.FORCE_CLIENT_LOGOUT.getMsgType();
                    }

                    @Override
                    public int getOutLineMsgListType() {
                        return MessageType.GET_OUTLINE_MESSAGE_LIST.getMsgType();
                    }

                    @Override
                    public int getServerSentReportMsgType() {
                        return MessageType.SERVER_MSG_SENT_STATUS_REPORT.getMsgType();
                    }

                    @Override
                    public int getClientReceivedReportMsgType() {
                        return MessageType.CLIENT_MSG_RECEIVED_STATUS_REPORT.getMsgType();
                    }

                    @Override
                    public int getResendCount() {
                        return 0;
                    }

                    @Override
                    public int getResendInterval() {
                        return 0;
                    }
                });

    }

    public void sendMessage(TransMessageProtobuf.TransMessage transMessage) {
        if (inited) {
            imsClient.sendMsg(transMessage);
        }
    }

    public void sendMessage(TransMessageProtobuf.TransMessage transMessage, boolean resend) {
        if (inited) {
            imsClient.sendMsg(transMessage, resend);
        }
    }


    public void updateAppStatus(int appStatus) {
        if (imsClient == null) {
            return;
        }
        imsClient.setAppStatus(appStatus);
    }

    /**
     * host  json list
     */
    private Vector<String> convertHosts(String hosts) {
        if (hosts != null && hosts.length() > 0) {
            JSONArray hostArray = JSONArray.parseArray(hosts);
            if (null != hostArray && hostArray.size() > 0) {
                Vector<String> serverUrlList = new Vector<String>();
                JSONObject host;
                for (int i = 0; i < hostArray.size(); i++) {
                    host = JSON.parseObject(hostArray.get(i).toString());
                    serverUrlList.add(host.getString("host") + " " + host.getInteger("port"));
                }
                return serverUrlList;
            }
        }
        return null;
    }


    public void close() {
        if (inited)
            imsClient.close();
        inited = false;
    }
}

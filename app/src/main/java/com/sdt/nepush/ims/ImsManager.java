package com.sdt.nepush.ims;

import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libchat.IMSClientFactory;
import com.sdt.libchat.OnConnectStatusCallback;
import com.sdt.libchat.core.ImsClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.util.CThreadPoolExecutor;

import java.util.Vector;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class ImsManager {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private ImsClient imsClient;
    private boolean inited;
    private boolean connected = false;


    public final static ImsManager getInstance() {
        return Holder.instance;
    }

    private final static class Holder {
        private final static ImsManager instance = new ImsManager();
    }

    public boolean isInited() {
        return inited;
    }

    public synchronized void init(Long userId, final String token, String hostJson, int appStatus) {
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
        imsClient.init(serverUrlList, new ImsOnConnectStatusCallback(), new ImsOnEventListener(userId, token));
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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}

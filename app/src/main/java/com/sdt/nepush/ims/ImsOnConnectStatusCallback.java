package com.sdt.nepush.ims;

import android.widget.Toast;

import com.sdt.libchat.OnConnectStatusCallback;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.util.CThreadPoolExecutor;

/**
 * <p>@author:          ${Author}</p>
 * <p>@date:            ${Date}</p>
 * <p>@email:           ${Email}</p>
 * <b>
 * <p>@Description:     ${Description}</p>
 * </b>
 */
public class ImsOnConnectStatusCallback implements OnConnectStatusCallback {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    public void onConnecting() {
        logger.d("回传应用层 onConnecting,");
        ImsManager.getInstance().setConnected(false);
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
        ImsManager.getInstance().setConnected(true);
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
        ImsManager.getInstance().setConnected(false);
        CThreadPoolExecutor.runOnMainThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(App.getInstance(), "连接失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

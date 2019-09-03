package com.sdt.nepush;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.notification.NotificationHelper;


/**
 * Created by sdt13411 on 2019/7/17.
 */

public class App extends Application {
    ILogger logger = ILoggerFactory.getLogger(getClass());
    private static App instance;

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        logger.i("----------------");
        logger.i("-");
        logger.i("-App onCreate");
        logger.i("-");
        logger.i("----------------");
        FlowManager.init(this);      //初始化DBFLOW
        NotificationHelper.initNotificationChannel(instance);

    }


    public static App getInstance() {
        return instance;
    }
}

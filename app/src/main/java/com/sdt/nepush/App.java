package com.sdt.nepush;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.raizlabs.android.dbflow.config.FlowManager;


/**
 * Created by sdt13411 on 2019/7/17.
 */

public class App extends Application {
    private static App instance;

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FlowManager.init(this);      //初始化DBFLOW
    }


    public static App getInstance() {
        return instance;
    }
}

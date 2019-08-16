package com.sdt.libcommon.esc;

import android.util.Log;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class AndroidLogger implements ILogger {

    String myTag;

    public AndroidLogger(Class cls) {
        this.myTag = cls.getName();
    }

    @Override
    public void v(String msg) {
        Log.v(myTag, msg);
    }

    @Override
    public void d(String msg) {
        Log.d(myTag, msg);
    }

    @Override
    public void i(String msg) {
        Log.i(myTag, msg);
    }

    @Override
    public void w(String msg) {
        Log.w(myTag, msg);
    }

    @Override
    public void e(String msg) {
        Log.e(myTag, msg);
    }

    @Override
    public void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    @Override
    public void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        Log.e(tag, msg);
    }
}

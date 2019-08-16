package com.sdt.libcommon.esc;

import java.io.File;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class ECSLogger implements ILogger {


    public final static int LEVEL_V = 1;
    public final static int LEVEL_D = 2;
    public final static int LEVEL_I = 3;
    public final static int LEVEL_W = 4;
    public final static int LEVEL_E = 5;

    private String myTag;
    private File storeDir;
    ECSWriteTask ecsWriteTask;

    public ECSLogger(Class cls, File logDir) {
        myTag = cls.getName();
        storeDir = logDir;
        ecsWriteTask = new ECSWriteTask(storeDir.getAbsolutePath());
    }

    @Override
    public void v(String msg) {
        ecsWriteTask.addTask(LEVEL_V, myTag, msg);
    }

    @Override
    public void d(String msg) {
        ecsWriteTask.addTask(LEVEL_D, myTag, msg);
    }

    @Override
    public void i(String msg) {
        ecsWriteTask.addTask(LEVEL_I, myTag, msg);
    }

    @Override
    public void w(String msg) {
        ecsWriteTask.addTask(LEVEL_W, myTag, msg);
    }

    @Override
    public void e(String msg) {
        ecsWriteTask.addTask(LEVEL_E, myTag, msg);
    }

    @Override
    public void v(String tag, String msg) {
        ecsWriteTask.addTask(LEVEL_V, tag, msg);
    }

    @Override
    public void d(String tag, String msg) {
        ecsWriteTask.addTask(LEVEL_D, tag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        ecsWriteTask.addTask(LEVEL_I, tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        ecsWriteTask.addTask(LEVEL_W, tag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        ecsWriteTask.addTask(LEVEL_E, tag, msg);
    }
}

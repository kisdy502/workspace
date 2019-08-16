package com.sdt.libcommon.esc;

import java.io.File;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public class ILoggerFactory {

    public final static String LogAndroid = "Android";
    public final static String LogFile = "File";

    private static String logType = LogAndroid;
    private static File logDir;

    public static void init(String logtype) {
        logType = logType;
    }

    public void init(String logtype, File logdir) {
        logType = logtype;
        logDir = logdir;
    }

    public static ILogger getLogger(Class cls) {
        if (LogAndroid.equalsIgnoreCase(logType)) {
            return new AndroidLogger(cls);
        } else if (LogFile.equalsIgnoreCase(logType)) {
            return new ECSLogger(cls, logDir);
        }
        return new AndroidLogger(cls);
    }


}

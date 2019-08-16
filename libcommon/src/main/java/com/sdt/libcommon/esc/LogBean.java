package com.sdt.libcommon.esc;

import android.content.SharedPreferences;

import static com.sdt.libcommon.esc.ECSLogger.LEVEL_D;
import static com.sdt.libcommon.esc.ECSLogger.LEVEL_E;
import static com.sdt.libcommon.esc.ECSLogger.LEVEL_I;
import static com.sdt.libcommon.esc.ECSLogger.LEVEL_V;
import static com.sdt.libcommon.esc.ECSLogger.LEVEL_W;


/**
 * Created by chenran3 on 2018/1/3.
 */

public class LogBean {
    public static String SESSION = "";
    public static int BEANCOUNT;
    public static SharedPreferences SP = null;
    public int seq;
    public float time;
    public String timeStr = "";
    public String msg = "";
    public String className = "";
    public int line;
    public int level = 0;
    public String func = "";
    public String tag = "";

    public LogBean() {
    }


    public String toString() {
        StringBuffer sb = new StringBuffer(this.timeStr);
        sb.append(' ').append(this.getLevel(this.level))
                .append('[').append(this.tag).append(']')
                .append('[').append(this.className).append('.').append(func).append("|").append(line).append(']')
                .append(' ').append(this.msg).append("\n");
        return sb.toString();
    }

    private String getLevel(int level) {
        String ret = null;
        switch (level) {
            case LEVEL_V:
                ret = "V";
                break;
            case LEVEL_D:
                ret = "D";
                break;
            case LEVEL_I:
                ret = "I";
                break;
            case LEVEL_W:
                ret = "W";
                break;
            case LEVEL_E:
                ret = "E";
                break;
            default:
                ret = "D";
        }

        return ret;
    }
}

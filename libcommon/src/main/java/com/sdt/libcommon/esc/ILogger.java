package com.sdt.libcommon.esc;

/**
 * Created by sdt13411 on 2019/7/17.
 */

public interface ILogger {

    void v(String msg);

    void d(String msg);

    void i(String msg);

    void w(String msg);

    void e(String msg);

    void v(String tag, String msg);

    void d(String tag, String msg);

    void i(String tag, String msg);

    void w(String tag, String msg);

    void e(String tag, String msg);
}

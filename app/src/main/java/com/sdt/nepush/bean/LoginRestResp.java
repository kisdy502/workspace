package com.sdt.nepush.bean;

import com.google.gson.annotations.SerializedName;

public class LoginRestResp extends RestResp {

    @SerializedName("data")
    protected UserBean userBean;

    protected LoginRestResp(int status, String message) {
        super(status, message);
    }

    public LoginRestResp(int status, String message, UserBean loginUser) {
        super(status, message);
        this.userBean = loginUser;
    }

    public UserBean getLoginUser() {
        return userBean;

    }
}


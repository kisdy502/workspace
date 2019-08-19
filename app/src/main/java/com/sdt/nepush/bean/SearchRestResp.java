package com.sdt.nepush.bean;

import com.google.gson.annotations.SerializedName;

public class SearchRestResp extends RestResp {

    @SerializedName("data")
    protected UserBean userBean;

    protected SearchRestResp(int status, String message) {
        super(status, message);
    }

    public SearchRestResp(int status, String message, UserBean userBean) {
        super(status, message);
        this.userBean = userBean;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}


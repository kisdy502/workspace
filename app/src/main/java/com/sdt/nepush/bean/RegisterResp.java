package com.sdt.nepush.bean;

public class RegisterResp extends RestResp {

    private UserBean data;

    protected RegisterResp(int status, String message) {
        super(status, message);
    }

    public UserBean getData() {
        return data;
    }

    public void setData(UserBean data) {
        this.data = data;
    }
}


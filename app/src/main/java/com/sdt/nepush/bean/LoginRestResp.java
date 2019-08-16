package com.sdt.nepush.bean;

public class LoginRestResp extends RestResp {

    protected String data;

    protected LoginRestResp(int status, String message) {
        super(status, message);
    }

    public LoginRestResp(int status, String message, String data) {
        super(status, message);
        this.data = data;
    }

    public String getData() {
        return data;
    }
}


package com.sdt.nepush.bean;

import com.google.gson.Gson;

public class RestResp {

    protected int status;
    protected String message;

    public boolean isSuccess() {
        return status == 1;
    }

    protected RestResp(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}


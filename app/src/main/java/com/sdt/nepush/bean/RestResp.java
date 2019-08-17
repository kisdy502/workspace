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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}


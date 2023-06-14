package com.mdnhs.graduateassist.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MyRatingRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("user_rate")
    private String user_rate;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getUser_rate() {
        return user_rate;
    }
}

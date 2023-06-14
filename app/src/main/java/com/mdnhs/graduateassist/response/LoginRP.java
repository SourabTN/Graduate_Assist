package com.mdnhs.graduateassist.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("name")
    private String name;

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

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

}

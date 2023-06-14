package com.mdnhs.graduateassist.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("msg")
    private String msg;

    @SerializedName("success")
    private String success;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("photo")
    private String photo;

    @SerializedName("photo_resize")
    private String photo_resize;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getMsg() {
        return msg;
    }

    public String getSuccess() {
        return success;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPhoto_resize() {
        return photo_resize;
    }
}

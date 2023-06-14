package com.mdnhs.graduateassist.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentList implements Serializable {

    @SerializedName("comment_id")
    private String comment_id;

    @SerializedName("movie_id")
    private String movie_id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("user_profile")
    private String user_profile;

    @SerializedName("user_profile_resize")
    private String user_profile_resize;

    @SerializedName("comment_text")
    private String comment_text;

    @SerializedName("comment_date")
    private String comment_date;

    public CommentList(String comment_id, String movie_id, String user_id, String user_name, String user_profile, String user_profile_resize, String comment_text, String comment_date) {
        this.comment_id = comment_id;
        this.movie_id = movie_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_profile = user_profile;
        this.user_profile_resize = user_profile_resize;
        this.comment_text = comment_text;
        this.comment_date = comment_date;
    }

    public String getComment_id() {
        return comment_id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public String getUser_profile_resize() {
        return user_profile_resize;
    }

    public String getComment_text() {
        return comment_text;
    }

    public String getComment_date() {
        return comment_date;
    }
}

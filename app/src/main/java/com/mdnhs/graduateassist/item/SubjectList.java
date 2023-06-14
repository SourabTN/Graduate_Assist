package com.mdnhs.graduateassist.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubjectList implements Serializable {

    @SerializedName("gid")
    private String gid;

    @SerializedName("genre_name")
    private String genre_name;

    @SerializedName("genre_image")
    private String genre_image;

    @SerializedName("genre_image_thumb")
    private String genre_image_thumb;

    public String getGid() {
        return gid;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public String getGenre_image() {
        return genre_image;
    }

    public String getGenre_image_thumb() {
        return genre_image_thumb;
    }
}

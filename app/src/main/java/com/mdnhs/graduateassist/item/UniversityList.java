package com.mdnhs.graduateassist.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UniversityList implements Serializable {

    @SerializedName("language_id")
    private String language_id;

    @SerializedName("language_name")
    private String language_name;

    @SerializedName("language_bg")
    private String language_bg;

    public String getLanguage_id() {
        return language_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public String getLanguage_bg() {
        return language_bg;
    }
}

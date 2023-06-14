package com.mdnhs.graduateassist.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SliderList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type ;

    @SerializedName("title")
    private String title ;

    @SerializedName("language_bg")
    private String language_bg;

    @SerializedName("language_name")
    private String language_name;

    @SerializedName("total_rate")
    private String total_rate;

    @SerializedName("rate_avg")
    private String rate_avg ;

    @SerializedName("image")
    private String image;

    @SerializedName("external_link")
    private String external_link;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage_bg() {
        return language_bg;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    public String getRate_avg() {
        return rate_avg;
    }

    public String getImage() {
        return image;
    }

    public String getExternal_link() {
        return external_link;
    }
}

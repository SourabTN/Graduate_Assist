package com.mdnhs.graduateassist.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("language_name")
    private String language_name;

    @SerializedName("language_bg")
    private String language_bg;

    @SerializedName("movie_title")
    private String movie_title;

    @SerializedName("movie_thumbnail_s")
    private String movie_thumbnail_s;

    @SerializedName("rate_avg")
    private String rate_avg;

    @SerializedName("total_rate")
    private String total_rate;

    public String getId() {
        return id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public String getLanguage_bg() {
        return language_bg;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public String getMovie_thumbnail_s() {
        return movie_thumbnail_s;
    }

    public String getRate_avg() {
        return rate_avg;
    }

    public String getTotal_rate() {
        return total_rate;
    }
}

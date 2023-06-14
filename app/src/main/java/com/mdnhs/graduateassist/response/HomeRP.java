package com.mdnhs.graduateassist.response;

import com.mdnhs.graduateassist.item.HomeDataList;
import com.mdnhs.graduateassist.item.UniversityList;
import com.mdnhs.graduateassist.item.DataList;
import com.mdnhs.graduateassist.item.SliderList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("movies_slider")
    private List<SliderList> sliderLists;

    @SerializedName("recent_movies")
    private List<DataList> recentViewLists;

    @SerializedName("language")
    private List<UniversityList> universityLists;

    @SerializedName("latest_reviews")
    private List<DataList> latestLists;

    @SerializedName("language_wise_movies")
    private List<HomeDataList> homeDataLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<SliderList> getSliderLists() {
        return sliderLists;
    }

    public List<DataList> getRecentViewLists() {
        return recentViewLists;
    }

    public List<UniversityList> getLanguageLists() {
        return universityLists;
    }

    public List<DataList> getLatestLists() {
        return latestLists;
    }

    public List<HomeDataList> getHomeMovieLists() {
        return homeDataLists;
    }
}

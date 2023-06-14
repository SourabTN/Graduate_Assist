package com.mdnhs.graduateassist.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeDataList implements Serializable {

    @SerializedName("language_id")
    private String language_id;

    @SerializedName("language_name")
    private String language_name;

    @SerializedName("language_movies")
    private List<DataList> dataLists;

    public String getLanguage_id() {
        return language_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public List<DataList> getMovieLists() {
        return dataLists;
    }
}

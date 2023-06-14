package com.mdnhs.graduateassist.response;

import com.mdnhs.graduateassist.item.UniversityList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UniversityRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("MOVIE_REVIEWS_APP")
    private List<UniversityList> universityLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<UniversityList> getLanguageLists() {
        return universityLists;
    }
}

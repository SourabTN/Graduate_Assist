package com.mdnhs.graduateassist.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GalleryList implements Serializable {

    @SerializedName("image_name")
    private String image_name;

    public String getImage_name() {
        return image_name;
    }
}

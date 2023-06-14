package com.mdnhs.graduateassist.response;

import com.mdnhs.graduateassist.item.CommentList;
import com.mdnhs.graduateassist.item.GalleryList;
import com.mdnhs.graduateassist.item.DataList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DataDetailRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("id")
    private String id;

    @SerializedName("genre_id")
    private String genre_id;

    @SerializedName("movie_title")
    private String movie_title;

    @SerializedName("movie_casts")
    private String movie_casts;

    @SerializedName("is_trailer")
    private String is_trailer;

    @SerializedName("video_url")
    private String video_url;

    @SerializedName("video_id")
    private String video_id;

    @SerializedName("movie_cover_b")
    private String movie_cover_b;

    @SerializedName("movie_cover_s")
    private String movie_cover_s;

    @SerializedName("movie_thumbnail_b")
    private String movie_thumbnail_b;

    @SerializedName("movie_thumbnail_s")
    private String movie_thumbnail_s;

    @SerializedName("movie_desc")
    private String movie_desc;

    @SerializedName("movie_date")
    private String movie_date;

    @SerializedName("total_views")
    private String total_views;

    @SerializedName("admin_rating")
    private String admin_rating;

    @SerializedName("total_rate")
    private String total_rate;

    @SerializedName("rate_avg")
    private String rate_avg;

    @SerializedName("language_id")
    private String language_id;

    @SerializedName("language_name")
    private String language_name;

    @SerializedName("language_bg")
    private String language_bg;

    @SerializedName("share_link")
    private String share_link;

    @SerializedName("is_fav")
    private String is_fav;

    @SerializedName("total_comment")
    private String total_comment;

    @SerializedName("gallery")
    private List<GalleryList> galleryLists;

    @SerializedName("related")
    private List<DataList> relatedDataLists;

    @SerializedName("comments")
    private List<CommentList> commentLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getId() {
        return id;
    }

    public String getGenre_id() {
        return genre_id;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public String getMovie_casts() {
        return movie_casts;
    }

    public String getIs_trailer() {
        return is_trailer;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getVideo_id() {
        return video_id;
    }

    public String getMovie_cover_b() {
        return movie_cover_b;
    }

    public String getMovie_cover_s() {
        return movie_cover_s;
    }

    public String getMovie_thumbnail_b() {
        return movie_thumbnail_b;
    }

    public String getMovie_thumbnail_s() {
        return movie_thumbnail_s;
    }

    public String getMovie_desc() {
        return movie_desc;
    }

    public String getMovie_date() {
        return movie_date;
    }

    public String getTotal_views() {
        return total_views;
    }

    public String getAdmin_rating() {
        return admin_rating;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    public String getRate_avg() {
        return rate_avg;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public String getLanguage_bg() {
        return language_bg;
    }

    public String getShare_link() {
        return share_link;
    }

    public String getIs_fav() {
        return is_fav;
    }

    public String getTotal_comment() {
        return total_comment;
    }

    public List<GalleryList> getGalleryLists() {
        return galleryLists;
    }

    public List<DataList> getRelatedMovieLists() {
        return relatedDataLists;
    }

    public List<CommentList> getCommentLists() {
        return commentLists;
    }
}

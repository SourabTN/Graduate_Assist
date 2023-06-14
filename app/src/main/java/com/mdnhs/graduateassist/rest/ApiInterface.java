package com.mdnhs.graduateassist.rest;

import com.mdnhs.graduateassist.response.AboutUsRP;
import com.mdnhs.graduateassist.response.AppRP;
import com.mdnhs.graduateassist.response.CommentRP;
import com.mdnhs.graduateassist.response.ContactRP;
import com.mdnhs.graduateassist.response.DataRP;
import com.mdnhs.graduateassist.response.DeleteCommentRP;
import com.mdnhs.graduateassist.response.FaqRP;
import com.mdnhs.graduateassist.response.FavouriteRP;
import com.mdnhs.graduateassist.response.SubjectRP;
import com.mdnhs.graduateassist.response.HomeRP;
import com.mdnhs.graduateassist.response.UniversityRP;
import com.mdnhs.graduateassist.response.LoginRP;
import com.mdnhs.graduateassist.response.DataDetailRP;
import com.mdnhs.graduateassist.response.DataListRP;
import com.mdnhs.graduateassist.response.MyRatingRP;
import com.mdnhs.graduateassist.response.PrivacyPolicyRP;
import com.mdnhs.graduateassist.response.ProfileRP;
import com.mdnhs.graduateassist.response.RatingRP;
import com.mdnhs.graduateassist.response.RegisterRP;
import com.mdnhs.graduateassist.response.TermsConditionsRP;
import com.mdnhs.graduateassist.response.UserCommentRP;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    //get app data
    @POST("api.php")
    @FormUrlEncoded
    Call<AppRP> getAppData(@Field("data") String data);

    //get about us
    @POST("api.php")
    @FormUrlEncoded
    Call<AboutUsRP> getAboutUs(@Field("data") String data);

    //get privacy policy
    @POST("api.php")
    @FormUrlEncoded
    Call<PrivacyPolicyRP> getPrivacyPolicy(@Field("data") String data);

    //get terms condition
    @POST("api.php")
    @FormUrlEncoded
    Call<TermsConditionsRP> getTermsCondition(@Field("data") String data);

    //get faq
    @POST("api.php")
    @FormUrlEncoded
    Call<FaqRP> getFaq(@Field("data") String data);

    //get contact subject
    @POST("api.php")
    @FormUrlEncoded
    Call<ContactRP> getContactSub(@Field("data") String data);

    //submit contact
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitContact(@Field("data") String data);

    //login
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLogin(@Field("data") String data);

    //register
    @POST("api.php")
    @FormUrlEncoded
    Call<RegisterRP> getRegister(@Field("data") String data);

    //forgot password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> getForgotPass(@Field("data") String data);

    //login check
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLoginDetail(@Field("data") String data);

    //get profile detail
    @POST("api.php")
    @FormUrlEncoded
    Call<ProfileRP> getProfile(@Field("data") String data);

    //edit profile
    @POST("api.php")
    @Multipart
    Call<DataRP> editProfile(@Part("data") RequestBody data, @Part MultipartBody.Part part);

    //update password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> updatePassword(@Field("data") String data);

    //home
    @POST("api.php")
    @FormUrlEncoded
    Call<HomeRP> getHome(@Field("data") String data);

    //Language list
    @POST("api.php")
    @FormUrlEncoded
    Call<UniversityRP> getLanguage(@Field("data") String data);

    //genre list
    @POST("api.php")
    @FormUrlEncoded
    Call<SubjectRP> getGenre(@Field("data") String data);

    //movie list
    @POST("api.php")
    @FormUrlEncoded
    Call<DataListRP> getMovieList(@Field("data") String data);

    //movie detail
    @POST("api.php")
    @FormUrlEncoded
    Call<DataDetailRP> getMovieDetail(@Field("data") String data);

    //favourite
    @POST("api.php")
    @FormUrlEncoded
    Call<FavouriteRP> isFavouriteOrNot(@Field("data") String data);

    //get my rating
    @POST("api.php")
    @FormUrlEncoded
    Call<MyRatingRP> getMyRating(@Field("data") String data);

    //rating movie
    @POST("api.php")
    @FormUrlEncoded
    Call<RatingRP> submitRating(@Field("data") String data);

    //get all comment
    @POST("api.php")
    @FormUrlEncoded
    Call<CommentRP> getAllComment(@Field("data") String data);

    //comment
    @POST("api.php")
    @FormUrlEncoded
    Call<UserCommentRP> submitComment(@Field("data") String data);

    //delete comment
    @POST("api.php")
    @FormUrlEncoded
    Call<DeleteCommentRP> deleteComment(@Field("data") String data);

}

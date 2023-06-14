package com.mdnhs.graduateassist.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.AllComment;
import com.mdnhs.graduateassist.activity.Gallery;
import com.mdnhs.graduateassist.activity.Login;
import com.mdnhs.graduateassist.activity.MainActivity;
import com.mdnhs.graduateassist.activity.ViewImage;
import com.mdnhs.graduateassist.activity.YoutubePlayActivity;
import com.mdnhs.graduateassist.adapter.CastAdapter;
import com.mdnhs.graduateassist.adapter.CommentAdapter;
import com.mdnhs.graduateassist.adapter.GalleryAdapter;
import com.mdnhs.graduateassist.adapter.HomeDataAdapter;
import com.mdnhs.graduateassist.interfaces.FavouriteIF;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.CommentList;
import com.mdnhs.graduateassist.response.FavouriteRP;
import com.mdnhs.graduateassist.response.DataDetailRP;
import com.mdnhs.graduateassist.response.MyRatingRP;
import com.mdnhs.graduateassist.response.RatingRP;
import com.mdnhs.graduateassist.response.UserCommentRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.Constant;
import com.mdnhs.graduateassist.util.Events;
import com.mdnhs.graduateassist.util.GlobalBus;
import com.mdnhs.graduateassist.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataDetailFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private Animation myAnim;
    private String id, type, title;
    private int position, rate;
    private Menu menu;
    private boolean isMenu = false;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private DataDetailRP dataDetailRP;
    private WebView webView;
    private InputMethodManager imm;
    private CircleImageView imageViewUser;
    private EditText editTextComment;
    private CommentAdapter commentAdapter;
    private List<CommentList> commentLists;
    private RatingView ratingViewAdmin, ratingViewUser;
    private ConstraintLayout conNoData, conMain, conRelated, conGallery, conComment;
    private ImageView imageView, imageViewPlay, imageViewSend, imageViewUserRating;
    private RecyclerView recyclerViewCast, recyclerViewGallery, recyclerViewRelated, recyclerViewComment;
    private MaterialTextView textViewName, textViewLag, textViewDate, textViewView, textViewRating, textViewUserRating, textViewRelated, textViewComment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.moviedetail_fragment, container, false);

        GlobalBus.getBus().register(this);

        commentLists = new ArrayList<>();

        assert getArguments() != null;
        id = getArguments().getString("id");
        type = getArguments().getString("type");
        title = getArguments().getString("title");
        position = getArguments().getInt("position");

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(title);
        }

        progressDialog = new ProgressDialog(getActivity());

        onClick = (position, type, id, title) -> {

            if (type.equals("gallery")) {
                Constant.galleryLists.clear();
                Constant.galleryLists.addAll(dataDetailRP.getGalleryLists());
                startActivity(new Intent(getActivity(), Gallery.class)
                        .putExtra("position", position));
            } else {
                getActivity().getSupportFragmentManager().popBackStack();
                DataDetailFragment dataDetailFragment = new DataDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("type", type);
                bundle.putString("title", title);
                bundle.putInt("position", position);
                dataDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                        dataDetailFragment, title).addToBackStack(title).commitAllowingStateLoss();
            }

        };
        method = new Method(getActivity(), onClick);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressBar = view.findViewById(R.id.progressbar_md);
        conNoData = view.findViewById(R.id.con_noDataFound);
        conMain = view.findViewById(R.id.con_md);
        imageView = view.findViewById(R.id.imageView_md);
        imageViewPlay = view.findViewById(R.id.imageView_play_md);
        imageViewUserRating = view.findViewById(R.id.imageView_userRating_md);
        textViewName = view.findViewById(R.id.textView_name_md);
        textViewDate = view.findViewById(R.id.textView_date_md);
        textViewView = view.findViewById(R.id.textView_view_md);
        textViewLag = view.findViewById(R.id.textView_lag_md);
        textViewRating = view.findViewById(R.id.textView_adminRating_md);
        textViewUserRating = view.findViewById(R.id.textView_user_rating_md);
        ratingViewAdmin = view.findViewById(R.id.ratingBar_md);
        ratingViewUser = view.findViewById(R.id.ratingBar_user_md);
        webView = view.findViewById(R.id.webView_md);
        textViewRelated = view.findViewById(R.id.textView_viewAll_related_md);
        recyclerViewCast = view.findViewById(R.id.recyclerView_cast_md);
        recyclerViewGallery = view.findViewById(R.id.recyclerView_gallery_md);
        recyclerViewRelated = view.findViewById(R.id.recyclerView_related_md);
        conRelated = view.findViewById(R.id.con_related_md);
        conGallery = view.findViewById(R.id.con_photoGallery_md);
        textViewComment = view.findViewById(R.id.textView_comment_bookDetail);
        recyclerViewComment = view.findViewById(R.id.recyclerView_comment_bookDetail);
        imageViewUser = view.findViewById(R.id.imageView_commentPro_bookDetail);
        imageViewSend = view.findViewById(R.id.imageView_comment_bookDetail);
        editTextComment = view.findViewById(R.id.editText_comment_bookDetail);
        conComment = view.findViewById(R.id.con_commentList_bookDetail);

        textViewName.setSelected(true);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        if (method.isLogin()) {
            Glide.with(getActivity().getApplicationContext()).load(method.getUserImageSmall())
                    .placeholder(R.drawable.profile)
                    .into(imageViewUser);
        }

        imageViewUser.setOnClickListener(v -> startActivity(new Intent(getActivity(), ViewImage.class)
                .putExtra("path", method.getUserImage())));

        recyclerViewCast.setHasFixedSize(true);
        RecyclerView.LayoutManager lmCast = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCast.setLayoutManager(lmCast);
        recyclerViewCast.setFocusable(false);
        recyclerViewCast.setNestedScrollingEnabled(false);

        recyclerViewGallery.setHasFixedSize(true);
        RecyclerView.LayoutManager lmGallery = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewGallery.setLayoutManager(lmGallery);
        recyclerViewGallery.setFocusable(false);
        recyclerViewGallery.setNestedScrollingEnabled(false);

        recyclerViewRelated.setHasFixedSize(true);
        RecyclerView.LayoutManager lmRecent = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRelated.setLayoutManager(lmRecent);
        recyclerViewRelated.setFocusable(false);
        recyclerViewRelated.setNestedScrollingEnabled(false);

        recyclerViewComment.setHasFixedSize(true);
        RecyclerView.LayoutManager lmComment = new LinearLayoutManager(getActivity());
        recyclerViewComment.setLayoutManager(lmComment);
        recyclerViewComment.setFocusable(false);
        recyclerViewComment.setNestedScrollingEnabled(false);

        callData(id);

        setHasOptionsMenu(true);
        return view;

    }

    private void callData(String id) {
        new Handler().postDelayed(() -> {
            if (method.isNetworkAvailable()) {
                if (method.isLogin()) {
                    detail(id, method.userId());
                } else {
                    detail(id, "0");
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }, 500);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        menu.clear();
        this.menu = menu;
        if (!isMenu) {
            isMenu = true;
        }
        inflater.inflate(R.menu.menu_md, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Subscribe
    public void getNotify(Events.AddComment addComment) {
        if (dataDetailRP != null) {
            if (dataDetailRP.getId().equals(addComment.getMovieId())) {
                commentLists.add(0, new CommentList(addComment.getCommentId(),
                        addComment.getMovieId(), addComment.getUserId(), addComment.getUserName(), addComment.getUserImage(),
                        addComment.getUserImageSmall(), addComment.getCommentText(), addComment.getCommentDate()));
                if (commentAdapter != null) {
                    commentAdapter.notifyDataSetChanged();
                    String textViewTotal = getResources().getString(R.string.view_all) + " " + "(" + addComment.getTotalComment() + ")";
                    textViewComment.setText(textViewTotal);
                }
            }
            if (commentLists.size() == 0) {
                conComment.setVisibility(View.GONE);
            } else {
                conComment.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe
    public void getNotify(Events.DeleteComment deleteComment) {
        if (dataDetailRP != null) {
            if (dataDetailRP.getId().equals(deleteComment.getMovieId())) {
                if (textViewComment != null) {
                    String buttonTotal = getResources().getString(R.string.view_all) + " " + "(" + deleteComment.getTotalComment() + ")";
                    textViewComment.setText(buttonTotal);
                }
            }
            if (deleteComment.getType().equals("all_comment")) {
                if (dataDetailRP.getId().equals(deleteComment.getMovieId())) {
                    commentLists.clear();
                    commentLists.addAll(deleteComment.getCommentLists());
                    if (commentAdapter != null) {
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (commentLists.size() == 0) {
                conComment.setVisibility(View.GONE);
            } else {
                conComment.setVisibility(View.VISIBLE);
            }
        }
    }

    private void detail(String id, String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("review_id", id);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "get_single_review");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataDetailRP> call = apiService.getMovieDetail(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataDetailRP>() {
                @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n"})
                @Override
                public void onResponse(@NotNull Call<DataDetailRP> call, @NotNull Response<DataDetailRP> response) {

                    if (getActivity() != null) {
                        try {
                            dataDetailRP = response.body();
                            assert dataDetailRP != null;

                            if (dataDetailRP.getStatus().equals("1")) {

                                if (dataDetailRP.getSuccess().equals("1")) {

                                    if (type.equals("deepLink")) {
                                        if (MainActivity.toolbar != null) {
                                            MainActivity.toolbar.setTitle(dataDetailRP.getMovie_title());
                                        }
                                    }

                                    if (isMenu) {
                                        MenuItem favItem = menu.findItem(R.id.fav_menu_md);
                                        MenuItem share = menu.findItem(R.id.share_menu_md);

                                        if (dataDetailRP.getIs_fav().equals("true")) {
                                            favItem.setIcon(R.drawable.fav_hov);
                                        } else {
                                            favItem.setIcon(R.drawable.fav);
                                        }

                                        favItem.setOnMenuItemClickListener(item -> {
                                            if (method.isLogin()) {
                                                FavouriteIF favouriteIF = (isFavourite, message) -> {
                                                    if (isFavourite) {
                                                        favItem.setIcon(R.drawable.fav_hov);
                                                    } else {
                                                        favItem.setIcon(R.drawable.fav);
                                                    }
                                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                                };
                                                addToFav(dataDetailRP.getId(), method.userId(), favouriteIF);
                                            } else {
                                                Method.loginBack = true;
                                                startActivity(new Intent(getActivity(), Login.class));
                                            }
                                            return false;
                                        });

                                        share.setOnMenuItemClickListener(item -> {

                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, dataDetailRP.getShare_link());
                                            startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_one)));

                                            return false;
                                        });

                                    }

                                    if (dataDetailRP.getIs_trailer().equals("true")) {
                                        imageViewPlay.setVisibility(View.VISIBLE);
                                    } else {
                                        imageViewPlay.setVisibility(View.GONE);
                                    }

                                    Glide.with(getActivity().getApplicationContext()).load(dataDetailRP.getMovie_cover_b())
                                            .placeholder(R.drawable.placeholder_landscape).into(imageView);

                                    imageView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ViewImage.class)
                                            .putExtra("path", dataDetailRP.getMovie_cover_b())));

                                    textViewName.setText(dataDetailRP.getMovie_title());
                                    textViewLag.setText(dataDetailRP.getLanguage_name());
                                    textViewDate.setText(dataDetailRP.getMovie_date());
                                    textViewView.setText(method.format(Double.parseDouble(dataDetailRP.getTotal_views())));
                                    textViewRating.setText("(" + dataDetailRP.getAdmin_rating() + ")");
                                    textViewUserRating.setText("(" + dataDetailRP.getRate_avg() +
                                            "/" + method.format(Double.parseDouble(dataDetailRP.getTotal_rate())) + ")");

                                    try {
                                        GradientDrawable gd = new GradientDrawable();
                                        gd.setShape(GradientDrawable.RECTANGLE);
                                        gd.setColor(Color.parseColor(dataDetailRP.getLanguage_bg()));
                                        gd.setCornerRadii(new float[]{40.0f, 40.0f, 40.0f, 40.0f, 40.0f, 40.0f, 40.0f, 40.0f}); // Make the border rounded border corner radius
                                        textViewLag.setBackground(gd);
                                    } catch (Exception e) {
                                        Log.d("error_show", e.toString());
                                    }

                                    webView.setBackgroundColor(Color.TRANSPARENT);
                                    webView.setFocusableInTouchMode(false);
                                    webView.setFocusable(false);
                                    webView.getSettings().setDefaultTextEncodingName("UTF-8");
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    String mimeType = "text/html";
                                    String encoding = "utf-8";

                                    String stringOpacity;
                                    if (method.isDarkMode()) {
                                        stringOpacity = "opacity: 0.8;";
                                    } else {
                                        stringOpacity = "";
                                    }

                                    String text = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/montserrat_regular.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + stringOpacity + "line-height:1.6}"
                                            + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                            + "</style>"
                                            + "</head>"
                                            + "<body>"
                                            + dataDetailRP.getMovie_desc()
                                            + "</body></html>";

                                    webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                                    ratingViewAdmin.setRating(Float.parseFloat(dataDetailRP.getAdmin_rating()));
                                    ratingViewUser.setRating(Float.parseFloat(dataDetailRP.getRate_avg()));

                                    if (!dataDetailRP.getMovie_casts().equals("")) {
                                        String CurrentString = dataDetailRP.getMovie_casts();
                                        String[] separated = CurrentString.split(",");
                                        CastAdapter castAdapter = new CastAdapter(getActivity(), separated);
                                        recyclerViewCast.setAdapter(castAdapter);
                                    }

                                    //movie gallery
                                    if (dataDetailRP.getGalleryLists().size() == 0) {
                                        conGallery.setVisibility(View.GONE);
                                    } else {
                                        GalleryAdapter galleryAdapter = new GalleryAdapter(getActivity(), "gallery", dataDetailRP.getGalleryLists(), onClick);
                                        recyclerViewGallery.setAdapter(galleryAdapter);
                                    }

                                    //related movie
                                    if (dataDetailRP.getRelatedMovieLists().size() == 0) {
                                        conRelated.setVisibility(View.GONE);
                                    } else {
                                        conRelated.setVisibility(View.VISIBLE);
                                        HomeDataAdapter homeDataAdapter = new HomeDataAdapter(getActivity(), dataDetailRP.getRelatedMovieLists(), "related_single", onClick);
                                        recyclerViewRelated.setAdapter(homeDataAdapter);
                                    }

                                    commentLists.addAll(dataDetailRP.getCommentLists());
                                    textViewComment.setText(getResources().getString(R.string.view_all) + " " + "(" + dataDetailRP.getTotal_comment() + ")");

                                    //movie comment
                                    if (commentLists.size() == 0) {
                                        conComment.setVisibility(View.GONE);
                                    } else {
                                        conComment.setVisibility(View.VISIBLE);
                                        commentAdapter = new CommentAdapter(getActivity(), commentLists);
                                        recyclerViewComment.setAdapter(commentAdapter);
                                    }

                                    conMain.setVisibility(View.VISIBLE);

                                    textViewComment.setOnClickListener(v -> startActivity(new Intent(getActivity(), AllComment.class)
                                            .putExtra("movieId", dataDetailRP.getId())));

                                    imageViewPlay.setOnClickListener(v -> {
                                        imageViewPlay.startAnimation(myAnim);
                                        startActivity(new Intent(getActivity(), YoutubePlayActivity.class)
                                                .putExtra("id", dataDetailRP.getVideo_id()));
                                    });

                                    imageViewUserRating.setOnClickListener(v -> {
                                        imageViewUserRating.startAnimation(myAnim);
                                        if (method.isLogin()) {
                                            myRating(id, method.userId());
                                        } else {
                                            Method.loginBack = true;
                                            startActivity(new Intent(getActivity(), Login.class));
                                        }
                                    });

                                    textViewRelated.setOnClickListener(v -> {
                                        String string = getResources().getString(R.string.related_movie);
                                        RelatedFragment relatedFragment = new RelatedFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id", dataDetailRP.getId());
                                        bundle.putString("languageId", dataDetailRP.getLanguage_id());
                                        relatedFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                                                relatedFragment, string).addToBackStack(string).commitAllowingStateLoss();
                                    });

                                    imageViewSend.setOnClickListener(v -> {
                                        if (method.isLogin()) {
                                            editTextComment.setError(null);
                                            String comment = editTextComment.getText().toString();
                                            if (comment.isEmpty()) {
                                                editTextComment.requestFocus();
                                                editTextComment.setError(getResources().getString(R.string.please_enter_comment));
                                            } else {
                                                editTextComment.clearFocus();
                                                imm.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);
                                                if (method.isNetworkAvailable()) {
                                                    comment(method.userId(), dataDetailRP.getId(), comment);
                                                } else {
                                                    method.alertBox(getResources().getString(R.string.internet_connection));
                                                }
                                            }
                                        } else {
                                            Method.loginBack = true;
                                            startActivity(new Intent(getActivity(), Login.class));
                                        }
                                    });

                                } else {
                                    conNoData.setVisibility(View.VISIBLE);
                                    method.alertBox(dataDetailRP.getMsg());
                                }

                            } else if (dataDetailRP.getStatus().equals("2")) {
                                method.suspend(dataDetailRP.getMessage());
                            } else {
                                method.alertBox(dataDetailRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<DataDetailRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    progressBar.setVisibility(View.GONE);
                    conNoData.setVisibility(View.VISIBLE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void myRating(String postId, String userId) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("post_id", postId);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "my_rating");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MyRatingRP> call = apiService.getMyRating(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<MyRatingRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<MyRatingRP> call, @NotNull Response<MyRatingRP> response) {

                    if (getActivity() != null) {
                        try {
                            MyRatingRP myRatingRP = response.body();
                            assert myRatingRP != null;

                            if (myRatingRP.getStatus().equals("1")) {

                                if (myRatingRP.getSuccess().equals("1")) {

                                    final Dialog dialog = new Dialog(getActivity());
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_rating);
                                    if (method.isRtl()) {
                                        dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                    }
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

                                    ImageView imageView = dialog.findViewById(R.id.imageView_close_dialogRating);
                                    RatingBar ratingBar = dialog.findViewById(R.id.ratingBar_dialogRating);
                                    MaterialButton button = dialog.findViewById(R.id.button_dialogRating);

                                    rate = Integer.parseInt(myRatingRP.getUser_rate());
                                    ratingBar.setRating(Float.parseFloat(myRatingRP.getUser_rate()));

                                    imageView.setOnClickListener(v -> dialog.dismiss());

                                    ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> rate = (int) rating);

                                    button.setOnClickListener(v -> {
                                        if (method.isNetworkAvailable()) {
                                            if (rate > 0) {
                                                rating(id, rate, userId);
                                                dialog.dismiss();
                                            } else {
                                                method.alertBox(getResources().getString(R.string.rating_status));
                                            }
                                        } else {
                                            method.alertBox(getResources().getString(R.string.internet_connection));
                                        }
                                    });

                                    dialog.show();

                                } else {
                                    method.alertBox(myRatingRP.getMsg());
                                }

                            } else if (myRatingRP.getStatus().equals("2")) {
                                method.suspend(myRatingRP.getMessage());
                            } else {
                                method.alertBox(myRatingRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NotNull Call<MyRatingRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void rating(String postId, int rate, String userId) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("rate", rate);
            jsObj.addProperty("post_id", postId);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "rating");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<RatingRP> call = apiService.submitRating(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<RatingRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<RatingRP> call, @NotNull Response<RatingRP> response) {

                    if (getActivity() != null) {
                        try {
                            RatingRP ratingRP = response.body();
                            assert ratingRP != null;

                            if (ratingRP.getStatus().equals("1")) {
                                if (ratingRP.getSuccess().equals("1")) {
                                    textViewUserRating.setText("(" + ratingRP.getRate_avg() + "/" + method.format(Double.parseDouble(ratingRP.getTotal_rate())) + ")");
                                    ratingViewUser.setRating(Float.parseFloat(ratingRP.getRate_avg()));
                                }
                                method.alertBox(ratingRP.getMsg());
                            } else if (ratingRP.getStatus().equals("2")) {
                                method.suspend(ratingRP.getMessage());
                            } else {
                                method.alertBox(ratingRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NotNull Call<RatingRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    //comment
    private void comment(final String userId, String movieId, final String comment) {

        if (getActivity() != null) {

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("movie_id", movieId);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("comment_text", comment);
            jsObj.addProperty("method_name", "user_comment");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserCommentRP> call = apiService.submitComment(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<UserCommentRP>() {
                @Override
                public void onResponse(@NotNull Call<UserCommentRP> call, @NotNull Response<UserCommentRP> response) {

                    if (getActivity() != null) {

                        try {
                            UserCommentRP userCommentRP = response.body();
                            assert userCommentRP != null;

                            if (userCommentRP.getStatus().equals("1")) {

                                if (userCommentRP.getSuccess().equals("1")) {

                                    editTextComment.setText("");

                                    commentLists.add(0, new CommentList(userCommentRP.getComment_id(),
                                            userCommentRP.getMovie_id(), userCommentRP.getUser_id(), userCommentRP.getUser_name(),
                                            userCommentRP.getUser_profile(), userCommentRP.getUser_profile_resize(), userCommentRP.getComment_text(), userCommentRP.getComment_date()));

                                    if (commentLists.size() == 0) {
                                        conComment.setVisibility(View.GONE);
                                    } else {
                                        conComment.setVisibility(View.VISIBLE);
                                        if (commentAdapter == null) {
                                            commentAdapter = new CommentAdapter(getActivity(), commentLists);
                                            recyclerViewComment.setAdapter(commentAdapter);
                                        } else {
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    String buttonTotal = getResources().getString(R.string.view_all) + " " + "(" + userCommentRP.getTotal_comment() + ")";
                                    textViewComment.setText(buttonTotal);

                                }

                                Toast.makeText(getActivity(), userCommentRP.getMsg(), Toast.LENGTH_SHORT).show();

                            } else if (userCommentRP.getStatus().equals("2")) {
                                method.suspend(userCommentRP.getMessage());
                            } else {
                                method.alertBox(userCommentRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(@NotNull Call<UserCommentRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    //add to favourite
    public void addToFav(String id, String userId, FavouriteIF favouriteIF) {

        if (getActivity() != null) {

            ProgressDialog progressDialog = new ProgressDialog(getActivity());

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("post_id", id);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "add_favourite");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<FavouriteRP> call = apiService.isFavouriteOrNot(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<FavouriteRP>() {
                @Override
                public void onResponse(@NotNull Call<FavouriteRP> call, @NotNull Response<FavouriteRP> response) {

                    if (getActivity() != null) {

                        try {
                            FavouriteRP favouriteRP = response.body();
                            assert favouriteRP != null;

                            if (favouriteRP.getStatus().equals("1")) {
                                if (favouriteRP.getSuccess().equals("1")) {
                                    favouriteIF.isFavourite(favouriteRP.isIs_favourite(), favouriteRP.getMsg());
                                } else {
                                    method.alertBox(favouriteRP.getMsg());
                                }
                            } else if (favouriteRP.getStatus().equals("2")) {
                                method.suspend(favouriteRP.getMessage());
                            } else {
                                method.alertBox(favouriteRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<FavouriteRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}

package com.mdnhs.graduateassist.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.Login;
import com.mdnhs.graduateassist.activity.MainActivity;
import com.mdnhs.graduateassist.activity.ViewImage;
import com.mdnhs.graduateassist.response.ProfileRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.Events;
import com.mdnhs.graduateassist.util.GlobalBus;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private MaterialButton buttonLogin;
    private CircleImageView imageView;
    private ConstraintLayout conMain, conNoData;
    private MaterialCardView cardViewPass, cardViewRecent;
    private MaterialTextView textViewName, textViewNotLogin;
    private ImageView imageViewLoginType, imageViewEdit, imageViewData;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.profile));
        }

        GlobalBus.getBus().register(this);

        method = new Method(getActivity());

        conMain = view.findViewById(R.id.con_main_pro);
        conNoData = view.findViewById(R.id.con_not_login);
        progressBar = view.findViewById(R.id.progressbar_pro);
        imageViewData = view.findViewById(R.id.imageView_not_login);
        buttonLogin = view.findViewById(R.id.button_not_login);
        textViewNotLogin = view.findViewById(R.id.textView_not_login);
        imageViewLoginType = view.findViewById(R.id.imageView_loginType_pro);
        imageView = view.findViewById(R.id.imageView_pro);
        imageViewEdit = view.findViewById(R.id.imageView_edit_pro);
        textViewName = view.findViewById(R.id.textView_name_pro);
        cardViewPass = view.findViewById(R.id.cardView_changePassword_pro);
        cardViewRecent = view.findViewById(R.id.cardView_recent_pro);

        buttonLogin.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finishAffinity();
        });

        progressBar.setVisibility(View.GONE);
        data(false, false);
        conMain.setVisibility(View.GONE);

        callData();

        return view;

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                profile(method.userId());
            } else {
                data(true, true);
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void data(boolean isShow, boolean isLogin) {
        if (isShow) {
            if (isLogin) {
                buttonLogin.setVisibility(View.VISIBLE);
                textViewNotLogin.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                buttonLogin.setVisibility(View.GONE);
                textViewNotLogin.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void getData(Events.ProfileUpdate profileUpdate) {
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.profile));
        }
        data(false, false);
        conMain.setVisibility(View.GONE);
        callData();
    }

    private void profile(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "user_profile");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProfileRP> call = apiService.getProfile(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProfileRP>() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onResponse(@NotNull Call<ProfileRP> call, @NotNull Response<ProfileRP> response) {

                    if (getActivity() != null) {

                        try {

                            ProfileRP profileRP = response.body();

                            assert profileRP != null;
                            if (profileRP.getStatus().equals("1")) {

                                if (profileRP.getSuccess().equals("1")) {

                                    method.editor.putString(method.userImage, profileRP.getPhoto());
                                    method.editor.putString(method.userImageSmall, profileRP.getPhoto_resize());
                                    method.editor.commit();

                                    String loginType = method.getLoginType();
                                    if (loginType.equals("google") || loginType.equals("facebook")) {
                                        cardViewPass.setVisibility(View.GONE);
                                        imageViewLoginType.setVisibility(View.VISIBLE);
                                        if (loginType.equals("google")) {
                                            imageViewLoginType.setImageDrawable(getResources().getDrawable(R.drawable.google_user_pro));
                                        } else {
                                            imageViewLoginType.setImageDrawable(getResources().getDrawable(R.drawable.fb_user_pro));
                                        }
                                    } else {
                                        cardViewPass.setVisibility(View.VISIBLE);
                                        imageViewLoginType.setVisibility(View.GONE);
                                    }

                                    Glide.with(getActivity().getApplicationContext()).load(profileRP.getPhoto())
                                            .placeholder(R.drawable.profile)
                                            .into(imageView);

                                    imageView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ViewImage.class)
                                            .putExtra("path", profileRP.getPhoto())));

                                    textViewName.setText(profileRP.getName());

                                    conMain.setVisibility(View.VISIBLE);

                                    imageViewEdit.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                                            new EditProfileFragment(), getResources().getString(R.string.edit_profile))
                                            .addToBackStack(getResources().getString(R.string.edit_profile)).commitAllowingStateLoss());

                                    cardViewPass.setOnClickListener(v -> {
                                        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("name", profileRP.getName());
                                        bundle.putString("image", profileRP.getPhoto());
                                        changePasswordFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                                                changePasswordFragment, getResources().getString(R.string.change_pass))
                                                .addToBackStack(getResources().getString(R.string.change_pass)).commitAllowingStateLoss();
                                    });

                                    cardViewRecent.setOnClickListener(v -> {
                                        DataListFragment dataListFragment = new DataListFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id", "");
                                        bundle.putString("type", "recentViewMovie");
                                        bundle.putString("title", getResources().getString(R.string.recent_movie));
                                        dataListFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                                                        dataListFragment, getResources().getString(R.string.recent_movie))
                                                .addToBackStack(getResources().getString(R.string.recent_movie)).commitAllowingStateLoss();
                                    });


                                } else {
                                    data(true, false);
                                    method.alertBox(profileRP.getMsg());
                                }

                            } else if (profileRP.getStatus().equals("2")) {
                                method.suspend(profileRP.getMessage());
                            } else {
                                data(true, false);
                                method.alertBox(profileRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<ProfileRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    data(true, false);
                    progressBar.setVisibility(View.GONE);
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

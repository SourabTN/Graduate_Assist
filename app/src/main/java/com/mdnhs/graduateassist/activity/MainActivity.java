package com.mdnhs.graduateassist.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.mdnhs.graduateassist.BuildConfig;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.fragment.CompareFragment;
import com.mdnhs.graduateassist.fragment.CostFragment;
import com.mdnhs.graduateassist.fragment.RankFragment;
import com.mdnhs.graduateassist.fragment.SubjectFragment;
import com.mdnhs.graduateassist.fragment.GroupFragment;
import com.mdnhs.graduateassist.fragment.HomeFragment;
import com.mdnhs.graduateassist.fragment.UniversityFragment;
import com.mdnhs.graduateassist.fragment.DataDetailFragment;
import com.mdnhs.graduateassist.fragment.DataListFragment;
import com.mdnhs.graduateassist.fragment.ProfileFragment;
import com.mdnhs.graduateassist.fragment.SettingFragment;
import com.mdnhs.graduateassist.response.AppRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.Constant;
import com.mdnhs.graduateassist.util.Events;
import com.mdnhs.graduateassist.util.GlobalBus;
import com.mdnhs.graduateassist.util.Method;
import com.facebook.login.LoginManager;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Method method;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    public static MaterialToolbar toolbar;
    private LinearLayout linearLayout;
    private ConsentForm form;
    private String id = "", type = "", title = "";
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalBus.getBus().register(this);

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id");
            title = intent.getStringExtra("title");
            type = intent.getStringExtra("type");
        }

        method = new Method(MainActivity.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        linearLayout = findViewById(R.id.linearLayout_main);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkLogin();

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                appDetail(method.userId());
            } else {
                appDetail("0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                String title;
                if (!(getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount()) instanceof SupportRequestManagerFragment)) {
                    title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount()).getTag();
                } else {
                    title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1).getTag();
                }
                if (title != null) {
                    if (title.equals("cat*")) {
                        toolbar.setTitle(method.category);
                    } else {
                        toolbar.setTitle(title);
                    }
                }
                super.onBackPressed();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.Please_click_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
    }

    public void backStackRemove() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //Checking if the item is in checked state or not, if not make it in checked state
        item.setChecked(!item.isChecked());

        //Closing drawer on item click
        drawer.closeDrawers();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.home:
                backStackRemove();
                selectDrawerItem(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                        getResources().getString(R.string.home)).commitAllowingStateLoss();
                return true;

            case R.id.latest:
                backStackRemove();
                selectDrawerItem(1);
                movieList("", "latestMovie", getResources().getString(R.string.latest));
                return true;

            case R.id.university:
                backStackRemove();
                selectDrawerItem(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new UniversityFragment(),
                        getResources().getString(R.string.university)).commitAllowingStateLoss();
                return true;

            case R.id.subject:
                backStackRemove();
                selectDrawerItem(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new SubjectFragment(),
                        getResources().getString(R.string.subject)).commitAllowingStateLoss();
                return true;

            case R.id.favorites:
                backStackRemove();
                selectDrawerItem(4);
                movieList("", "favMovie", getResources().getString(R.string.favorite));
                return true;

            case R.id.rank:
                backStackRemove();
                selectDrawerItem(5);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new RankFragment(),
                        getResources().getString(R.string.rank)).commitAllowingStateLoss();
                return true;

            case R.id.group:
                backStackRemove();
                selectDrawerItem(6);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new GroupFragment(),
                        getResources().getString(R.string.group)).commitAllowingStateLoss();
                return true;

            case R.id.cost:
                backStackRemove();
                selectDrawerItem(7);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new CostFragment(),
                        getResources().getString(R.string.cost)).commitAllowingStateLoss();
                return true;

            case R.id.compare:
                backStackRemove();
                selectDrawerItem(8);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new CompareFragment(),
                        getResources().getString(R.string.compare)).commitAllowingStateLoss();
                return true;

            case R.id.profile:
                backStackRemove();
                selectDrawerItem(9);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new ProfileFragment(),
                        getResources().getString(R.string.profile)).commitAllowingStateLoss();
                return true;

            case R.id.setting:
                backStackRemove();
                selectDrawerItem(10);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new SettingFragment(),
                        getResources().getString(R.string.setting)).commitAllowingStateLoss();
                return true;

            case R.id.login:
                deselectDrawerItem(11);
                if (method.isLogin()) {
                    logout();
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finishAffinity();
                }
                return true;

            default:
                return true;
        }
    }

    private void movieList(String id, String type, String title) {
        DataListFragment dataListFragment = new DataListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("type", type);
        bundle.putString("title", title);
        dataListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                dataListFragment, title).commitAllowingStateLoss();
    }

    public void selectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    public void deselectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setCheckable(false);
        navigationView.getMenu().getItem(position).setChecked(false);
    }

    public void appDetail(String userId) {

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("method_name", "get_app_details");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AppRP> call = apiService.getAppData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AppRP>() {
            @Override
            public void onResponse(@NotNull Call<AppRP> call, @NotNull Response<AppRP> response) {

                try {

                    Constant.appRP = response.body();
                    assert Constant.appRP != null;

                    method.initializeAds();

                    method.editor.putString(method.userImage, Constant.appRP.getPhoto());
                    method.editor.putString(method.userImageSmall, Constant.appRP.getPhoto_resize());
                    method.editor.commit();

                    if (Constant.appRP.getStatus().equals("1")) {

                        if (Constant.appRP.isApp_update_status() && Constant.appRP.getApp_new_version() > BuildConfig.VERSION_CODE) {
                            showAppDialog(Constant.appRP.getApp_update_desc(),
                                    Constant.appRP.getApp_redirect_url(),
                                    Constant.appRP.isCancel_update_status());
                        }

                        if (Constant.appRP.getInterstitial_ad_click().equals("")) {
                            Constant.interstitialAdShow = 0;
                        } else {
                            Constant.interstitialAdShow = Integer.parseInt(Constant.appRP.getInterstitial_ad_click());
                        }

                        if (Constant.appRP.getNative_ad_position().equals("")) {
                            Constant.nativeAdPos = 0;
                        } else {
                            Constant.nativeAdPos = Integer.parseInt(Constant.appRP.getNative_ad_position());
                        }

                        if(method.isAdmobFBAds()) {
                            checkForConsent();
                        } else {
                            method.showBannerAd(linearLayout);
                        }

                        try {

                            if (type.equals("genre") || type.equals("language")) {
                                movieList(id, type, title);
                            } else if (type.equals("single") || type.equals("deepLink")) {
                                DataDetailFragment dataDetailFragment = new DataDetailFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
                                bundle.putString("type", type);
                                bundle.putString("title", title);
                                bundle.putInt("position", 0);
                                dataDetailFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                                        dataDetailFragment, title).commitAllowingStateLoss();
                            } else {
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                                        getResources().getString(R.string.home)).commitAllowingStateLoss();
                                selectDrawerItem(0);
                            }


                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        method.alertBox(Constant.appRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(@NotNull Call<AppRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void checkForConsent() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {Constant.appRP.getPublisher_id()};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Method.personalization_ad = true;
                        method.showBannerAd(linearLayout);
                        break;
                    case NON_PERSONALIZED:
                        Method.personalization_ad = false;
                        method.showBannerAd(linearLayout);
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            Method.personalization_ad = true;
                            method.showBannerAd(linearLayout);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }

    public void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(Constant.appRP.getPrivacy_policy_link());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm();
                        // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                Method.personalization_ad = true;
                                method.showBannerAd(linearLayout);
                                break;
                            case NON_PERSONALIZED:
                            case UNKNOWN:
                                Method.personalization_ad = false;
                                method.showBannerAd(linearLayout);
                                break;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    private boolean getBannerAdType() {
        return Constant.appRP.getBanner_ad_type().equals("admob");
    }

    @Subscribe
    public void getLogin(Events.Login login) {
        if (method != null) {
            checkLogin();
        }
    }

    private void checkLogin() {
        if (navigationView != null) {
            int position = 11;
            if (method.isLogin()) {
                navigationView.getMenu().getItem(position).setIcon(R.drawable.signout);
                navigationView.getMenu().getItem(position).setTitle(getResources().getString(R.string.logout));
            } else {
                navigationView.getMenu().getItem(position).setIcon(R.drawable.signin);
                navigationView.getMenu().getItem(position).setTitle(getResources().getString(R.string.login));
            }
        }
    }

    //alert message box
    public void logout() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.DialogTitleTextStyle);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.logout_message));
        builder.setPositiveButton(getResources().getString(R.string.logout),
                (arg0, arg1) -> {
                    if (method.getLoginType().equals("google")) {

                        // Configure sign-in to request the ic_user_login's ID, email address, and basic
                        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        // Build a GoogleSignInClient with the options specified by gso.
                        //Google login
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(MainActivity.this, task -> {
                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(MainActivity.this, Login.class));
                                    finishAffinity();
                                });
                    } else if (method.getLoginType().equals("facebook")) {
                        LoginManager.getInstance().logOut();
                        method.editor.putBoolean(method.pref_login, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    } else {
                        method.editor.putBoolean(method.pref_login, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                (dialogInterface, i) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showAppDialog(String description, String link, boolean isCancel) {

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_app);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

        MaterialTextView textViewDes = dialog.findViewById(R.id.textView_description_dialog_update);
        MaterialButton buttonUpdate = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        if (isCancel) {
            buttonCancel.setVisibility(View.VISIBLE);
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        textViewDes.setText(description);

        buttonUpdate.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        super.onDestroy();
    }

}

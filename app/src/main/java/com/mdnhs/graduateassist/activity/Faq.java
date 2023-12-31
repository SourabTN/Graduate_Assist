package com.mdnhs.graduateassist.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.response.FaqRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Faq extends AppCompatActivity {

    private Method method;
    private WebView webView;
    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private ConstraintLayout conNoData;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        method = new Method(Faq.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar_faq);
        toolbar.setTitle(getResources().getString(R.string.faq));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        webView = findViewById(R.id.webView_faq);
        progressBar = findViewById(R.id.progressBar_faq);
        conNoData = findViewById(R.id.con_noDataFound);
        LinearLayout linearLayout = findViewById(R.id.linearLayout_faq);
        method.showBannerAd(linearLayout);

        conNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            faq();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void faq() {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Faq.this));
        jsObj.addProperty("method_name", "app_faq");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<FaqRP> call = apiService.getFaq(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<FaqRP>() {
            @Override
            public void onResponse(@NotNull Call<FaqRP> call, @NotNull Response<FaqRP> response) {

                try {
                    FaqRP faqRP = response.body();
                    assert faqRP != null;

                    if (faqRP.getStatus().equals("1")) {

                        webView.setBackgroundColor(Color.TRANSPARENT);
                        webView.setFocusableInTouchMode(false);
                        webView.setFocusable(false);
                        webView.getSettings().setDefaultTextEncodingName("UTF-8");
                        String mimeType = "text/html";
                        String encoding = "utf-8";

                       String text = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/montserrat_regular.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}"
                                + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                + "</style></head>"
                                + "<body>"
                                + faqRP.getApp_faq()
                                + "</body></html>";

                        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        method.alertBox(faqRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<FaqRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                conNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

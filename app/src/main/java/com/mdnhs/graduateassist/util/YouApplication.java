package com.mdnhs.graduateassist.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.multidex.MultiDex;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.SplashScreen;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class YouApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("5c2e22b8-ebc0-41b9-8866-3d44eb6f0242");
        OneSignal.setNotificationOpenedHandler(new NotificationExtenderExample());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/montserrat_regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

    class NotificationExtenderExample implements OneSignal.OSNotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {

            try {

                String id = result.getNotification().getAdditionalData().getString("id");
                String type = result.getNotification().getAdditionalData().getString("type");
                String title = result.getNotification().getAdditionalData().getString("title");
                String url = result.getNotification().getAdditionalData().getString("external_link");

                Intent intent;
                if (!url.equals("false") && !url.trim().isEmpty()) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                } else {
                    intent = new Intent(YouApplication.this, SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", id);
                    intent.putExtra("type", type);
                    intent.putExtra("title", title);
                }
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

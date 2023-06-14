package com.mdnhs.graduateassist.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.appbar.MaterialToolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        String path = getIntent().getStringExtra("path");

        Method method = new Method(ViewImage.this);
        method.forceRTLIfSupported();

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        method.changeStatusBarColor();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_viewImage);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView imageView = findViewById(R.id.imageView_viewImage);

        Glide.with(ViewImage.this).load(path)
                .placeholder(R.drawable.placeholder_landscape).into(imageView);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
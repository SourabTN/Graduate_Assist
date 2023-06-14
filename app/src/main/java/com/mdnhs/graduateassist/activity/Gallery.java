package com.mdnhs.graduateassist.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.adapter.GalleryDetailAdapter;
import com.mdnhs.graduateassist.util.Constant;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.appbar.MaterialToolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Gallery extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private ViewPager viewPager;
    private int selectedPosition = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        method = new Method(Gallery.this);
        method.forceRTLIfSupported();

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        method.changeStatusBarColor();

        selectedPosition = getIntent().getIntExtra("position", 0);

        toolbar = findViewById(R.id.toolbar_gallery);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = findViewById(R.id.viewpager_gallery);

        GalleryDetailAdapter galleryDetailAdapter = new GalleryDetailAdapter(Gallery.this, Constant.galleryLists);
        viewPager.setAdapter(galleryDetailAdapter);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);

    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

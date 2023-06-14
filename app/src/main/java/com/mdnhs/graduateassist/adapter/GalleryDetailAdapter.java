package com.mdnhs.graduateassist.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.item.GalleryList;
import com.mdnhs.graduateassist.util.TouchImageView;

import java.util.List;

public class GalleryDetailAdapter extends PagerAdapter {

    private Activity activity;
    private List<GalleryList> galleryLists;

    public GalleryDetailAdapter(Activity activity, List<GalleryList> galleryLists) {
        this.activity = activity;
        this.galleryLists = galleryLists;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.gallary_detail_adapter, container, false);

        TouchImageView imageView = view.findViewById(R.id.imageView_gallery_detail_adapter);

        Glide.with(activity).load(galleryLists.get(position).getImage_name())
                .placeholder(R.drawable.placeholder_portable).into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return galleryLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}

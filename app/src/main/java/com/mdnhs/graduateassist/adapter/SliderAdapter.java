package com.mdnhs.graduateassist.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.SliderList;
import com.mdnhs.graduateassist.util.EnchantedViewPager;
import com.mdnhs.graduateassist.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private Method method;
    private String type;
    private Activity activity;
    private List<SliderList> sliderLists;

    public SliderAdapter(Activity activity, String type, List<SliderList> sliderLists, OnClick onClick) {
        this.activity = activity;
        this.sliderLists = sliderLists;
        this.type = type;
        method = new Method(activity, onClick);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View view;
        if (sliderLists.get(position).getType().equals("external")) {

            view = activity.getLayoutInflater().inflate(R.layout.slider_external_adpter, container, false);

            ImageView imageView = view.findViewById(R.id.imageView_sliderExternal_adapter);
            MaterialTextView textView = view.findViewById(R.id.textView_name_sliderExternal_adapter);

            Glide.with(activity).load(sliderLists.get(position).getImage())
                    .placeholder(R.drawable.placeholder_landscape).into(imageView);
            textView.setText(sliderLists.get(position).getTitle());

            imageView.setOnClickListener(v -> {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sliderLists.get(position).getExternal_link())));
                } catch (Exception e) {
                    method.alertBox(activity.getResources().getString(R.string.wrong));
                }
            });

        } else {

            view = activity.getLayoutInflater().inflate(R.layout.slider_adapter, container, false);

            ImageView imageView = view.findViewById(R.id.imageView_slider_adapter);
            MaterialTextView textViewLanguage = view.findViewById(R.id.textView_lag_slider_adapter);
            MaterialTextView textViewName = view.findViewById(R.id.textView_name_slider_adapter);
            RatingView ratingView = view.findViewById(R.id.ratingBar_slider_adapter);
            MaterialTextView textViewRating = view.findViewById(R.id.textView_rating_slider_adapter);

            Glide.with(activity).load(sliderLists.get(position).getImage())
                    .placeholder(R.drawable.placeholder_landscape).into(imageView);

            textViewLanguage.setText(sliderLists.get(position).getLanguage_name());
            textViewName.setText(sliderLists.get(position).getTitle());
            ratingView.setRating(Float.parseFloat(sliderLists.get(position).getRate_avg()));
            textViewRating.setText("(" + sliderLists.get(position).getRate_avg()
                    + "/" + sliderLists.get(position).getTotal_rate() + ")");

            try {
                GradientDrawable gd = new GradientDrawable();
                gd.setShape(GradientDrawable.RECTANGLE);
                gd.setColor(Color.parseColor(sliderLists.get(position).getLanguage_bg()));
                gd.setCornerRadii(new float[]{40.0f, 40.0f, 40.0f, 40.0f, 40.0f, 40.0f, 40.0f, 40.0f}); // Make the border rounded border corner radius
                textViewLanguage.setBackground(gd);
            } catch (Exception e) {
                Log.d("error_show", e.toString());
            }

            imageView.setOnClickListener(v -> method.click(position, type, sliderLists.get(position).getId(), sliderLists.get(position).getTitle()));

        }

        view.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        container.addView(view, 0);
        return view;

    }

    @Override
    public int getCount() {
        return sliderLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}


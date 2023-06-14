package com.mdnhs.graduateassist.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.DataList;
import com.mdnhs.graduateassist.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeDataAdapter extends RecyclerView.Adapter<HomeDataAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private String string;
    private List<DataList> dataLists;

    public HomeDataAdapter(Activity activity, List<DataList> dataLists, String string, OnClick onClick) {
        this.activity = activity;
        this.dataLists = dataLists;
        this.string = string;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_data_adapter, parent, false);

        return new HomeDataAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Glide.with(activity).load(dataLists.get(position).getMovie_thumbnail_s())
                .placeholder(R.drawable.placeholder_portable).into(holder.imageView);

        holder.textViewLanguage.setText(dataLists.get(position).getLanguage_name());
        holder.textViewName.setText(dataLists.get(position).getMovie_title());
        holder.ratingView.setRating(Float.parseFloat(dataLists.get(position).getRate_avg()));
        holder.textViewRating.setText("(" + dataLists.get(position).getRate_avg()
                + "/" + dataLists.get(position).getTotal_rate() + ")");

        try {
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.RECTANGLE);
            gd.setColor(Color.parseColor(dataLists.get(position).getLanguage_bg()));
            if (method.isRtl()) {
                gd.setCornerRadii(new float[]{40.0f, 40.0f, 0, 0, 0, 0, 40.0f, 40.0f}); // Make the border rounded border corner radius
            } else {
                gd.setCornerRadii(new float[]{0, 0, 40.0f, 40.0f, 40.0f, 40.0f, 0, 0}); // Make the border rounded border corner radius
            }
            holder.textViewLanguage.setBackground(gd);
        } catch (Exception e) {
            Log.d("error_show", e.toString());
        }

        holder.imageView.setOnClickListener(v -> method.click(position, string, dataLists.get(position).getId(), dataLists.get(position).getMovie_title()));

    }

    @Override
    public int getItemCount() {
        return dataLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private RatingView ratingView;
        private MaterialTextView textViewName, textViewRating, textViewLanguage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_homeMovie_adapter);
            ratingView = itemView.findViewById(R.id.ratingBar_home_movie_adapter);
            textViewLanguage = itemView.findViewById(R.id.textView_lag_home_movie_adapter);
            textViewName = itemView.findViewById(R.id.textView_name_home_movie_adapter);
            textViewRating = itemView.findViewById(R.id.textView_rating_home_movie_adapter);

        }
    }
}

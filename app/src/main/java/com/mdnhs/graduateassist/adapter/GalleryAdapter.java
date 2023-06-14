package com.mdnhs.graduateassist.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.GalleryList;
import com.mdnhs.graduateassist.util.Method;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private String string;
    private List<GalleryList> galleryLists;

    public GalleryAdapter(Activity activity, String string, List<GalleryList> galleryLists, OnClick onClick) {
        this.activity = activity;
        this.string = string;
        this.galleryLists = galleryLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.gallary_adapter, parent, false);

        return new GalleryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Glide.with(activity).load(galleryLists.get(position).getImage_name())
                .placeholder(R.drawable.placeholder_gallery).into(holder.imageView);

        holder.imageView.setOnClickListener(v -> method.click(position, string, "", ""));

    }

    @Override
    public int getItemCount() {
        return galleryLists.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_gallery_adapter);

        }
    }
}

package com.mdnhs.graduateassist.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.SubjectList;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class SubjectAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Method method;
    private String string;
    private List<SubjectList> genresLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public SubjectAdapter(Activity activity, List<SubjectList> genresLists, String string, OnClick onClick) {
        this.activity = activity;
        this.string = string;
        this.genresLists = genresLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.subject_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            if (!genresLists.get(position).getGenre_image_thumb().equals("")) {
                Glide.with(activity).load(genresLists.get(position).getGenre_image_thumb())
                        .placeholder(R.drawable.placeholder_landscape).into(viewHolder.imageView);
            }

            viewHolder.cardView.setOnClickListener(v -> method.click(position, string, genresLists.get(position).getGid(), genresLists.get(position).getGenre_name()));
            viewHolder.textView.setText(genresLists.get(position).getGenre_name());

        }

    }

    @Override
    public int getItemCount() {
        return genresLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == genresLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private MaterialTextView textView;
        private MaterialCardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_genres_adapter);
            textView = itemView.findViewById(R.id.textView_genres_adapter);
            cardView = itemView.findViewById(R.id.cardView_genres_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }

}

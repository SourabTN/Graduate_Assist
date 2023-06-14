package com.mdnhs.graduateassist.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.HomeDataList;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeLanguageMovieAdapter extends RecyclerView.Adapter<HomeLanguageMovieAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private OnClick onClick;
    private String string;
    private List<HomeDataList> homeDataLists;

    public HomeLanguageMovieAdapter(Activity activity, List<HomeDataList> homeDataLists, String string, OnClick onClick) {
        this.activity = activity;
        this.string = string;
        this.homeDataLists = homeDataLists;
        this.onClick = onClick;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public HomeLanguageMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.other_language_adapter, parent, false);

        return new HomeLanguageMovieAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeLanguageMovieAdapter.ViewHolder holder, int position) {

        holder.textView.setText(homeDataLists.get(position).getLanguage_name());

        holder.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setFocusable(false);

        holder.textViewViewAll.setOnClickListener(v -> method.click(position, string, homeDataLists.get(position).getLanguage_id(), homeDataLists.get(position).getLanguage_name()));

        if (homeDataLists.get(position).getMovieLists().size() != 0) {
            holder.con.setVisibility(View.VISIBLE);
            HomeDataAdapter homeDataAdapter = new HomeDataAdapter(activity, homeDataLists.get(position).getMovieLists(), "languageMovieList", onClick);
            holder.recyclerView.setAdapter(homeDataAdapter);
        } else {
            holder.con.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return homeDataLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout con;
        private RecyclerView recyclerView;
        private MaterialTextView textView, textViewViewAll;

        public ViewHolder(View itemView) {
            super(itemView);

            con = itemView.findViewById(R.id.con_latest_home);
            textView = itemView.findViewById(R.id.textView_movie_adapter);
            textViewViewAll = itemView.findViewById(R.id.textView_viewAll_movie_adapter);
            recyclerView = itemView.findViewById(R.id.recyclerview_movie_adapter);

        }
    }
}

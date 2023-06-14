package com.mdnhs.graduateassist.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.UniversityList;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class HomeUniversityAdapter extends RecyclerView.Adapter<HomeUniversityAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String string;
    private List<UniversityList> universityLists;

    public HomeUniversityAdapter(Activity activity, List<UniversityList> universityLists, String string, OnClick onClick) {
        this.activity = activity;
        this.string = string;
        this.universityLists = universityLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_university_adapter, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.textView.setText(universityLists.get(position).getLanguage_name());

        holder.cardView.setCardBackgroundColor(Color.parseColor(universityLists.get(position).getLanguage_bg()));

        holder.cardView.setOnClickListener(v -> method.click(position, string, universityLists.get(position).getLanguage_id(), universityLists.get(position).getLanguage_name()));

    }

    @Override
    public int getItemCount() {
        return universityLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView;
        private MaterialCardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView_home_lag_adapter);
            cardView = itemView.findViewById(R.id.cardView_home_lag_adapter);

        }
    }
}

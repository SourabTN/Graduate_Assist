package com.mdnhs.graduateassist.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.UniversityList;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class DataUniversityAdapter extends RecyclerView.Adapter<DataUniversityAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private int rowIndex = -1;
    private List<UniversityList> universityLists;

    public DataUniversityAdapter(Activity activity, List<UniversityList> universityLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.universityLists = universityLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.data_university_adapter, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (rowIndex > -1) {
            if (rowIndex == position) {
                holder.cardView.setStrokeWidth(6);
                holder.cardView.setStrokeColor(activity.getResources().getColor(R.color.cardView_stork_ml_adapter));
            } else {
                holder.cardView.setStrokeWidth(0);
            }
        }

        holder.textView.setText(universityLists.get(position).getLanguage_name());
        holder.cardView.setCardBackgroundColor(Color.parseColor(universityLists.get(position).getLanguage_bg()));

        holder.cardView.setOnClickListener(v -> {
            rowIndex = position;
            notifyDataSetChanged();
            method.click(position, type, universityLists.get(position).getLanguage_id(), universityLists.get(position).getLanguage_name());
        });

    }

    @Override
    public int getItemCount() {
        return universityLists.size();
    }

    public void select(int position) {
        rowIndex = position;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView;
        private MaterialCardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView_ml_adapter);
            cardView = itemView.findViewById(R.id.cardView_ml_adapter);

        }
    }

}

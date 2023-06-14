package com.mdnhs.graduateassist.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mdnhs.graduateassist.R;
import com.google.android.material.textview.MaterialTextView;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private Activity activity;
    private String[] separated;

    public CastAdapter(Activity activity, String[] separated) {
        this.activity = activity;
        this.separated = separated;
    }

    @NonNull
    @Override
    public CastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.cast_adapter, parent, false);

        return new CastAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdapter.ViewHolder holder, int position) {

        holder.textView.setText(separated[position].trim());

    }

    @Override
    public int getItemCount() {
        return separated.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView_cast_adapter);

        }
    }
}

package com.mdnhs.graduateassist.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.ViewImage;
import com.mdnhs.graduateassist.item.CommentList;
import com.mdnhs.graduateassist.response.DeleteCommentRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.Events;
import com.mdnhs.graduateassist.util.GlobalBus;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Method method;
    private Animation myAnim;
    private Activity activity;
    private List<CommentList> commentLists;

    public CommentAdapter(Activity activity, List<CommentList> commentLists) {
        this.activity = activity;
        this.commentLists = commentLists;
        method = new Method(activity);
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.comment_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {

        if (!commentLists.get(position).getUser_profile().equals("")) {
            Glide.with(activity).load(commentLists.get(position).getUser_profile_resize())
                    .placeholder(R.drawable.profile).into(holder.circleImageView);
        } else {
            // make sure Glide doesn't load anything into this view until told otherwise
            Glide.with(activity).clear(holder.circleImageView);
        }

        holder.circleImageView.setOnClickListener(v -> {
            activity.startActivity(new Intent(activity, ViewImage.class)
                    .putExtra("path", commentLists.get(position).getUser_profile()));
        });

        if (method.isLogin()) {
            if (method.userId().equals(commentLists.get(position).getUser_id())) {
                holder.textViewDelete.setVisibility(View.VISIBLE);
            } else {
                holder.textViewDelete.setVisibility(View.GONE);
            }
        } else {
            holder.textViewDelete.setVisibility(View.GONE);
        }

        holder.textViewName.setText(commentLists.get(position).getUser_name());
        holder.textViewDate.setText(commentLists.get(position).getComment_date());
        holder.textViewComment.setText(commentLists.get(position).getComment_text());

        holder.textViewDelete.setOnClickListener(v -> {

            holder.textViewDelete.startAnimation(myAnim);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
            builder.setMessage(activity.getResources().getString(R.string.delete_comment));
            builder.setCancelable(false);
            builder.setPositiveButton(activity.getResources().getString(R.string.delete),
                    (arg0, arg1) -> delete(commentLists.get(position).getMovie_id(), commentLists.get(position).getComment_id()));
            builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                    (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return commentLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private MaterialTextView textViewName, textViewDate, textViewComment, textViewDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.imageView_comment_adapter);
            textViewName = itemView.findViewById(R.id.textView_userName_comment_adapter);
            textViewDate = itemView.findViewById(R.id.textView_date_comment_adapter);
            textViewComment = itemView.findViewById(R.id.textView_comment_adapter);
            textViewDelete = itemView.findViewById(R.id.textView_delete_adapter);

        }
    }

    public void delete(String movieId, String commentId) {

        ProgressDialog progressDialog = new ProgressDialog(activity);

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("movie_id", movieId);
        jsObj.addProperty("comment_id", commentId);
        jsObj.addProperty("method_name", "delete_comment");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DeleteCommentRP> call = apiService.deleteComment(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DeleteCommentRP>() {
            @Override
            public void onResponse(@NotNull Call<DeleteCommentRP> call, @NotNull Response<DeleteCommentRP> response) {

                try {
                    DeleteCommentRP deleteCommentRP = response.body();
                    assert deleteCommentRP != null;

                    if (deleteCommentRP.getStatus().equals("1")) {

                        if (deleteCommentRP.getSuccess().equals("1")) {

                            commentLists.clear();
                            commentLists.addAll(deleteCommentRP.getCommentLists());

                            notifyDataSetChanged();

                            Events.DeleteComment deleteComment = new Events.DeleteComment(deleteCommentRP.getTotal_comment(), movieId, "comment", null);
                            GlobalBus.getBus().post(deleteComment);

                            Toast.makeText(activity, deleteCommentRP.getMsg(), Toast.LENGTH_SHORT).show();

                        } else {
                            method.alertBox(deleteCommentRP.getMsg());
                        }

                    } else {
                        method.alertBox(deleteCommentRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<DeleteCommentRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });

    }

}

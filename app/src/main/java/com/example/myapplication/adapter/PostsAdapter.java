package com.example.myapplication.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.DetailPostActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postsList;

    public PostsAdapter(Context context, List<Post> postsList) {
        this.context = context;
        this.postsList =  new ArrayList<>(postsList);;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post posts = postsList.get(position);
        holder.titleTextView.setText(posts.getTitle());
        holder.descriptionTextView.setText(posts.getDescription());

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        try {
            holder.dateTextView.setText(outputFormat.format(inputFormat.parse(posts.getUpdatedAt())));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (posts.getUser() != null) {
            holder.userNameTextView.setText(posts.getUser().getUsername());
            if (posts.getUser().getProfileImage() != null && !posts.getUser().getProfileImage().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(posts.getUser().getProfileImage())
                        .into(holder.userImage);
            } else {
                holder.userImage.setImageResource(R.drawable.baseline_account_circle_24);
            }
        }
        Glide.with(holder.itemView.getContext())
                .load(posts.getImage())
                .into(holder.postImageView);

        holder.post_content.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailPostActivity.class);
            intent.putExtra("postId", posts.getId());
            intent.putExtra("userId", posts.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }


    public void addPosts(List<Post> newPosts) {
        int startPosition = postsList.size();
        postsList.addAll(newPosts);
        notifyItemRangeInserted(startPosition, newPosts.size());
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView, userImage;
        TextView titleTextView, descriptionTextView, userNameTextView, dateTextView;
        LinearLayout post_content;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.iv_post_image);
            titleTextView = itemView.findViewById(R.id.tv_title);
            dateTextView = itemView.findViewById(R.id.tv_date);
            descriptionTextView = itemView.findViewById(R.id.tv_description);
            userNameTextView = itemView.findViewById(R.id.tv_username);
            userImage = itemView.findViewById(R.id.iv_user_profile);
            post_content = itemView.findViewById(R.id.post_content);
        }
    }
}

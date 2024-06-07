package com.example.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Posts;
import com.example.myapplication.model.PostsResponse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Posts> postsList;

    public PostsAdapter(PostsResponse postsResponse) {
        this.postsList = postsResponse.getPosts();
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
        Posts posts = postsList.get(position);
        holder.titleTextView.setText(posts.getTitle());
        holder.descriptionTextView.setText(posts.getDescription());

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        try {
            holder.dateTextView.setText(outputFormat.format(inputFormat.parse(posts.getCreatedAt())));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (posts.getUser() != null) {
            holder.userNameTextView.setText(posts.getUser().getUsername());
            Glide.with(holder.itemView.getContext())
                    .load(posts.getUser().getProfileImage())
                    .into(holder.userImage);
        }
        Glide.with(holder.itemView.getContext())
                .load(posts.getImage())
                .into(holder.postImageView);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView, userImage;
        TextView titleTextView, descriptionTextView, userNameTextView, dateTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.iv_post_image);
            titleTextView = itemView.findViewById(R.id.tv_title);
            dateTextView = itemView.findViewById(R.id.tv_date);
            descriptionTextView = itemView.findViewById(R.id.tv_description);
            userNameTextView = itemView.findViewById(R.id.tv_username);
            userImage = itemView.findViewById(R.id.iv_user_profile);
        }
    }
}

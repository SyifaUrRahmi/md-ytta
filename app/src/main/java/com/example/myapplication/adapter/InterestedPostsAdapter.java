package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.DetailPostActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class InterestedPostsAdapter extends RecyclerView.Adapter<InterestedPostsAdapter.InterestedPostViewHolder> {

    private Context context;
    private List<Post> postsList;

    public InterestedPostsAdapter(Context context, List<Post> postsList) {
        this.context = context;
        this.postsList = postsList;
    }
    public void addPost(Post post) {
        postsList.add(post);
        notifyItemInserted(postsList.size() - 1);
    }

    @NonNull
    @Override
    public InterestedPostsAdapter.InterestedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interested_posts, parent, false);
        return new InterestedPostsAdapter.InterestedPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestedPostsAdapter.InterestedPostViewHolder holder, int position) {
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

        Glide.with(holder.itemView.getContext())
                .load(posts.getImage())
                .into(holder.postImageView);

        holder.itemView.setOnClickListener(view -> {
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

    public class InterestedPostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView;
        TextView titleTextView, descriptionTextView, dateTextView;
        public InterestedPostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.iv_post_image);
            titleTextView = itemView.findViewById(R.id.tv_title);
            dateTextView = itemView.findViewById(R.id.tv_date);
            descriptionTextView = itemView.findViewById(R.id.tv_description);
        }
    }
}

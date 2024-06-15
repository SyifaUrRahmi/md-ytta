package com.example.myapplication.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.DetailPostActivity;
import com.example.myapplication.R;
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postsList;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = currentUser.getUid();


    public PostsAdapter(Context context, List<Post> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    public void setPosts(List<Post> posts) {
        this.postsList = posts;
        notifyDataSetChanged();
    }

    public void addPosts(List<Post> posts) {
        this.postsList.addAll(posts);
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
        String postId = posts.getId();
        holder.titleTextView.setText(posts.getTitle());
        holder.descriptionTextView.setText(posts.getDescription());
        holder.tv_interest_count.setText(String.valueOf(posts.getInterestCount()));

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
        Call<User> call = APIClient.getClient().create(APIService.class).getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        List<String> interestPosts = user.getInterestedPosts();
                        if (interestPosts.contains(postId)) {
                            holder.btn_interest.setImageResource(R.drawable.baseline_favorite_24);
                            holder.btn_interest.setOnClickListener(view -> {
                                Call<Void> unInterest = APIClient.getClient().create(APIService.class).markunInterest(userId, postId);
                                unInterest.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            holder.btn_interest.setImageResource(R.drawable.baseline_favorite_border_24);

                                        } else {
                                            Toast.makeText(context, "Failed to mark interest", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            });
                        } else {
                            holder.btn_interest.setImageResource(R.drawable.baseline_favorite_border_24);
                            holder.btn_interest.setOnClickListener(view -> {
                                Call<Void> interest = APIClient.getClient().create(APIService.class).markInterest(userId, postId);
                                interest.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            holder.btn_interest.setImageResource(R.drawable.baseline_favorite_24);

                                        } else {
                                            Toast.makeText(context, "Failed to mark interest", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            });
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView, userImage, btn_interest;
        TextView titleTextView, descriptionTextView, userNameTextView, dateTextView, tv_interest_count;
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
            tv_interest_count = itemView.findViewById(R.id.tv_interest_count);
            btn_interest = itemView.findViewById(R.id.btn_interest);
        }
    }
}

package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.NetworkUtil;
import com.example.myapplication.R;
import com.example.myapplication.adapter.PostsAdapter;
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.PostsResponse;
import com.example.myapplication.model.User;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;

    private ProgressBar progressBar;
    private LinearLayout noInternetLayout;
    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.rv_posts);
        progressBar = view.findViewById(R.id.progress_bar);
        noInternetLayout = view.findViewById(R.id.no_internet_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noInternetLayout.setOnClickListener(v -> {
            if (NetworkUtil.isConnected(getContext())) {
                fetchPosts();
            } else {
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        if (NetworkUtil.isConnected(getContext())) {
            fetchPosts();
        } else {
            showNoInternetConnection();
        }

        return view;
    }

    private void fetchPosts() {
        showLoading(true);
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<PostsResponse> call = apiService.getPosts();

        call.enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showLoading(false);
                    PostsResponse postsResponse = response.body();

                    List<Post> posts = postsResponse.getPosts();
                    Collections.sort(posts, (post1, post2) -> post2.getUpdatedAt().compareTo(post1.getUpdatedAt()));
                    postsAdapter = new PostsAdapter(getContext(), posts);
                    recyclerView.setAdapter(postsAdapter);
                    for (Post post : posts) {
                        fetchUserForPost(post);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchUserForPost(Post post) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<User> call = apiService.getUser(post.getUserId());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    post.setUser(user);
                    postsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
    }

    private void showNoInternetConnection() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }


}



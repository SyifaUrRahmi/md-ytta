package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
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
    private Button btnLoadMore;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.rv_posts);
        progressBar = view.findViewById(R.id.progress_bar);
        noInternetLayout = view.findViewById(R.id.no_internet_layout);
        btnLoadMore = view.findViewById(R.id.btn_load_more);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        postsAdapter = new PostsAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(postsAdapter);

        btnLoadMore.setOnClickListener(v -> {
            if (!isLoading && !isLastPage) {
                fetchPosts();
            }
        });

        currentPage = 1;
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
        isLoading = true;
        showLoading(true);
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<PostsResponse> call = apiService.getPosts(currentPage, 10);

        call.enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                isLoading = false;
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    PostsResponse postsResponse = response.body();

                    List<Post> newPosts = postsResponse.getPosts();

                    if (currentPage == 1) {
                        postsAdapter.setPosts(newPosts);
                    } else {
                        postsAdapter.addPosts(newPosts);
                    }

                    if (newPosts.isEmpty()) {
                        isLastPage = true;
                        btnLoadMore.setVisibility(View.GONE);
                    } else {
                        currentPage++;
                        btnLoadMore.setVisibility(View.VISIBLE);
                    }

                    for (Post post : newPosts) {
                        fetchUserForPost(post);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                isLoading = false;
                showLoading(false);
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

    private void showNoInternetConnection() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        noInternetLayout.setVisibility(View.GONE);
    }
}

package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.PostsAdapter;
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.model.Posts;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.rv_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchPosts();
        return view;
    }

    private void fetchPosts() {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<PostsResponse> call = apiService.getPosts();

        call.enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PostsResponse postsResponse = response.body();

                    List<Posts> posts = postsResponse.getPosts();
                    Collections.sort(posts, (post1, post2) -> post2.getUpdatedAt().compareTo(post1.getUpdatedAt()));
                    postsAdapter = new PostsAdapter(posts);
                    recyclerView.setAdapter(postsAdapter);
                    for (Posts post : posts) {
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


    private void fetchUserForPost(Posts post) {
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
}

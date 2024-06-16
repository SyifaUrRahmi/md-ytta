package com.example.myapplication.fragment.profile_fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MyPostsAdapter;
import com.example.myapplication.adapter.PostsAdapter;
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.PostsResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyPostsAdapter mypostsAdapter;

    TextView tv_no_data;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        recyclerView = view.findViewById(R.id.rv_posts);
        tv_no_data = view.findViewById(R.id.tv_no_data);
        mypostsAdapter = new MyPostsAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(mypostsAdapter);
        fetchMyPosts();
        return view;


    }

    private void fetchMyPosts() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<PostsResponse> call = apiService.getMyPost(userId);

        call.enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                PostsResponse postsResponse = response.body();

                List<Post> newPosts = postsResponse.getPosts();
                if (newPosts != null && !newPosts.isEmpty()) {
                    mypostsAdapter.setPosts(newPosts);
                    tv_no_data.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {

            }
        });
    }
}
package com.example.myapplication.fragment.profile_fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DetailPostActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.InterestedPostsAdapter;
import com.example.myapplication.adapter.MyPostsAdapter;
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InterestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private InterestedPostsAdapter interestedpostsAdapter;
    TextView tv_no_data;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = currentUser.getUid();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interests, container, false);
        recyclerView = view.findViewById(R.id.rv_posts);
        tv_no_data = view.findViewById(R.id.tv_no_data);
        interestedpostsAdapter = new InterestedPostsAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(interestedpostsAdapter);
        getUser(userId);
        return view;
    }

    private void getUser(String userId) {
        Call<User> call = APIClient.getClient().create(APIService.class).getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user != null) {
                    List<String> interestedPostIds = user.getInterestedPosts();
                    if (interestedPostIds != null && !interestedPostIds.isEmpty()) {
                        for (String postId : interestedPostIds) {
                            fetchInterestedPosts(postId);
                        }
                        tv_no_data.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void fetchInterestedPosts(String postId) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<Post> call = apiService.getPost(postId);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Post post = response.body();
                    interestedpostsAdapter.addPost(post);

                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }


}
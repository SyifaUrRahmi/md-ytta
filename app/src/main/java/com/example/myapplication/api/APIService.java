package com.example.myapplication.api;



import com.example.myapplication.model.LoginRequest;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.PostsResponse;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.RegisterResponse;
import com.example.myapplication.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @POST("/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("/posts")
    Call<PostsResponse> getPosts();

    @GET("/user/{userId}")
    Call<User> getUser(@Path("userId") String userId);
}


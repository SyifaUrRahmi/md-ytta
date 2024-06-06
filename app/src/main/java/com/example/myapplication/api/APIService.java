package com.example.myapplication.api;



import com.example.myapplication.model.LoginRequest;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}


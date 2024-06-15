package com.example.myapplication.api;



import com.example.myapplication.model.LoginRequest;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.PostsResponse;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.RegisterResponse;
import com.example.myapplication.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @POST("/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("/page/posts")
    Call<PostsResponse> getPosts(@Query("page") int page, @Query("size") int size);

    @GET("/user/{userId}")
    Call<User> getUser(@Path("userId") String userId);

    @Multipart
    @POST("/{userId}/post")
    Call<ResponseBody> uploadPost(
            @Path("userId") String userId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("status") RequestBody status,
            @Part("type") RequestBody type,
            @Part MultipartBody.Part image
    );
    @Multipart
    @PUT("/{userId}/post/{postId}")
    Call<ResponseBody> editPost(
            @Path("userId") String userId,
            @Path("postId") String postId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("status") RequestBody status,
            @Part("type") RequestBody type,
            @Part MultipartBody.Part image
    );

    @GET("/post/{postId}")
    Call<Post> getPost(@Path("postId") String postId);

    @DELETE("/{userId}/delete/{postId}")
    Call<Void> deletePost(@Path("userId") String userId, @Path("postId") String postId);

    @POST("/{userId}/post/{postId}/interest")
    Call<Void> markInterest(
            @Path("userId") String userId,
            @Path("postId") String postId
    );
    @POST("/{userId}/post/{postId}/uninterest")
    Call<Void> markunInterest(
            @Path("userId") String userId,
            @Path("postId") String postId
    );

}


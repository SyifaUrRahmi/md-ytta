package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PostsResponse {
    @SerializedName("posts")
    private List<Posts> posts;

    public List<Posts> getPosts() {
        return posts;
    }
}




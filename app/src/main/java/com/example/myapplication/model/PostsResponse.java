package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PostsResponse {
    @SerializedName("posts")
    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }
}




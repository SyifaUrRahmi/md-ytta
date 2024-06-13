package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("userId")
    private String userId;

    @SerializedName("profile_img")
    private String profileImage;

    @SerializedName("city")
    private String city;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("search_histories")
    private List<String> searchHistories;

    @SerializedName("interested_posts")
    private List<String> interestedPosts;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getSearchHistories() {
        return searchHistories;
    }

    public void setSearchHistories(List<String> searchHistories) {
        this.searchHistories = searchHistories;
    }

    public List<String> getInterestedPosts() {
        return interestedPosts;
    }

    public void setInterestedPosts(List<String> interestedPosts) {
        this.interestedPosts = interestedPosts;
    }
}


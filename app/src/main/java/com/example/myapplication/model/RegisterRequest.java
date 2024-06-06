package com.example.myapplication.model;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String city;

    public RegisterRequest(String username, String email, String password, String confirmPassword, String city) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.city = city;
    }

}


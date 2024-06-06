package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText cityEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.et_username);
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        cityEditText = findViewById(R.id.et_city);
        registerButton = findViewById(R.id.btn_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();

        if (password.equals(confirmPassword)) {
            RegisterRequest registerRequest = new RegisterRequest(username, email, password, confirmPassword, city);

            APIService apiService = APIClient.getClient().create(APIService.class);
            Call<RegisterResponse> call = apiService.registerUser(registerRequest);

            call.enqueue(new Callback<RegisterResponse>() {


                @Override
                public void onResponse(Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {
                    if (response.isSuccessful()) {
                        RegisterResponse registerResponse = response.body();
                        if (registerResponse.isSuccess()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + registerResponse.getError(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
    }
}
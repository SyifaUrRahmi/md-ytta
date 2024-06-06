package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    EditText tv_username, tv_city, tv_email, tv_pass;
    Button btn_regis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_username = findViewById(R.id.tv_username);
        tv_city = findViewById(R.id.tv_city);
        tv_email = findViewById(R.id.tv_email);
        tv_pass = findViewById(R.id.tv_password);
        btn_regis = findViewById(R.id.btn_register);


        btn_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = tv_pass.getText().toString();
                String email = tv_email.getText().toString();
                String city = tv_city.getText().toString();
                String username = tv_username.getText().toString();

                // Buat request POST ke API untuk register
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                String url = "https://barterbuddy-api-bed6dth5aq-et.a.run.app/register";
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("username", username);
                    jsonBody.put("password", password);
                    jsonBody.put("email", email);
                    jsonBody.put("city", city);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("tes" + e.toString());
                    System.out.println("bbb"+ e.getMessage());
                }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Proses respons dari API
                        try {
                            if (response.getBoolean("success")) {
                                // Register berhasil, redirect ke halaman LoginActivity
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Register gagal, tunjukkan pesan error
                                Toast.makeText(Register.this, "Register failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("tes" + e.toString());
                            System.out.println("aaaa"+ e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Toast.makeText(Register.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(jsonObjectRequest);
            }
        });
    }

    public void regis(View view) {

    }
}
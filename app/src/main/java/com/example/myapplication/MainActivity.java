package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.fragment.AddFragment;
import com.example.myapplication.fragment.ChatsFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_explore, btn_add, btn_chats, btn_profile;
    private TextView tv_explore, tv_add, tv_chats, tv_profile;
    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private ChatsFragment chatsFragment;
    private ProfileFragment profileFragment;
    private int defaultTextColor;
    private int selectedTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isLoggedIn()) {
            navigateToLogin();
        }
        btn_explore = findViewById(R.id.btn_explore);
        btn_add = findViewById(R.id.btn_add);
        btn_chats = findViewById(R.id.btn_chats);
        btn_profile = findViewById(R.id.btn_profile);

        tv_explore = findViewById(R.id.tv_explore);
        tv_add = findViewById(R.id.tv_add);
        tv_chats = findViewById(R.id.tv_chats);
        tv_profile = findViewById(R.id.tv_profile);


        homeFragment = new HomeFragment();
        addFragment = new AddFragment();
        chatsFragment = new ChatsFragment();
        profileFragment = new ProfileFragment();

        defaultTextColor = Color.BLACK;
        selectedTextColor = Color.parseColor("#B99DBFE3");

        setButtonState(btn_explore, tv_explore, true);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, homeFragment, HomeFragment.class.getSimpleName())
                .commit();

        btn_explore.setOnClickListener(view -> {
            resetButtonStates();
            setButtonState(btn_explore, tv_explore, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        });
        btn_add.setOnClickListener(view -> {
            resetButtonStates();
            setButtonState(btn_add, tv_add, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addFragment).commit();
        });

        btn_chats.setOnClickListener(view -> {
            resetButtonStates();
            setButtonState(btn_chats, tv_chats, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, chatsFragment).commit();
        });

        btn_profile.setOnClickListener(view -> {
            resetButtonStates();
            setButtonState(btn_profile, tv_profile, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
        });
    }

    private void setButtonState(ImageButton button, TextView textView, boolean isSelected) {
        int backgroundDrawable = isSelected ? R.drawable.bg_button_click : R.drawable.bg_button_default;
        int textColor = isSelected ? selectedTextColor : defaultTextColor;

        button.setBackgroundResource(backgroundDrawable);
        textView.setTextColor(textColor);
    }

    private void resetButtonStates() {
        setButtonState(btn_explore, tv_explore, false);
        setButtonState(btn_add, tv_add, false);
        setButtonState(btn_chats, tv_chats, false);
        setButtonState(btn_profile, tv_profile, false);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("MainActivity", "Token: " + token); // Debugging: cek nilai token
        return token != null;
    }
}
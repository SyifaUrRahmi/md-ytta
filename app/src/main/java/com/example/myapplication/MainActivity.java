package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.fragment.AddFragment;
import com.example.myapplication.fragment.ChatsFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.ProfileFragment;
import com.example.myapplication.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_explore, btn_add, btn_chats, btn_profile, btn_search;
    private TextView tv_explore, tv_add, tv_chats, tv_profile, tv_search;
    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private ChatsFragment chatsFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;
    private int defaultTextColor;
    private int selectedTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        if (!isLoggedIn()) {
            navigateToLogin();
        }
        btn_explore = findViewById(R.id.btn_explore);
        btn_search = findViewById(R.id.btn_search);
        btn_add = findViewById(R.id.btn_add);
        btn_chats = findViewById(R.id.btn_chats);
        btn_profile = findViewById(R.id.btn_profile);

        tv_explore = findViewById(R.id.tv_explore);
        tv_search = findViewById(R.id.tv_search);
        tv_add = findViewById(R.id.tv_add);
        tv_chats = findViewById(R.id.tv_chats);
        tv_profile = findViewById(R.id.tv_profile);

        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        addFragment = new AddFragment();
        chatsFragment = new ChatsFragment();
        profileFragment = new ProfileFragment();

        defaultTextColor = Color.BLACK;
        selectedTextColor = Color.parseColor("#195FBA");

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
        btn_search.setOnClickListener(view -> {
            resetButtonStates();
            setButtonState(btn_search, tv_search, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
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
        int textColor = isSelected ? selectedTextColor : defaultTextColor;
        int backgroundDrawable = isSelected ? R.drawable.bg_button_click : R.drawable.bg_button_default;
        int textStyle = isSelected ? Typeface.BOLD : Typeface.NORMAL;

        button.setBackgroundResource(backgroundDrawable);
        textView.setTextColor(textColor);
        textView.setTypeface(null, textStyle);
    }

    private void resetButtonStates() {
        setButtonState(btn_explore, tv_explore, false);
        setButtonState(btn_add, tv_add, false);
        setButtonState(btn_chats, tv_chats, false);
        setButtonState(btn_profile, tv_profile, false);
        setButtonState(btn_search, tv_search, false);
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
package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.LoginActivity; // Pastikan untuk mengimpor aktivitas login Anda
import com.example.myapplication.fragment.profile_fragment.InterestsFragment;
import com.example.myapplication.fragment.profile_fragment.PostsFragment;
import com.example.myapplication.fragment.profile_fragment.TransactionsFragment;
import com.google.firebase.auth.FirebaseAuth;
public class ProfileFragment extends Fragment {

    LinearLayout layout_posts, layout_interests, layout_transactions;

    PostsFragment postsFragment;
    InterestsFragment interestsFragment;
    TransactionsFragment transactionsFragment;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        layout_posts = view.findViewById(R.id.layout_posts);
        layout_interests = view.findViewById(R.id.layout_interests);
        layout_transactions = view.findViewById(R.id.layout_transactions);

        postsFragment = new PostsFragment();
        interestsFragment = new InterestsFragment();
        transactionsFragment = new TransactionsFragment();

        setButtonState(layout_posts, true);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, postsFragment)
                .commit();

        layout_posts.setOnClickListener(view1 -> {
            resetButtonStates();
            setButtonState(layout_posts, true);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, postsFragment).commit();
        });
        layout_interests.setOnClickListener(view1 -> {
            resetButtonStates();
            setButtonState(layout_interests, true);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, interestsFragment).commit();
        });
        layout_transactions.setOnClickListener(view1 -> {
            resetButtonStates();
            setButtonState(layout_transactions, true);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, transactionsFragment).commit();
        });

        ImageView lg = view.findViewById(R.id.logout); // Pastikan ID ini sesuai dengan layout Anda
        lg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;

    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class); // Ganti dengan aktivitas login Anda
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setButtonState(LinearLayout ly, boolean isSelected) {
        int backgroundDrawable = isSelected ? R.drawable.bg_profile_click : R.drawable.bg_button_default;
        ly.setBackgroundResource(backgroundDrawable);
    }

    private void resetButtonStates() {
        setButtonState(layout_posts, false);
        setButtonState(layout_interests, false);
        setButtonState(layout_transactions, false);
    }
}

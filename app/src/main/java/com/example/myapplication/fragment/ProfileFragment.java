package com.example.myapplication.fragment;

import static com.example.myapplication.R.id.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.EditProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.LoginActivity; // Pastikan untuk mengimpor aktivitas login Anda
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.fragment.profile_fragment.InterestsFragment;
import com.example.myapplication.fragment.profile_fragment.PostsFragment;
import com.example.myapplication.fragment.profile_fragment.TransactionsFragment;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    LinearLayout layout_posts, layout_interests, layout_transactions;

    PostsFragment postsFragment;
    InterestsFragment interestsFragment;
    TransactionsFragment transactionsFragment;

    ImageView iv_profile, popup_menu;
    TextView tv_username, tv_city, tv_email;

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

        iv_profile = view.findViewById(R.id.iv_user_profile);
        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        tv_city = view.findViewById(R.id.tv_city);
        popup_menu = view.findViewById(R.id.popup_menu);
        
        fetchProfile();
        
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

        popup_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        return view;

    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_edit_profile) {
                    Intent toEditProfile = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(toEditProfile);
                    return true;
                } else if (id == R.id.action_logout) {
                    showLogoutConfirmation();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.baseline_arrow_forward_24)
                .show();
    }

    private void fetchProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        APIService apiService = APIClient.getClient().create(APIService.class);

        Call<User> call = apiService.getUser(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tv_username.setText(user.getUsername());
                    tv_email.setText(user.getEmail());
                    tv_city.setText(user.getCity());

                    if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                        Glide.with(getActivity())
                                .load(user.getProfileImage())
                                .into(iv_profile);
                    } else {
                        iv_profile.setImageResource(R.drawable.baseline_account_circle_24);
                    }


                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
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

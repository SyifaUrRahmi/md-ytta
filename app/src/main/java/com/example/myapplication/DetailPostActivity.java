package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.model.Post;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPostActivity extends AppCompatActivity {
    private static final int EDIT_POST_REQUEST_CODE = 1001;
    private ImageView postImageView;
    private ImageButton btnClose;
    private TextView titleTextView, dateTextView, descriptionTextView, statusTextView, typeTextView;
    private Button btnDeal, btnEdit, btnDelete;
    private LinearLayout button;
    private String postId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_post);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        postImageView = findViewById(R.id.iv_post_image);
        titleTextView = findViewById(R.id.tv_detail_title);
        dateTextView = findViewById(R.id.tv_detail_date);
        descriptionTextView = findViewById(R.id.tv_detail_description);
        statusTextView = findViewById(R.id.tv_detail_status);
        typeTextView = findViewById(R.id.tv_detail_type);
        btnDeal = findViewById(R.id.btn_deal);
        btnDelete = findViewById(R.id.btn_delete);
        btnEdit = findViewById(R.id.btn_edit);
        button = findViewById(R.id.btn_update);
        btnClose = findViewById(R.id.btn_close);

        postId = getIntent().getStringExtra("postId");
        userId = getIntent().getStringExtra("userId");
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("uid", uId);
        Log.d("userId", userId);

        if (userId.equals(uId)){
            button.setVisibility(View.VISIBLE);
            btnDeal.setVisibility(View.GONE);
        }else {
            button.setVisibility(View.GONE);
            btnDeal.setVisibility(View.VISIBLE);
        }

        if (postId != null) {
            fetchPostDetails(postId);
        } else {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPostActivity.this, EditPostActivity.class);
                intent.putExtra("postId", postId);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, EDIT_POST_REQUEST_CODE);
            }
        });

        btnDelete.setOnClickListener(view -> {
            showDeleteConfirmationDialog(userId, postId);
        });

        btnClose.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_POST_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Lakukan refresh tampilan post
            String updatedPostId = data.getStringExtra("updatedPostId");
            if (updatedPostId != null && updatedPostId.equals(postId)) {
                // Jika post yang diperbarui adalah post yang sedang ditampilkan
                fetchPostDetails(postId); // Perbarui detail post
            }
        }
    }

    private void fetchPostDetails(String postId) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<Post> call = apiService.getPost(postId);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Post post = response.body();
                    displayPostDetails(post);
                } else {
                    Toast.makeText(DetailPostActivity.this, "Failed to load post details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(DetailPostActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPostDetails(Post post) {
        titleTextView.setText(post.getTitle());
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        try {
            dateTextView.setText(outputFormat.format(inputFormat.parse(post.getUpdatedAt())));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        descriptionTextView.setText(post.getDescription());
        statusTextView.setText(post.getStatus());
        typeTextView.setText(post.getType());

        Glide.with(this)
                .load(post.getImage())
                .into(postImageView);
    }

    private void showDeleteConfirmationDialog(String userId, String postId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(userId, postId);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.baseline_delete_forever_24)
                .show();
    }

    private void deletePost(String userId, String postId) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<Void> call = apiService.deletePost(userId, postId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailPostActivity.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailPostActivity.this, MainActivity.class);
                    intent.putExtra("fragment_to_load", "home");
                    startActivity(intent);
                    new Handler().postDelayed(() -> finish(), 100);
                } else {
                    Toast.makeText(DetailPostActivity.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DetailPostActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

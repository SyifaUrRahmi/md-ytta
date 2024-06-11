package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.example.myapplication.model.Post;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPostActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText;
    private ImageView selectImageView;
    private Spinner status, type;
    private Button saveButton, cancelButton;
    private String postId, userId;
    private Bitmap bitmap;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleEditText = findViewById(R.id.et_title);
        descriptionEditText = findViewById(R.id.et_description);
        selectImageView = findViewById(R.id.selectImage);
        status = findViewById(R.id.status);
        type = findViewById(R.id.type);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);

        postId = getIntent().getStringExtra("postId");
        userId = getIntent().getStringExtra("userId");

        if (postId != null) {
            fetchPostDetails(postId);
        } else {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            selectImageView.setImageURI(selectedImageUri);
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                imageUri = selectedImageUri;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        selectImageView.setOnClickListener(v -> openFileChooser());

        saveButton.setOnClickListener(v -> updatePost());

        cancelButton.setOnClickListener(v -> finish());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
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
                    Toast.makeText(EditPostActivity.this, "Failed to load post details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPostDetails(Post post) {
        titleEditText.setText(post.getTitle());
        descriptionEditText.setText(post.getDescription());
        status.setSelection(getStatusIndex(post.getStatus()));
        type.setSelection(getStatusIndex(post.getType()));

        Glide.with(this)
                .load(post.getImage())
                .into(selectImageView);
    }

    private int getStatusIndex(String status) {
        String[] statuses = getResources().getStringArray(R.array.status_item);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                return i;
            }
        }
        return 0; // Default to first item if not found
    }
    private int getTypesIndex(String type) {
        String[] types = getResources().getStringArray(R.array.type_item);
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(type)) {
                return i;
            }
        }
        return 0; // Default to first item if not found
    }

    private void updatePost() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String sts = status.getSelectedItem().toString();
        String tipe = type.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(sts)|| TextUtils.isEmpty(tipe)) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = null;
        if (bitmap != null) {
            file = new File(getCacheDir(), "image.jpg");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        RequestBody titleRequest = RequestBody.create(MediaType.parse("multipart/form-data"), title);
        RequestBody descriptionRequest = RequestBody.create(MediaType.parse("multipart/form-data"), description);
        RequestBody statusRequest = RequestBody.create(MediaType.parse("multipart/form-data"), sts);
        RequestBody typeRequest = RequestBody.create(MediaType.parse("multipart/form-data"), tipe);
        MultipartBody.Part imagePart = null;

        if (file != null) {
            RequestBody imageRequest = RequestBody.create(MediaType.parse("image/jpeg"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), imageRequest);
        }

        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<ResponseBody> call = apiService.editPost(userId, postId, titleRequest, descriptionRequest, statusRequest, typeRequest, imagePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditPostActivity.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedPostId", postId); // Kirim id post yang diperbarui
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(EditPostActivity.this, "Failed to update post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

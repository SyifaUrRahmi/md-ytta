package com.example.myapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.api.APIClient;
import com.example.myapplication.api.APIService;
import com.google.firebase.auth.FirebaseAuth;

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

public class AddFragment extends Fragment {


    private EditText etTitle, etDescription;
    private Spinner status, type;
    private Button btnPost;
    private ImageView selectImage;
    private Bitmap bitmap;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectImage.setImageURI(imageUri);
                            ViewGroup.LayoutParams params = selectImage.getLayoutParams();
                            params.height = convertDpToPixel(300);
                            selectImage.setLayoutParams(params);
                            selectImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        status = view.findViewById(R.id.status);
        type = view.findViewById(R.id.type);
        selectImage = view.findViewById(R.id.selectImage);
        btnPost = view.findViewById(R.id.btn_post);

        if (bitmap != null){
            selectImage.setImageBitmap(bitmap);
        }else {
            selectImage.setImageResource(R.drawable.baseline_image_24); // Ubah sesuai dengan gambar default di aplikasi Anda

        }

        selectImage.setOnClickListener(v -> openFileChooser());
        btnPost.setOnClickListener(v -> uploadPost());



        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void resetData() {
        etTitle.setText("");
        etDescription.setText("");
        status.setSelection(0);
        type.setSelection(0);
        resetImage();
    }

    private void resetImage() {
        selectImage.setImageResource(R.drawable.baseline_image_24);
        bitmap = null;
        imageUri = null;
    }


    private void uploadPost() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String sts = status.getSelectedItem().toString();
        String tipe = type.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || sts.isEmpty() || tipe.isEmpty() || bitmap == null ) {
            Toast.makeText(getActivity(), "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getActivity().getCacheDir(), "image.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("multipart/form-data"), title);
        RequestBody requestBodyDescription = RequestBody.create(MediaType.parse("multipart/form-data"), description);
        RequestBody requestBodyStatus = RequestBody.create(MediaType.parse("multipart/form-data"), sts);
        RequestBody requestBodyType = RequestBody.create(MediaType.parse("multipart/form-data"), tipe);
        RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestBodyImage);

        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<ResponseBody> call = apiService.uploadPost(userId, requestBodyTitle, requestBodyDescription, requestBodyStatus, requestBodyType, imagePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                    resetData();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    ((MainActivity) requireActivity()).setButtonState(((MainActivity) requireActivity()).btn_explore, ((MainActivity) requireActivity()).tv_explore, true);
                    ((MainActivity) requireActivity()).setButtonState(((MainActivity) requireActivity()).btn_add, ((MainActivity) requireActivity()).tv_add, false);

                } else {
                    Toast.makeText(getActivity(), "Failed to upload post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Failed to upload post", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}

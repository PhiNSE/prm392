package com.phinse.prm392.ui.upload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phinse.prm392.databinding.ActivityUploadBinding;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityUploadBinding binding;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private StorageReference imageRef = storageRef.child("images/");
    private StorageReference fileRef = storageRef.child("files/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnChooseImage.setOnClickListener(v -> {
            //choose image from gallery
            chooseImage();
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            //get image uri
            binding.ivUpload.setImageURI(data.getData());
            //upload image to firebase
            uploadImage(data.getData());
        }
    }

    private void uploadImage(Uri data) {
        String fileExtension = data.getLastPathSegment();
        String imageName = System.currentTimeMillis() + "." + fileExtension;
        Log.d(TAG, "uploadImage: " + imageName);
        StorageReference image = imageRef.child(imageName);
        setLoading(true);
        image.putFile(data)
                .addOnSuccessListener(taskSnapshot -> {
                    setLoading(false);
                    image.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(this).load(uri).into(binding.ivUpload);
                    });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(Boolean isLoading) {
        if (isLoading) {
            binding.btnChooseImage.setEnabled(false);
            binding.tvProgress.setText("Uploading...");
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnChooseImage.setEnabled(true);
            binding.tvProgress.setText("Uploaded successfully!");
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}
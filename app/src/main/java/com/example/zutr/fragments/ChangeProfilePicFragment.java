package com.example.zutr.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * Short Fragment that pops up to Launch the Camera,
 * save the picture to the current FirebaseUser's
 * profile picture, and returns to  the profile
 * fragment upon success
 **/


public class ChangeProfilePicFragment extends Fragment {


    private static final String TAG = "ChangeProfilePicFrag";
    public static final String LOADING = "Loading...";
    public static final String CAMERA_CHOICE = "Take Picture";
    private static final int PICK_IMAGE_CODE = 10000;
    private static final int PERMISSION_CODE = 3444;


    private Button btnCaptureImage;
    private Button btnGalleryImage;
    protected ImageView ivPostImage;
    protected ProgressBar pbLoading;


    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;


    public static ChangeProfilePicFragment newInstance() {
        ChangeProfilePicFragment fragment = new ChangeProfilePicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Back button will go to main activity
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_profile_pic, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        btnGalleryImage = view.findViewById(R.id.btnGallery);

        ivPostImage = view.findViewById(R.id.ivProfilePicture);
        pbLoading = view.findViewById(R.id.pbLoading);

        pbLoading.setVisibility(View.INVISIBLE);

        btnCaptureImage.setText(CAMERA_CHOICE);
        btnGalleryImage.setVisibility(View.VISIBLE);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCaptureImage.setText(LOADING);
                launchCamera();
            }
        });

        btnGalleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        // permission hasn't been granted so need to request
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        // permission already granted
                        pbLoading.setVisibility(View.VISIBLE);
                        pickImageFromGallery();
                    }
                } else {
                    // system os is less than marshmallow
                    pbLoading.setVisibility(View.VISIBLE);
                    pickImageFromGallery();
                }


                btnGalleryImage.setVisibility(View.GONE);
            }
        });


    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pbLoading.setVisibility(View.VISIBLE);
                    pickImageFromGallery();
                } else {
                    Toast.makeText(getContext(), "Gallery access was denied!", Toast.LENGTH_SHORT).show();

                }
        }
    }

    private void pictureBeingLoaded() {
        pbLoading.setVisibility(View.VISIBLE);
        btnGalleryImage.setVisibility(View.GONE);
        btnCaptureImage.setText(LOADING);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        pictureBeingLoaded();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivPostImage.setImageBitmap(bitmap);
                handleUpload(bitmap);
            } else {
                Toast.makeText(getContext(), "Photo wasn't taken!", Toast.LENGTH_SHORT).show();
                pbLoading.setVisibility(View.GONE);
                startUserProfileFragment();
            }
        }

        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    handleUpload(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "No photo selected!", Toast.LENGTH_SHORT).show();
                pbLoading.setVisibility(View.GONE);
                startUserProfileFragment();
            }
        }
    }


    //Begin Upload proccess from bitmap returned from camera intent
    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("ProfileImages")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }

    //Get Uri to change Profile Image
    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: " + uri);
                        setUserProfileUrl(uri);
                    }
                });
    }

    //update firebaseuser profile img based off Uri
    private void setUserProfileUrl(Uri uri) {

        final String collectionPath = LogInActivity.IS_TUTOR ? Tutor.PATH : Student.PATH;

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        FirebaseAuth.getInstance().getCurrentUser().updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Updated succesfully", Toast.LENGTH_SHORT).show();

                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        database.collection(collectionPath).document(user.getUid()).update("profileUrl", user.getPhotoUrl().toString());

                        pbLoading.setVisibility(View.INVISIBLE);
                        startUserProfileFragment();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile image failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // To go back to the User Profile with the Uodated Profile Picture
    public void startUserProfileFragment() {

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, profileFragment);
        fragmentTransaction.commit();

    }
}
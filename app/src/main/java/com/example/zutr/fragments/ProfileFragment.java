package com.example.zutr.fragments;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.LogInActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";


    private ImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvFullName;
    private TextView tvSubjects;
    private Button btnSignUp;


    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private FirebaseAuth mAuth;



    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final String path = (LogInActivity.IS_TUTOR ? Tutor.PATH : Student.PATH);

        Log.i(TAG, "onViewCreated: " + path);
        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.i(TAG, "onViewCreated: user " + mAuth.getCurrentUser());
        ivProfilePic = view.findViewById(R.id.ivProfilePicture);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvSubjects = view.findViewById(R.id.tvSubjects);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnSignUp = view.findViewById(R.id.btnSignOut);




        database.collection(path).document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    tvUsername.setText(String.format("@%s", documentSnapshot.get(User.KEY_USERNAME)));
                    tvFullName.setText(String.format("%s  %s", documentSnapshot.get(User.KEY_FIRSTNAME), documentSnapshot.get(User.KEY_LASTNAME)));
                }

            }
        });


        //Load ivProfilePic with user's image uri

        if (user.getPhotoUrl() != null) {
            Glide.with(getContext()).load(user.getPhotoUrl()).into(ivProfilePic);
        }


        //Set the picture to be clickable to the edit fragment
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUserProfileFragment();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), LogInActivity.class);
                startActivity(intent);

            }
        });

    }

    public void startUserProfileFragment() {

        ChangeProfilePicFragment changeUserPicFragment = new ChangeProfilePicFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, changeUserPicFragment);
        fragmentTransaction.commit();

    }
}
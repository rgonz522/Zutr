package com.example.zutr.fragments;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";


    private ImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvFullName;
    private TextView tvSubjects;


    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private FirebaseUser user;

    public ProfileFragment() {
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        user = MainActivity.CurrentUser;

        ivProfilePic = view.findViewById(R.id.ivProfilePicture);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvSubjects = view.findViewById(R.id.tvSubjects);
        tvUsername = view.findViewById(R.id.tvUsername);


        database.collection(Student.PATH).whereEqualTo(Student.KEY_EMAIL, MainActivity.CurrentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        Log.i(TAG, "onComplete: " + documentSnapshot.get(Student.KEY_FIRSTNAME));
                        tvUsername.setText(String.format("@%s", documentSnapshot.get(Student.KEY_USERNAME)));

                        tvFullName.setText(String.format("%s  %s", documentSnapshot.get(Student.KEY_FIRSTNAME), documentSnapshot.get(Student.KEY_LASTNAME)));
                    }

                }

            }
        });

        //Load ivProfilePic with user's image uri
        String photo_url = user.getPhotoUrl().toString();
        if (photo_url != null) {
            Glide.with(getContext()).load(photo_url).into(ivProfilePic);
        }


        //Set the picture to be clickable to the edit fragment
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUserProfileFragment();
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
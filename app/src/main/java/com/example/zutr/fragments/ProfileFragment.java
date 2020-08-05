package com.example.zutr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";


    private ImageView ivProfilePic;
    private ImageView ivEdit;
    private TextView tvUsername;
    private TextView tvFullName;
    private TextView tvSubjects;
    private RatingBar rbZutrRate;
    private Button btnSignOut;


    private FirebaseFirestore database;
    private FirebaseStorage storage;


    private boolean isCurrentUser;
    private String userID;
    private String path;

    public ProfileFragment() {
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        isCurrentUser = true;
        path = LogInActivity.IS_TUTOR ? Tutor.PATH : Student.PATH;

    }

    public ProfileFragment(String userid, String path) {

        this.userID = userid;
        this.path = path;

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userid.equals(currentUserID)) {
            isCurrentUser = true;

        }

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


        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        ivProfilePic = view.findViewById(R.id.ivProfilePicture);
        ivEdit = view.findViewById(R.id.ivEdit);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        rbZutrRate = view.findViewById(R.id.rbZutrRate);
        btnSignOut = view.findViewById(R.id.btnSignOut);


        rbZutrRate.setIsIndicator(true);

        database.collection(path).document(userID).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();


                tvUsername.setText(String.format("@%s", documentSnapshot.get(User.KEY_USERNAME)));
                Log.i(TAG, "onComplete: " + tvUsername.getText());
                tvFullName.setText(String.format("%s  %s"
                        , documentSnapshot.get(User.KEY_FIRSTNAME)
                        , documentSnapshot.get(User.KEY_LASTNAME)));

                //Load ivProfilePic with user's image uri

                String imageURL = documentSnapshot.getString(User.KEY_IMAGE);
                if (imageURL != null) {
                    Glide.with(getContext()).load(imageURL).circleCrop().into(ivProfilePic);
                }

                Double rate = documentSnapshot.getDouble(Tutor.RATING);

                if (rate != null) {
                    rbZutrRate.setRating(rate.floatValue());
                    rbZutrRate.setVisibility(View.VISIBLE);
                } else {
                    rbZutrRate.setVisibility(View.GONE);
                }
            }

        });


        if (isCurrentUser) {


            setUpPosts();

            //Set the picture to be clickable to the edit fragment
            ivProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startUserProfileFragment();
                }
            });

            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), LogInActivity.class);
                    startActivity(intent);

                }
            });

        } else {
            btnSignOut.setVisibility(View.GONE);
            ivEdit.setVisibility(View.GONE);
        }

    }

    public void startUserProfileFragment() {

        ChangeProfilePicFragment changeUserPicFragment = new ChangeProfilePicFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, changeUserPicFragment);
        fragmentTransaction.commit();

    }


    private void setUpPosts() {


        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.flprevious, homeFragment);
        fragmentTransaction.commit();


    }
}
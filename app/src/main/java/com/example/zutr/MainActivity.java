package com.example.zutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import android.view.MenuItem;

import com.example.zutr.fragments.ChatsFragment;
import com.example.zutr.fragments.MessagesFragment;
import com.example.zutr.fragments.GetZutrFragment;
import com.example.zutr.fragments.HomeFragment;
import com.example.zutr.fragments.OpenSessionsFragment;
import com.example.zutr.fragments.ProfileFragment;



import com.example.zutr.user_auth.LogInActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";


    private BottomNavigationView bottomNavigationView;

    FragmentManager fragmentManager;


    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
    private FirebaseFirestore dataBase;

    public MainActivity() {
        dataBase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_navigation);


        fragmentManager = getSupportFragmentManager();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_profile:

                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_compose:

                        fragment = LogInActivity.IS_TUTOR ? new OpenSessionsFragment() : new GetZutrFragment();
                        break;
                    case R.id.action_home:

                        fragment = new HomeFragment();
                        break;
                    case R.id.action_history:

                        fragment = new ChatsFragment();
                        break;
                    default:
                        fragment = null;
                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_right, R.anim.enter_from_left, R.anim.exit_from_left);
                transaction.replace(R.id.flContainer, fragment);
                transaction.commit();


                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);


    }



}
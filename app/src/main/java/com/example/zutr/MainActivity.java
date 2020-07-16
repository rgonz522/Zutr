package com.example.zutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import com.example.zutr.fragments.GetZutrFragment;
import com.example.zutr.fragments.HistoryFragment;
import com.example.zutr.fragments.HomeFragment;
import com.example.zutr.fragments.OpenSessionsFragment;
import com.example.zutr.fragments.ProfileFragment;

import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static boolean IS_TUTOR;

    private BottomNavigationView bottomNavigationView;

    FragmentManager fragmentManager;


    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
    private FirebaseFirestore dataBase;

    public MainActivity() {
        dataBase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        isTutor();

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

                        fragment = IS_TUTOR ? new OpenSessionsFragment() : new GetZutrFragment();
                        break;
                    case R.id.action_home:

                        fragment = new HomeFragment();
                        break;
                    case R.id.action_history:

                        fragment = new HistoryFragment();
                        break;
                    default:
                        fragment = null;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);


    }

    public boolean isTutor() {

        dataBase.collection(Tutor.PATH).document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.get(User.KEY_EMAIL) != null) {
                    IS_TUTOR = true;
                    Log.i(TAG, "onSuccess: " + documentSnapshot.get(User.KEY_EMAIL));
                    Log.i(TAG, "onSuccess: " + currentUser.getUid());
                } else {
                    IS_TUTOR = false;
                    Log.i(TAG, "onFailure: " + documentSnapshot.get(User.KEY_FIRSTNAME));
                }

            }
        });
        return IS_TUTOR;
    }

}
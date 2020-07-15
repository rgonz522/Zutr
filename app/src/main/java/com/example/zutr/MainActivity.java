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

import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
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


    private FirebaseUser CurrentUser;

    private FirebaseAuth mAuth;
    private FirebaseFirestore DataBase;

    public MainActivity() {
        DataBase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
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

                        fragment = new ProfileFragment(true);
                        break;
                    case R.id.action_compose:

                        fragment = new GetZutrFragment();

                        Log.i(TAG, "onNavigationItemSelected: " + IS_TUTOR);

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

        DataBase.collection(Tutor.PATH).document(CurrentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                IS_TUTOR = true;
            }
        });
        return IS_TUTOR;
    }

    }
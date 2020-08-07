package com.example.zutr;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.zutr.fragments.GetZutrFragment;
import com.example.zutr.fragments.HomeFragment;
import com.example.zutr.fragments.OpenSessionsFragment;
import com.example.zutr.fragments.SuggestionFragment;
import com.example.zutr.user_auth.LogInActivity;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final int PROFILE_INDEX = 2;
    public static final int HOME_INDEX = 1;
    public static final int SUGGESTION_INDEX = 0;

    private BubbleNavigationLinearView bubbleNavigation;

    private BubbleToggleView bubbleToggleView;
    private BubbleToggleView bubbleToggleView2;

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


        bubbleNavigation = findViewById(R.id.top_navigation_constraint);
        bubbleToggleView = findViewById(R.id.c_item_rest);
        bubbleToggleView2 = findViewById(R.id.c_item_rest_2);

        bubbleNavigation.setCurrentActiveItem(PROFILE_INDEX);


        fragmentManager = getSupportFragmentManager();


        bubbleNavigation.setNavigationChangeListener((view, position) -> {
            startFragment(position);
        });


        startFragment(HOME_INDEX);

    }


    private void startFragment(int position) {

        bubbleNavigation.setCurrentActiveItem(position);


        Log.i(TAG, "startFragment: " + bubbleNavigation.getCurrentActiveItemPosition());
        final Fragment fragment;
        switch (position) {

            case PROFILE_INDEX:
                fragment = new HomeFragment();
                break;
            case HOME_INDEX:
                fragment = LogInActivity.IS_TUTOR ? new OpenSessionsFragment() : new GetZutrFragment();
                break;
            case SUGGESTION_INDEX:
                fragment = new SuggestionFragment();

                break;
            default:
                fragment = new HomeFragment();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_right, R.anim.enter_from_left, R.anim.exit_from_left);
        transaction.replace(R.id.flContainer, fragment);
        transaction.commit();
    }

}
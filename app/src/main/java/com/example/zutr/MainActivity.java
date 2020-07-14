package com.example.zutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.zutr.fragments.GetZutrFragment;
import com.example.zutr.fragments.HistoryFragment;
import com.example.zutr.fragments.HomeFragment;
import com.example.zutr.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.zutr.models.Student;

import com.example.zutr.user_auth.*;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";


    private BottomNavigationView bottomNavigationView;

    FragmentManager fragmentManager;


    public static FirebaseUser CurrentUser;

    private FirebaseAuth mAuth;

    public static FirebaseFirestore DataBase;

    public MainActivity() {
        DataBase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();

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

                        fragment = new GetZutrFragment();
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


    public void writeData(){


        Student tutor = new Student("rgonz522", "rebeca", "gonzalez",
                "ganahil@yahoo.com", "8430 SW  107th Ave");


        Log.i("button", "onClick: ");

        DataBase.collection("student").document().set(tutor);

    }

    public void getStudents(){
        // [START get_all_users]
        DataBase.collection("zutr")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("username"));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

   /* public void fetchQuote(View view) {

        database.collection("student").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                Student student =
                                        new Student((String) documentSnapshot.get(Student.KEY_USERNAME)
                                                ,(String) documentSnapshot.get(Student.KEY_FIRSTNAME)
                                                ,(String) documentSnapshot.get(Student.KEY_LASTNAME)
                                                ,(String) documentSnapshot.get(Student.KEY_EMAIL)
                                                ,(String) documentSnapshot.get(Student.KEY_ADDRESS));


                                Log.i(TAG, "onComplete: " + student.getFirst_name());
                                Log.i(TAG, "onComplete: " + student);
                                students.add(student);
                                Log.i(TAG, "onComplete: " +  students.get(count++).getAddress());
                            }
                        }
                        else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }




                        Log.i(TAG, "onClick: amt of students" + students.size());
                        for(Student student : students){
                            Log.i(TAG, "onClick: Student : " + student.getFirst_name());
                        }
                    }





        });




    }*/

    }
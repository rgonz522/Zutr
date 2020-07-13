package com.example.zutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Student;
import models.Tutor;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    FirebaseAnalytics analytics;
    Button button;
    EditText textView;
    List<Student> students;
    int count = 0;

    String name;
    DocumentReference mDocref = FirebaseFirestore.getInstance().document("student/5kf7FCWkyOFB3LPilN0Z");


    FirebaseFirestore database = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        students = new ArrayList<>();
        button = findViewById(R.id.button);
        textView = findViewById(R.id.tvName);

        analytics = FirebaseAnalytics.getInstance(this);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchQuote(view);


            }
        });

    }


    public void writeData(){


        Student tutor = new Student("rgonz522", "rebeca", "gonzalez",
                "ganahil@yahoo.com", "8430 SW  107th Ave");


        Log.i("button", "onClick: ");

        database.collection("student").document().set(tutor);

    }

    public void getStudents(){
        // [START get_all_users]
        database.collection("zutr")
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

    public void fetchQuote(View view) {

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




    }

    }
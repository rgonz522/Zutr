package com.example.zutr.user_auth;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    public static final String TAG = " LoginActivty";

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private Button btnSignZutr;


    private FirebaseAuth mAuth;
    private FirebaseFirestore database;



    public static boolean IS_TUTOR;

    private boolean tutorChecked;


    //overriding the back button so the signed out user
    // cannot go back to Main while Assync Firebase Loads
    @Override
    protected void onStart() {
        super.onStart();
        //Back button will not go to main activity
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        //using Firebase's email and pw auth.
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        Log.i(TAG, "onCreate: login right befoe mVidAuth instance");


        if (mAuth.getCurrentUser() != null) {

            isTutor();


        }
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignUp);
        btnSignZutr = findViewById(R.id.btnZutrSignUp);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO do a better filtering of user input with UI
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                authorizingUser(email, password);

            }
        });

        btnSignZutr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                intent.putExtra("Tutor", true);
                startActivity(intent);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }


    private void authorizingUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            isTutor();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    private void startMainActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

    }

    public boolean isTutor() {

        final FirebaseUser currentUser = mAuth.getCurrentUser();


        database.collection(Tutor.PATH).document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.get(User.KEY_EMAIL) != null) {
                    IS_TUTOR = true;
                } else {
                    IS_TUTOR = false;
                }
                startMainActivity();

            }
        });

        tutorChecked = true;


        return IS_TUTOR;
    }
}
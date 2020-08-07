package com.example.zutr.user_auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private User currentUser;


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
        setContentView(R.layout.activity_log_in_splash);


        //using Firebase's email and pw auth.
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();


        if (mAuth.getCurrentUser() != null) {

            isTutor();

            Log.i(TAG, "onCreate: already signed in");
        } else {
            setContentView(R.layout.activity_log_in);
            initializeUILogIn();
        }


    }

    private void initializeUILogIn() {
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
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        isTutor();

                        etEmail.setText("");
                        etPassword.setText("");

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        etPassword.setError("Incorrect Email or Password");
                        etPassword.requestFocus();
                        Toast.makeText(LogInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

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


        database.collection(Tutor.PATH).document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {

            IS_TUTOR = documentSnapshot.get(User.KEY_EMAIL) != null;
            startMainActivity();


        });


        return IS_TUTOR;
    }
}
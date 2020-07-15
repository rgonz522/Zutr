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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    public static final String TAG = " LoginActivty";

    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private Button btn_signup;
    private Button btn_sign_zutr;


    private FirebaseAuth mAuth;


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


        Log.i(TAG, "onCreate: Logged in:" + mAuth.getCurrentUser());
        if (mAuth.getCurrentUser() != null) {

            startMainActivity();

        }
        et_email = findViewById(R.id.etEmail);
        et_password = findViewById(R.id.etPassword);
        btn_login = findViewById(R.id.btnLogin);
        btn_signup = findViewById(R.id.btnSignUp);
        btn_sign_zutr = findViewById(R.id.btnZutrSignUp);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO do a better filtering of user input with UI
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                authorizingUser(email, password);

            }
        });

        btn_sign_zutr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                intent.putExtra("Tutor", true);
                startActivity(intent);
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
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
                            startMainActivity();

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

}
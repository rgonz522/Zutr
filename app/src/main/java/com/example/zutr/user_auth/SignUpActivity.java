package com.example.zutr.user_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpActivity extends AppCompatActivity {

    public static final String TUTOR_PATH = "zutr";
    public static final String STUDENT_PATH = "student";
    private static final String TAG = "SignUpActivity";



    private Button btnSignUp;
    private EditText etUserName;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPassword;
    private EditText etAddress;
    private EditText etEmail;

    private User user;

    FirebaseFirestore database;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final boolean tutor = getIntent().getBooleanExtra("Tutor", false);


        btnSignUp = findViewById(R.id.btnSignUp);
        etUserName = findViewById(R.id.etEmail);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPassword = findViewById(R.id.etPassword);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser(tutor);

            }
        });


    }

    private void registerUser(boolean tutor) {

        final String path = (tutor ? TUTOR_PATH : STUDENT_PATH);

        final String username = etUserName.getText().toString().trim();
        final String first_name = etFirstName.getText().toString().trim();
        final String last_name = etLastName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String address = etAddress.getText().toString().trim() + "";
        final String password = etPassword.getText().toString().trim();


        if (username.isEmpty()) {
            etUserName.setError(getString(R.string.input_error_username));
            etUserName.requestFocus();
            return;
        }

        if (first_name.isEmpty()) {
            etFirstName.setError(getString(R.string.input_error_name));
            etFirstName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.input_error_email));
            etEmail.requestFocus();
            return;
        }

        //if email is acceptable format ie: abc@email.com
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.input_error_email_invalid));
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.input_error_password));
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError(getString(R.string.input_error_password_length));
            etPassword.requestFocus();
            return;
        }

        if (last_name.isEmpty()) {
            etLastName.setError(getString(R.string.input_error_lastname));
            etLastName.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Log.i(TAG, "onComplete: created autho successfully" + task.getResult().getUser().getUid());
                            createNewUser(new Student(username, first_name, last_name, email, address), path, task.getResult().getUser().getUid());
                            startLoginActivity();


                        } else {
                            Toast.makeText(SignUpActivity.this, "An account with that email already exists", Toast.LENGTH_LONG).show();
                            etEmail.setError(getString(R.string.input_error_email_invalid));
                            etEmail.requestFocus();
                            Log.e(TAG, "onComplete: task failed ", task.getException());
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "An account with that email already exists", Toast.LENGTH_LONG);
                Log.e(TAG, "onComplete: task failed ", e);
            }
        });


    }


    public void createNewUser(User user, String path, String docID) {

        database.collection(path).document(docID).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        Log.i(TAG, "onSuccess: " + task.getResult());

                    }
                });


    }






    public void startLoginActivity() {

        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);

    }
}
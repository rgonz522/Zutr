package com.example.zutr.user_auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;


public class SignUpActivity extends AppCompatActivity {

    public static final String TUTOR_PATH = "zutr";
    public static final String STUDENT_PATH = "student";
    private static final String TAG = "SignUpActivity";
    public static final int RESULT_SIGN_IN_GOOGLE = 12231;


    private Button btnSignUp;
    private EditText etUserName;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPassword;
    private EditText etAddress;
    private EditText etEmail;
    private SignInButton signbtnGoogle;

    private User user;

    private boolean tutor;
    FirebaseFirestore database;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tutor = getIntent().getBooleanExtra("Tutor", false);


        FirebaseAuth.getInstance().signOut();
        btnSignUp = findViewById(R.id.btnSignUp);
        etUserName = findViewById(R.id.etEmail);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPassword = findViewById(R.id.etPassword);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
        signbtnGoogle = findViewById(R.id.sign_in_google);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();

            }
        });


        signbtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

    }

    private void registerUser() {

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


                            etEmail.setText("");
                            etPassword.setText("");
                            etAddress.setText("");
                            etFirstName.setText("");
                            etLastName.setText("");
                            etUserName.setText("");

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
                Toast.makeText(SignUpActivity.this, "An account with that email already exists", Toast.LENGTH_LONG).show();
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
                        startLoginActivity();
                    }
                });


    }


    public void startLoginActivity() {

        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);

    }


//    private void signIn() {
//
//        // Configure sign-in to request the user's ID, email address, and basic
//        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        // Build a GoogleSignInClient with the options specified by gso.
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent , RESULT_SIGN_IN_GOOGLE);
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: resultCode: " + resultCode);

        if (requestCode == RESULT_SIGN_IN_GOOGLE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: responseObj " + response.toString());

                if (response.isNewUser()) {
                    Log.i(TAG, "onActivityResult: New user created. About to add to firestore");

                    addUserToFireStore(response, data);
                } else {
                    //  getUserAndStoreInSharedPref
                }


            } else {
                if (response == null) {
                    return;
                }

                Log.e(TAG, "onActivityResult: Error Authenticating", response.getError().getCause());

            }
        }

    }


    private void addUserToFireStore(IdpResponse response, Intent data) {

        String path = tutor ? Tutor.PATH : Student.PATH;

        // Assigning fields of the user object


        String email = response.getEmail();
        String authUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String username = "changeMe!";

        // public Student(String username, String first_name, String last_name, String email, String address) {
        User user = new Student(username, name, " ", email, "");


    }

    // Kick start sign in
    private void signInUser() {

        List<AuthUI.IdpConfig> PROVIDERS = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build()
        );


        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(PROVIDERS)
                        .enableAnonymousUsersAutoUpgrade()
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.logomakr_1z0hro)
                        .build(),
                RESULT_SIGN_IN_GOOGLE);
    }


}
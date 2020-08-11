package com.example.zutr.user_auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class LogInActivity extends AppCompatActivity {

    public static final String TAG = " LoginActivty";
    public static final int RESULT_SIGN_IN_GOOGLE = 12231;

    private boolean tutor;

    private Button btnLogin;
    private Button btnSignup;
    private Button btnSignZutr;


    private ProgressBar pbload;
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

            startUILogIn();
        }

    }

    private void startUILogIn() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignUp);
        btnSignZutr = findViewById(R.id.btnZutrSignUp);
        pbload = findViewById(R.id.pbLoading);


        pbload.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(view -> {

            pbload.setVisibility(View.VISIBLE);

            signInUser();


        });

        btnSignZutr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutor = true;
                signInUser();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutor = false;
                signInUser();
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


            Log.i(TAG, "isTutor: ");
            IS_TUTOR = documentSnapshot.get(User.KEY_EMAIL) != null;
            startMainActivity();

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });


        return IS_TUTOR;
    }


    public void createNewUser(User user, String path, String docID) {

        database.collection(path).document(docID).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        Log.i(TAG, "onSuccess: " + task.getResult());
                        isTutor();
                    }
                });


    }


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

                    isTutor();

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
        setUserProfileUrl(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());

        String email = response.getEmail();
        String authUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String username = "changeMe!";

        // public Student(String username, String first_name, String last_name, String email, String address) {
        User user = new Student(username, name, " ", email, "");


        createNewUser(user, path, authUserID);


    }

    // Kick start sign in
    private void signInUser() {


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        List<AuthUI.IdpConfig> PROVIDERS = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().setSignInOptions(gso).build(),
                new AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build()
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


    //update firebaseuser profile img based off Uri
    private void setUserProfileUrl(Uri uri) {

        final String collectionPath = LogInActivity.IS_TUTOR ? Tutor.PATH : Student.PATH;

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        FirebaseAuth.getInstance().getCurrentUser().updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LogInActivity.this, "Updated succesfully", Toast.LENGTH_SHORT).show();

                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        Uri photo = user.getPhotoUrl();
                        if (photo != null) {
                            database.collection(collectionPath).document(user.getUid()).update("profileUrl", photo.toString());

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LogInActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
package com.example.zutr.user_auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
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


public class SignUpActivity extends AppCompatActivity {

    public static final String TUTOR_PATH = "zutr";
    public static final String STUDENT_PATH = "student";
    private static final String TAG = "SignUpActivity";
    public static final int RESULT_SIGN_IN_GOOGLE = 12231;


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
        signbtnGoogle = findViewById(R.id.sign_in_google);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();



        signbtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
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

                startLoginActivity();


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


        // Build a GoogleSignInClient with the options specified by gso.
        //GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


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
                        Toast.makeText(SignUpActivity.this, "Updated succesfully", Toast.LENGTH_SHORT).show();

                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        database.collection(collectionPath).document(user.getUid()).update("profileUrl", user.getPhotoUrl().toString());


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
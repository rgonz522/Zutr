package com.example.zutr.user_auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    public static final String TAG = " LoginActivty";
    public static final int RESULT_SIGN_IN_GOOGLE = 12231;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private Button btnSignZutr;
    private SignInButton signbtnGoogle;

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
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignUp);
        btnSignZutr = findViewById(R.id.btnZutrSignUp);
        pbload = findViewById(R.id.pbLoading);
        signbtnGoogle = findViewById(R.id.sign_in_google);

        pbload.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(view -> {

            pbload.setVisibility(View.VISIBLE);
            if (etEmail.getText() != null
                    && etPassword.getText() != null
                    && !etEmail.getText().toString().isEmpty()
                    && !etPassword.getText().toString().isEmpty()) {


                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                //authorizingUser(email, password);


            } else {

                etEmail.setError("Please fill required fields");
                etEmail.requestFocus();
                etEmail.setText("");
                etPassword.setText("");

                pbload.setVisibility(View.INVISIBLE);
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

        signbtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
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
                        etPassword.setText("");
                        etEmail.setText("");
                        etPassword.setError("Incorrect Email or Password");
                        etPassword.requestFocus();
                        Toast.makeText(LogInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                        pbload.setVisibility(View.INVISIBLE);

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


    private boolean isGoogleAccountSignedIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        return account != null;
    }


    private void signIn() {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RESULT_SIGN_IN_GOOGLE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RESULT_SIGN_IN_GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());

            Toast.makeText(this, "google account is connected", Toast.LENGTH_SHORT).show();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }


}
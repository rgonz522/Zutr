package com.example.zutr.user_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.zutr.R;
import com.example.zutr.models.Student;
import com.example.zutr.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public static final String TUTOR_PATH = "zutr";
    public static final String STUDENT_PATH = "student";


    private Button btnSignUp;
    private EditText etuser_name;
    private EditText etfirst_name;
    private EditText etlast_name;
    private EditText etpassword;
    private EditText etaddress;
    private EditText etemail;

    private User user;

    FirebaseFirestore database;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = findViewById(R.id.btnSignUp);
        etuser_name = findViewById(R.id.etEmail);
        etfirst_name = findViewById(R.id.etFirstName);
        etlast_name = findViewById(R.id.etLastName);
        etpassword = findViewById(R.id.etPassword);
        etaddress = findViewById(R.id.etAddress);
        etemail = findViewById(R.id.etEmail);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(false);
            }
        });


    }

    private void registerUser(boolean tutor) {

        final String path = (tutor ? TUTOR_PATH : STUDENT_PATH);

        final String username = etuser_name.getText().toString().trim();
        final String first_name = etfirst_name.getText().toString().trim();
        final String last_name = etlast_name.getText().toString().trim();
        final String email = etemail.getText().toString().trim();
        final String address = etaddress.getText().toString().trim() + "";
        final String password = etpassword.getText().toString().trim();


        if (username.isEmpty()) {
            etuser_name.setError(getString(R.string.input_error_username));
            etuser_name.requestFocus();
            return;
        }

        if (first_name.isEmpty()) {
            etfirst_name.setError(getString(R.string.input_error_name));
            etfirst_name.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etemail.setError(getString(R.string.input_error_email));
            etemail.requestFocus();
            return;
        }

        //if email is acceptable format ie: abc@email.com
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError(getString(R.string.input_error_email_invalid));
            etemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etpassword.setError(getString(R.string.input_error_password));
            etpassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etpassword.setError(getString(R.string.input_error_password_length));
            etpassword.requestFocus();
            return;
        }

        if (last_name.isEmpty()) {
            etlast_name.setError(getString(R.string.input_error_lastname));
            etlast_name.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            user = new User(
                                    username,
                                    first_name,
                                    last_name,
                                    email,
                                    address
                            );
                            createNewStudent(user, path);
                        }

                    }
                });


    }


    public void createNewStudent(User user, String path) {

        //TODO Make this less boilerplate code
        DocumentReference mDocref = database.collection(path).document();


        Map<String, Object> dataToSave = new HashMap<String, Object>();

        dataToSave.put(Student.KEY_USERNAME, user.getUsername());
        dataToSave.put(Student.KEY_FIRSTNAME, user.getFirst_name());
        dataToSave.put(Student.KEY_LASTNAME, user.getLast_name());
        dataToSave.put(Student.KEY_EMAIL, user.getEmail());
        dataToSave.put(Student.KEY_ADDRESS, user.getAddress());

        Log.i("button", "onClick: ");
        mDocref.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.i("SignUpActivty", "onSuccess: ");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                Log.e("SignUpActivity", "onFailure: ", e);
            }
        });


    }
}
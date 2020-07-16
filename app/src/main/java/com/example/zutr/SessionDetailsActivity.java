package com.example.zutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zutr.models.Session;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SessionDetailsActivity extends AppCompatActivity {

    public static final String TAG = "SessionDetailsActivty";

    private boolean isTutor;

    private TextView tvDate;
    private TextView tvSubject;
    private TextView tvTutor;
    private TextView tvQuestion;
    private TextView tvType;
    private EditText etAnswer;

    private Button btnZutrStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        //initialize textviews and button
        tvDate = findViewById(R.id.tvDate);
        tvSubject = findViewById(R.id.tvSubject);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvType = findViewById(R.id.tvType);
        btnZutrStart = findViewById(R.id.btnZutrStart);
        etAnswer = findViewById(R.id.etAnswer);

        Intent intent = getIntent();
        final Session session = (Session) intent.getSerializableExtra(Session.PATH);

        //Set the view values with the session values

        tvDate.setText(session.getTimeStart());
        tvSubject.setText(session.getSubject());
        tvQuestion.setText(session.getQuestion());
        tvType.setText(session.getSessionType() + "");


        Log.i(TAG, "onCreate: " + session.getQuestion());


        if (LogInActivity.IS_TUTOR) {

            btnZutrStart.setVisibility(View.VISIBLE);
            btnZutrStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateSessionTutor(session.getStudentId(), session.getQuestion(), etAnswer.getText().toString());
                }
            });
        } else {
            btnZutrStart.setVisibility(View.GONE);
        }


    }


    private void updateSessionTutor(String studentId, String question, final String answer) {

        final FirebaseFirestore dataBase = FirebaseFirestore.getInstance();


        dataBase.collection(Session.PATH)
                .whereEqualTo(Session.KEY_STUDENT_UID, studentId)
                .whereEqualTo(Session.KEY_QUESTION, question).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                dataBase.collection(Session.PATH).document(document.getId()).update(Session.KEY_TUTOR_UID, currentUserID);
                                dataBase.collection(Session.PATH).document(document.getId()).update(Session.KEY_ANSWER, answer);
                                Log.i(TAG, "onComplete: ");
                                finish();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}
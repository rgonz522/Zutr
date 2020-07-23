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

import com.example.zutr.fragments.ChatsFragment;
import com.example.zutr.models.Message;
import com.example.zutr.models.Session;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jivesoftware.smack.chat.Chat;

import java.util.Date;

public class SessionDetailsActivity extends AppCompatActivity {

    public static final String TAG = "SessionDetailsActivty";
    public static final long DATE_MIN_EQUAL = 900000L;
    private boolean isTutor;

    private TextView tvDate;
    private TextView tvSubject;
    private TextView tvTutor;
    private TextView tvQuestion;
    private TextView tvType;
    private TextView tvAnswered;
    private EditText etAnswer;


    private Button btnZutrStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        //TODO Fix session details UI
        //initialize textviews and button
        tvDate = findViewById(R.id.tvDate);
        tvSubject = findViewById(R.id.tvSubject);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvType = findViewById(R.id.tvType);
        tvAnswered = findViewById(R.id.tvAnswered);
        btnZutrStart = findViewById(R.id.btnZutrStart);
        etAnswer = findViewById(R.id.etAnswer);

        Intent intent = getIntent();
        final Session session = (Session) intent.getSerializableExtra(Session.PATH);

        //Set the view values with the session values

        tvDate.setText(session.getRelativeTimeAgo());
        tvSubject.setText(session.getSubject());
        tvQuestion.setText(session.getQuestion());
        tvType.setText(getTypeSession(session.getSessionType()));
        tvAnswered.setText("Answer: \n\n" + session.getAnswer());

        Log.i(TAG, "onCreate: " + session.getSessionType());


        if (LogInActivity.IS_TUTOR && hasNoTutor(session.getTutorId())) {

            tvAnswered.setVisibility(View.GONE);
            btnZutrStart.setVisibility(View.VISIBLE);
            btnZutrStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    updateSessionTutor(session.getStudentId(), session.getQuestion(), etAnswer.getText().toString());

                }
            });
        } else {
            btnZutrStart.setVisibility(View.GONE);
            etAnswer.setVisibility(View.GONE);
        }


    }

    private String getTypeSession(int typecode) {
        String type = "";
        switch (typecode) {
            case Session.SESSION_CALL:
                type = "Call Session";
                break;
            case Session.SESSION_TEXT:
                type = "Text Session";
                break;
            case Session.SESSION_VIDEO:
                type = "Video Session";
                break;
            case Session.SESSION_QUESTION:
                type = "Question";
                break;
        }
        Log.i(TAG, "getTypeSession: " + type + "code:" + typecode);
        return type;

    }

    private boolean hasNoTutor(String tutorId) {

        return tutorId == null || tutorId.isEmpty() || tutorId.equals(Session.NO_TUTOR_YET);
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
                                updateChat(studentId, currentUserID, document.getDate(Session.KEY_CREATED_AT), answer);
                                Log.i(TAG, "onComplete: ");

                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void updateChat(String studentID, String currentUserID, Date sessionCreatedAt, String answer) {


        CollectionReference colRef = FirebaseFirestore.getInstance().collection(ChatsFragment.CHAT_PATH);
        //Chats -> where equal to solicited question -> Messages -> add response from tutor

        Log.i(TAG, "onComplete: document time" + sessionCreatedAt.getTime());


        colRef.whereEqualTo(ChatsFragment.STUDENT_ID_PATH, studentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        Message message = new Message(answer, currentUserID, new Date());

                        Long chatTime = 0L;

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            //set tutor to accepting tutor

                            chatTime = documentSnapshot.getDate(Session.KEY_CREATED_AT).getTime();

                            if (Math.abs(chatTime - sessionCreatedAt.getTime()) < DATE_MIN_EQUAL) {

                                Log.i(TAG, "onComplete: THEY MATCH: " + documentSnapshot.get(Message.KEY_MSG_BODY));
                                colRef.document(documentSnapshot.getId()).update(ChatsFragment.TUTOR_ID_PATH, currentUserID);

                                //send reply message
                                colRef.document(documentSnapshot.getId())
                                        .collection(ChatsFragment.MESSAGE_PATH)
                                        .document()
                                        .set(message);
                            } else {
                                Log.i(TAG, "onComplete: they dont match");
                            }
                            Log.i(TAG, "onComplete: uodated chat and tutor" + chatTime);

                        }


                        finish();

                    }
                });

    }


}
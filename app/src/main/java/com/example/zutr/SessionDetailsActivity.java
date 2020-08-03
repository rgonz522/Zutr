package com.example.zutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zutr.models.Message;
import com.example.zutr.models.Session;
import com.example.zutr.models.Tutor;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SessionDetailsActivity extends AppCompatActivity {

    public static final String TAG = "SessionDetailsActivty";
    public static final long DATE_MIN_EQUAL = 900000L;
    private boolean isTutor;


    public static final String MESSAGE_PATH = "";
    public static final String TUTOR_ID_PATH = "";
    public static final String STUDENT_ID_PATH = "";
    public static final String CHAT_PATH = "";

    private boolean ratedByStudent;

    private TextView tvTutor;
    private EditText etAnswer;
    RatingBar rbZutrRate;

    private FirebaseFirestore dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);


        dataBase = FirebaseFirestore.getInstance();

        //initialize textviews and button
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvQuestion = findViewById(R.id.tvQuestion);
        TextView tvType = findViewById(R.id.tvType);
        TextView tvAnswered = findViewById(R.id.tvAnswered);
        Button btnZutrStart = findViewById(R.id.btnZutrStart);
        etAnswer = findViewById(R.id.etAnswer);
        rbZutrRate = findViewById(R.id.rbZutrRate);
        RelativeLayout relativeLayout = findViewById(R.id.rlDetails);


        Intent intent = getIntent();
        final Session session = (Session) intent.getSerializableExtra(Session.PATH);

        Log.i(TAG, "onCreate: sessiontype:" + session.getSessionType());

        //Set the view values with the session values

        tvDate.setText(session.getRelativeTimeAgo());
        tvSubject.setText(session.getSubject());
        tvQuestion.setText(session.getQuestion());
        tvType.setText(getTypeSession(session.getSessionType()));
        tvAnswered.setText("Answer: \n\n" + session.getAnswer());
        Log.i(TAG, "onCreate: " + session.getSessionType());


        //if user is tutor and session has not been answered
        if (LogInActivity.IS_TUTOR && hasNoTutor(session.getTutorId())) {

            rbZutrRate.setVisibility(View.GONE);
            tvAnswered.setVisibility(View.GONE);
            btnZutrStart.setVisibility(View.VISIBLE);
            btnZutrStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateSessionTutor(session.getStudentId(), session.getQuestion(), etAnswer.getText().toString(), session.getSessionType());

                }
            });
            //if user is student and session has been answered
        } else if (!LogInActivity.IS_TUTOR && !hasNoTutor(session.getTutorId())) {

            btnZutrStart.setVisibility(View.GONE);
            etAnswer.setVisibility(View.GONE);
            rbZutrRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    ratedByStudent(v, session.getTutorId());
                }
            });
            //if user is tutor and its been answered
        } else if (LogInActivity.IS_TUTOR) {
            etAnswer.setVisibility(View.GONE);
            btnZutrStart.setVisibility(View.GONE);
            rbZutrRate.setVisibility(View.GONE);
            //if user is student and its not answered
        } else {
            etAnswer.setVisibility(View.GONE);
            btnZutrStart.setVisibility(View.GONE);
            rbZutrRate.setVisibility(View.GONE);

            tvAnswered.setText("No Answer Yet");
        }


    }


    private void ratedByStudent(float rate, String tutorID) {


        dataBase.collection(Tutor.PATH)
                .document(tutorID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {


                            Map<String, Object> updates = new HashMap<>();
                            rbZutrRate.setClickable(false);
                            //   Formula for new average rating given a new rate
                            //-----------------------------------------------------------
                            //
                            //       (num_rates * rating)  + new_rate
                            //   _______________________________________  = new average rating
                            //                   rates + 1
                            //
                            //-----------------------------------------------------------


                            // long and int cannot be null
                            //need an object to test if null

                            Object numRates = task.getResult().get(Tutor.AMT_RATES);

                            Double average = task.getResult().getDouble(Tutor.RATING);
                            int num_rates = 0;

                            Log.i(TAG, "onComplete: num_rates" + num_rates + numRates);
                            Log.i(TAG, "onComplete: average " + average);


                            if (numRates != null && average != null) {
                                num_rates = task.getResult().getLong(Tutor.AMT_RATES).intValue();
                                average *= num_rates;

                                num_rates++;

                                average += rate;

                                average /= num_rates;


                                updates.put(Tutor.RATING, average);
                                updates.put(Tutor.AMT_RATES, num_rates);


                                updateRate(updates, tutorID);

                            } else {
                                //if no previous average
                                //set the rate to be the average
                                //set then num_rates to be 1
                                average = new Double(rate);
                                num_rates = 1;

                                updates.put(Tutor.RATING, average);
                                updates.put(Tutor.AMT_RATES, num_rates);

                                writeFirstRate(updates, tutorID);

                            }


                        }

                    }
                });

    }

    private void writeFirstRate(Map<String, Object> first, String tutorID) {


        dataBase.collection(Tutor.PATH).document(tutorID).set(first, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: " + task.toString());
                            rbZutrRate.setClickable(true);
                        }
                    }
                });

    }

    private void updateRate(Map<String, Object> updates, String tutorID) {

        dataBase.collection(Tutor.PATH).document(tutorID).update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: " + task.toString());
                            rbZutrRate.setClickable(true);
                        }
                    }
                });
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


    private void updateSessionTutor(String studentId, String question, final String answer, int sessionType) {


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


                                if (sessionType == Session.SESSION_TEXT) {
                                    updateChat(studentId, currentUserID, document.getDate(Session.KEY_CREATED_AT), answer);
                                } else {
                                    startMainActivity();
                                }
                                Log.i(TAG, "onComplete: ");

                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void updateChat(String studentID, String currentUserID, Date sessionCreatedAt, String answer) {


        CollectionReference colRef = FirebaseFirestore.getInstance().collection(CHAT_PATH);
        //Chats -> where equal to solicited question -> Messages -> add response from tutor

        Log.i(TAG, "onComplete: document time" + sessionCreatedAt.getTime());


        colRef.whereEqualTo(STUDENT_ID_PATH, studentID)
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
                                colRef.document(documentSnapshot.getId()).update(TUTOR_ID_PATH, currentUserID);

                                //send reply message
                                colRef.document(documentSnapshot.getId())
                                        .collection(MESSAGE_PATH)
                                        .document()
                                        .set(message);
                            } else {
                                Log.i(TAG, "onComplete: they dont match");
                            }
                            Log.i(TAG, "onComplete: uodated chat and tutor" + chatTime);

                        }


                        startMainActivity();

                    }
                });

    }


    private void startMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
package com.example.zutr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.zutr.fragments.ProfileFragment;
import com.example.zutr.models.Message;
import com.example.zutr.models.Session;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SessionDetailsActivity extends AppCompatActivity {

    public static final String TAG = "SessionDetailsActivty";


    public static final String MESSAGE_PATH = "messages";
    public static final String TUTOR_ID_PATH = "tutorID";
    public static final String STUDENT_ID_PATH = "studentID";
    public static final String CHAT_PATH = "chats";


    private TextView tvAnswered;
    private EditText etAnswer;
    private RatingBar rbZutrRate;

    private boolean ratedByStudent;
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
        Button btnZutrStart = findViewById(R.id.btnZutrStart);
        tvAnswered = findViewById(R.id.tvAnswered);
        etAnswer = findViewById(R.id.etAnswer);
        rbZutrRate = findViewById(R.id.rbZutrRate);


        Intent intent = getIntent();
        final Session session = (Session) intent.getSerializableExtra(Session.PATH);

        if (session == null) finish();

        ratedByStudent = session.isRatedByStudent();


        //Set the view values with the session values


        tvDate.setText(session.getRelativeTimeAgo());
        tvSubject.setText(session.getSubject());
        tvQuestion.setText(session.getQuestion());
        tvType.setText(getTypeSession(session.getSessionType()));

        Log.i(TAG, "onCreate: answer:" + session.getAnswer());
        Log.i(TAG, "onCreate: tutor: " + session.getTutorId());
        if (isAnswerReady(session)) {
            Log.i(TAG, "onCreate: answer is ready");
            setUserRealName(session.getTutorId(), session.getAnswer());

            Log.i(TAG, "onCreate: usereal name  :" + tvAnswered.getText());
            if (ratedByStudent) {
                rbZutrRate.setClickable(false);
                getRating(session.getTutorId());
            }
        }


        //if user is tutor and session has not been answered
        if (LogInActivity.IS_TUTOR && hasNoTutor(session.getTutorId())) {

            rbZutrRate.setVisibility(View.GONE);
            tvAnswered.setVisibility(View.GONE);
            btnZutrStart.setVisibility(View.VISIBLE);
            btnZutrStart.setOnClickListener(view -> {

                if (etAnswer.getText() != null && etAnswer.getText().toString().length() > 0) {
                    updateSessionTutor(session, etAnswer.getText().toString());
                } else {
                    etAnswer.setError("Empty Answer");
                    etAnswer.requestFocus();
                    etAnswer.setText("");
                }
            });

            //if user is student and session has been answered
        } else if (!LogInActivity.IS_TUTOR
                && !hasNoTutor(session.getTutorId())) {

            btnZutrStart.setVisibility(View.GONE);
            etAnswer.setVisibility(View.GONE);

            rbZutrRate.setOnRatingBarChangeListener((ratingBar, v, b) -> rateByStudent(v, session));

            tvAnswered.setOnClickListener(view -> {

                startProfileFragment(session.getTutorId());

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

            Log.i(TAG, "onCreate: " + session.getAnswer());
            tvAnswered.setText(Session.NO_ANSWER);
        }


    }


    private void rateByStudent(float rate, Session session) {

        String tutorID = session.getTutorId();

        session.setRatedByStudent(true);
        ratedByStudent = true;
        setRatedByStudent(tutorID, session.getStudentId(), session.getQuestion());

        dataBase.collection(Tutor.PATH)
                .document(tutorID)
                .get()
                .addOnCompleteListener(task -> {

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

                        Object numRates = Objects.requireNonNull(task.getResult()).get(Tutor.AMT_RATES);

                        Double average = task.getResult().getDouble(Tutor.RATING);
                        int num_rates = 0;

                        Log.i(TAG, "onComplete: num_rates" + num_rates + numRates);
                        Log.i(TAG, "onComplete: average " + average);


                        if (numRates != null && average != null) {
                            num_rates = Objects.requireNonNull(task.getResult().getLong(Tutor.AMT_RATES)).intValue();
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
                            average = (double) rate;
                            num_rates = 1;

                            updates.put(Tutor.RATING, average);
                            updates.put(Tutor.AMT_RATES, num_rates);

                            writeFirstRate(updates, tutorID);

                        }

                        rbZutrRate.setClickable(false);
                        rbZutrRate.setIsIndicator(true);
                        rbZutrRate.setRating(Float.parseFloat(String.valueOf(average)));

                    }

                });

    }

    private void getRating(String tutorID) {

        dataBase.collection(Tutor.PATH)
                .document(tutorID)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Double rate = task.getResult().getDouble(Tutor.RATING);

                        if (rate != null) {

                            rbZutrRate.setRating(rate.floatValue());
                        }
                    }
                });


    }

    private void writeFirstRate(Map<String, Object> first, String tutorID) {


        dataBase.collection(Tutor.PATH).document(tutorID).set(first, SetOptions.merge())
                .addOnCompleteListener(task -> {


                    if (task.isSuccessful()) {
                        Log.i(TAG, "onComplete: " + task.toString());
                        rbZutrRate.setClickable(true);
                    }
                });

    }

    private void updateRate(Map<String, Object> updates, String tutorID) {

        dataBase.collection(Tutor.PATH).document(tutorID).update(updates)
                .addOnCompleteListener(task -> {


                    if (task.isSuccessful()) {
                        Log.i(TAG, "onComplete: " + task.toString());
                        rbZutrRate.setClickable(true);
                    }
                });
    }

    private void setRatedByStudent(String tutorID, String studentID, String question) {


        dataBase.collection(Session.PATH)
                .whereEqualTo(Session.KEY_TUTOR_UID, tutorID)
                .whereEqualTo(Session.KEY_STUDENT_UID, studentID)
                .whereEqualTo(Session.KEY_QUESTION, question)
                .get()
                .addOnCompleteListener(task -> {

                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {

                        String id = documentSnapshot.getId();
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(Session.KEY_RATED_ZUTR, true);

                        dataBase.collection(Session.PATH).document(id).set(updates, SetOptions.merge())
                                .addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {
                                        Log.i(TAG, "onComplete: " + task1.toString());
                                        rbZutrRate.setClickable(true);
                                    }
                                });

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


    private void updateSessionTutor(Session session, String answer) {


        dataBase.collection(Session.PATH)
                .whereEqualTo(Session.KEY_STUDENT_UID, session.getStudentId())
                .whereEqualTo(Session.KEY_QUESTION, session.getQuestion()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                            Log.i(TAG, "updateSessionTutor: " + session.getAnswer());
                            dataBase.collection(Session.PATH).document(document.getId()).update(Session.KEY_TUTOR_UID, currentUserID);
                            dataBase.collection(Session.PATH).document(document.getId()).update(Session.KEY_ANSWER, answer);


                            if (session.getSessionType() == Session.SESSION_TEXT) {
                                updateChat(session);
                            } else {
                                finish();
                            }
                            Log.i(TAG, "onComplete: ");

                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateChat(Session session) {

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference colRef = FirebaseFirestore.getInstance().collection(CHAT_PATH);
        //Chats -> where equal to solicited question -> Messages -> add response from tutor

        colRef.whereEqualTo(STUDENT_ID_PATH, session.getStudentId())
                .whereEqualTo(Session.KEY_QUESTION, session.getQuestion())
                .get()
                .addOnCompleteListener(task -> {

                    Message message = new Message(session.getAnswer(), null, session.getTutorId(), new Date());


                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        //set tutor to accepting tutor


                        String chatDocID = documentSnapshot.getId();

                        colRef.document(chatDocID).update(TUTOR_ID_PATH, currentUserID);

                        //send reply message
                        colRef.document(chatDocID)
                                .collection(MESSAGE_PATH)
                                .document()
                                .set(message);

                    }


                    finish();

                });

    }


    public void setUserRealName(final String userID, String answer) {

        Log.i(TAG, "getUserRealName: " + userID);

        FirebaseFirestore database = FirebaseFirestore.getInstance();


        //has to be an array in order to be changed within
        //inner CompleteListener Class

        database.collection(Tutor.PATH).document(userID).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                String userRealName = "";

                userRealName += task.getResult().getString(User.KEY_FIRSTNAME);
                userRealName += "  ";
                userRealName += task.getResult().getString(User.KEY_LASTNAME);

                Log.i(TAG, "setUserRealName: " + userRealName);
                if (!userRealName.contains("null")) {

                    Log.i(TAG, "getUserRealName: " + userRealName.indexOf("null"));
                    Log.i(TAG, "getUserRealName: " + userRealName);

                    tvAnswered.setText(String.format("%s: \n\n%s", userRealName, answer));

                    Log.i(TAG, "onCreate: " + tvAnswered.getText());
                } else {
                    tvAnswered.setText(String.format(" \n\n%s", answer));
                }
            }
        });


    }


    private boolean isAnswerReady(Session session) {
        return session.getTutorId() != null &&
                !session.getTutorId().equals(Session.NO_TUTOR_YET)
                && session.getTutorId() != null
                && session.getAnswer() != null
                && !session.getAnswer().isEmpty()
                && !session.getTutorId().isEmpty();
    }

    private void startProfileFragment(String remoteID) {

        String path = Tutor.PATH;

        findViewById(R.id.rlDetails).setVisibility(View.GONE);
        Fragment fragment = new ProfileFragment(remoteID, path);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_right, R.anim.enter_from_left, R.anim.exit_from_left);
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();

    }

}
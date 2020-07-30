package com.example.zutr.adapters;

import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.SessionDetailsActivity;

import com.example.zutr.fragments.MessagesFragment;
import com.example.zutr.models.Session;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.Serializable;
import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {

    public static final String TAG = "SessionsAdapter";

    public static final int NO_USER_FOUND = -1;

    private Context context;
    private List<Session> sessions;


    public SessionsAdapter(Context context, List<Session> sessions) {
        this.context = context;
        this.sessions = sessions;

    }


    @Override
    public SessionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);


        return new SessionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionsAdapter.ViewHolder holder, int position) {
        Session session = sessions.get(position);

        holder.bind(session);

    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tvSubject;
        private TextView tvDate;
        private TextView tvTutor;
        private TextView tvQuestion;
        private TextView tvAnswer;

        private TextView tvType;
        private ImageView ivUserPic;
        private ImageView ivChat;

        private LinearLayout linearLayout;


        public ViewHolder(@NonNull View view) {
            super(view);


            tvSubject = view.findViewById(R.id.tvSubject);
            tvDate = view.findViewById(R.id.tvDate);
            tvQuestion = view.findViewById(R.id.tvQuestion);
            tvType = view.findViewById(R.id.tvType);
            tvTutor = view.findViewById(R.id.tvTutor);
            tvAnswer = view.findViewById(R.id.tvAnswer);
            ivChat = view.findViewById(R.id.ivChat);

            linearLayout = view.findViewById(R.id.llSessionItem);


        }


        public void bind(Session session) {



            tvQuestion.setText("Question" + ": " + session.getQuestion());


            Log.i(TAG, "bind: " + session.getTutorId());
            if (session.getTutorId() == Session.NO_TUTOR_YET || session.getTutorId() == null) {
                tvTutor.setVisibility(View.GONE);

            } else {


                // path ->> Student Collection
                // Student Collection ->> ID ->> Document

                String path = LogInActivity.IS_TUTOR ? Student.PATH : Tutor.PATH;
                String userID = LogInActivity.IS_TUTOR ? session.getStudentId() : session.getTutorId();

                String userName = getUserRealName(path, userID);
                Log.i(TAG, "bind: userName" + userName);


            }

            tvSubject.setText(Session.KEY_SUBJECT + ": " + session.getSubject());


            if (session.isSessionQuestion()) {
                tvDate.setVisibility(View.GONE);
            } else {
                tvDate.setText(session.getTimeStart());
            }

            if (session.isFinished()) {
                tvAnswer.setText(session.getAnswer());
            } else {
                tvAnswer.setVisibility(View.GONE);
            }


            if (isChatAvailible(session)) {

            } else {
                ivChat.setVisibility(View.GONE);
            }


            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        // make sure the position is valid, i.e. actually exists in the view
                        // get the Session at the position
                        Session session = sessions.get(position);

                        String remoteID = LogInActivity.IS_TUTOR ? session.getStudentId() : session.getTutorId();

                        //if the session is text and the user is involved
                        if (isChatAvailible(session)) {

                            startChat(remoteID, session.getCreatedAt().getTime());
                        } else {
                            startDetails(session);
                        }
                    }

                }
            });
        }

        private boolean isChatAvailible(Session session) {
            return session.getSessionType() == Session.SESSION_TEXT
                    && !session.getTutorId().equals(Session.NO_TUTOR_YET)
                    && session.getTutorId() != null;
        }

        private void startDetails(Session session) {


            // create intent for the new activity
            Intent intent = new Intent(context, SessionDetailsActivity.class);

            Log.i(TAG, "onClick: " + session.getSessionType());
            // pass the Session[already serializable] , use its already declared path as a key
            intent.putExtra(Session.PATH, (Serializable) session);
            context.startActivity(intent);

        }

        private void startChat(String tutorID, long createdAt) {

            Log.i(TAG, "openChatWith: " + tutorID);
            MessagesFragment messagesFragment = new MessagesFragment(tutorID, createdAt);
            FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.flContainer, messagesFragment);
            fragmentTransaction.commit();


        }

        public String getUserRealName(String collectionPath, final String userID) {


            FirebaseFirestore database = FirebaseFirestore.getInstance();


            final StringBuilder userRealName = new StringBuilder("");
            //has to be an array in order to be changed within
            //inner CompleteListener Class

            database.collection(collectionPath).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        userRealName.append(task.getResult().getString(User.KEY_FIRSTNAME) + task.getResult().getString(User.KEY_LASTNAME));
                        Log.i(TAG, "getUserRealName: " + userRealName);

                        if (userRealName.indexOf("null") == NO_USER_FOUND) {
                            tvTutor.setText(userRealName);
                        }
                    }
                }
            });

            return userRealName.toString();


        }

    }


}

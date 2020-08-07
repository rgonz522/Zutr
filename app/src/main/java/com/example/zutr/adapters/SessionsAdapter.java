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
import androidx.recyclerview.widget.RecyclerView;

import com.example.zutr.MainActivity;
import com.example.zutr.MessagesActivity;
import com.example.zutr.R;
import com.example.zutr.SessionDetailsActivity;
import com.example.zutr.fragments.OpenSessionsFragment;
import com.example.zutr.models.Session;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.LogInActivity;
import com.google.firebase.firestore.FirebaseFirestore;

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


            tvQuestion.setText(String.format("Question: %s", session.getQuestion()));


            Log.i(TAG, "bind: " + session.getTutorId());
            if (session.getTutorId() == Session.NO_TUTOR_YET
                    || session.getTutorId() == null
                    || session.getTutorId().isEmpty()
                    || session.getAnswer() == null) {

                tvTutor.setVisibility(View.GONE);
                tvAnswer.setVisibility(View.GONE);
                ivChat.setVisibility(View.GONE);

            } else {

                tvTutor.setVisibility(View.VISIBLE);
                // path ->> Student Collection
                // Student Collection ->> ID ->> Document

                String path = LogInActivity.IS_TUTOR ? Student.PATH : Tutor.PATH;
                String userID = LogInActivity.IS_TUTOR ? session.getStudentId() : session.getTutorId();

                String userName = getUserRealName(path, userID);
                Log.i(TAG, "bind: userName" + userName);


                tvAnswer.setText(session.getAnswer());


                if (isChatAvailable(session)) {
                    ivChat.setVisibility(View.VISIBLE);
                } else {
                    ivChat.setVisibility(View.GONE);


                }

            }
            tvType.setText(session.getSessionTypeString());

            tvSubject.setText(String.format("%s: %s", Session.KEY_SUBJECT, session.getSubject()));


            if (session.isSessionQuestion()) {
                tvDate.setVisibility(View.GONE);
            } else {
                tvDate.setVisibility(View.VISIBLE);
                tvDate.setText(session.getTimeStart());
            }


            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        // make sure the position is valid, i.e. actually exists in the view
                        // get the Session at the position
                        Session session = sessions.get(position);


                        //if the session is text and the user is involved
                        if (isChatAvailable(session)) {

                            startChat(session);
                        } else {
                            startDetails(session);
                        }
                    }

                }
            });


        }

        private boolean isChatAvailable(Session session) {

            return session.getSessionType() == Session.SESSION_TEXT
                    && session.getTutorId() != null &&
                    !session.getTutorId().equals(Session.NO_TUTOR_YET)
                    && session.getTutorId() != null
                    && session.getAnswer() != null
                    && !session.getAnswer().isEmpty()
                    && !session.getTutorId().isEmpty();

        }

        private void startDetails(Session session) {


            // create intent for the new activity
            Intent intent = new Intent(context, SessionDetailsActivity.class);

            Log.i(TAG, "onClick: " + session.getSessionType());
            // pass the Session[already serializable] , use its already declared path as a key
            intent.putExtra(Session.PATH, session);
            ((MainActivity) (context)).startActivityForResult(intent, OpenSessionsFragment.RESULT_OK);

        }

        private void startChat(Session session) {

            Intent intent = new Intent(context, MessagesActivity.class);
            intent.putExtra(Session.PATH, session);
            context.startActivity(intent);


        }

        public String getUserRealName(String collectionPath, final String userID) {


            FirebaseFirestore database = FirebaseFirestore.getInstance();


            final StringBuilder userRealName = new StringBuilder();
            //has to be an array in order to be changed within
            //inner CompleteListener Class

            database.collection(collectionPath).document(userID).get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    userRealName.append(task.getResult().getString(User.KEY_FIRSTNAME));
                    userRealName.append("   ");
                    userRealName.append(task.getResult().getString(User.KEY_LASTNAME));
                    Log.i(TAG, "getUserRealName: " + userRealName);

                    if (userRealName.indexOf("null") == NO_USER_FOUND) {
                        tvTutor.setText(userRealName);
                    }
                }
            });

            return userRealName.toString();


        }

    }


}

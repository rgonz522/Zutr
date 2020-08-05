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

import com.example.zutr.MessagesActivity;
import com.example.zutr.R;
import com.example.zutr.SessionDetailsActivity;
import com.example.zutr.models.Session;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "SessionsAdapter";

    public static final int NO_USER_FOUND = -1;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    private Context context;
    private List<Session> sessions;


    public SessionsAdapter(Context context, List<Session> sessions) {
        this.context = context;
        this.sessions = sessions;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            // Here Inflating your recyclerview item layout
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            // Here Inflating your header view
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_session_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else return null;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            // You have to set your header items values with the help of model class and you can modify as per your needs

            headerViewHolder.bind();
        } else if (holder instanceof ItemViewHolder) {
            Session session = sessions.get(position);

            ((ItemViewHolder) holder).bind(session);
        }


    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {


        public HeaderViewHolder(View view) {
            super(view);

        }

        public void bind() {
            Log.i(TAG, "bind: header");
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {


        private TextView tvSubject;
        private TextView tvDate;
        private TextView tvTutor;
        private TextView tvQuestion;
        private TextView tvAnswer;

        private TextView tvType;
        private ImageView ivUserPic;
        private ImageView ivChat;

        private LinearLayout linearLayout;


        public ItemViewHolder(@NonNull View view) {
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
            tvType.setText(session.getSessionTypeString());

            tvSubject.setText(String.format("%s: %s", Session.KEY_SUBJECT, session.getSubject()));


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

                            startChat(session);
                        } else {
                            startDetails(session);
                        }
                    }

                }
            });


        }

        private boolean isChatAvailible(Session session) {

            Log.i(TAG, "isChatAvailible:  " + session.getSessionType());
            Log.i(TAG, "isChatAvailible:  " + session.getTutorId());
            Log.i(TAG, "isChatAvailible:  " + session.getAnswer());
            boolean chat = session.getSessionType() == Session.SESSION_TEXT
                    && !session.getTutorId().equals(Session.NO_TUTOR_YET)
                    && session.getTutorId() != null;

            Log.i(TAG, "isChatAvailible: " + chat);

            return chat;
        }

        private void startDetails(Session session) {


            // create intent for the new activity
            Intent intent = new Intent(context, SessionDetailsActivity.class);

            Log.i(TAG, "onClick: " + session.getSessionType());
            // pass the Session[already serializable] , use its already declared path as a key
            intent.putExtra(Session.PATH, session);
            context.startActivity(intent);

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

            database.collection(collectionPath).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        userRealName.append(task.getResult().getString(User.KEY_FIRSTNAME));
                        userRealName.append("   ");
                        userRealName.append(task.getResult().getString(User.KEY_LASTNAME));
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

package com.example.zutr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zutr.models.Session;

import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {


    public static final String NO_TUTOR = "Not answered yet";

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
        private TextView tvType;


        public ViewHolder(@NonNull View view) {
            super(view);


            tvSubject = view.findViewById(R.id.tvSubject);
            tvDate = view.findViewById(R.id.tvDate);
            tvQuestion = view.findViewById(R.id.tvQuestion);
            tvType = view.findViewById(R.id.tvType);
            tvTutor = view.findViewById(R.id.tvTutor);


        }


        public void bind(Session session) {

            if (session.getQuestion() != null) {
                tvQuestion.setText(Session.KEY_QUESTION + ": " + session.getQuestion());

                if (session.getTutor_email() == null || session.getTutor_email().isEmpty()) {
                    tvTutor.setText(NO_TUTOR);
                } else {
                    //TODO better tutor information
                    tvTutor.setText(String.format("@%s", session.getTutor_email()));
                }

                tvSubject.setText(Session.KEY_SUBJECT + ": " + session.getSubject());

                if (session.isSessionQuestion()) {
                    tvDate.setVisibility(View.GONE);
                } else {
                    tvDate.setText(session.getTime_started());
                }

            } else {
                //TODO FIX empty question, item view visibility
                itemView.setVisibility(View.GONE);
            }

        }

    }
}

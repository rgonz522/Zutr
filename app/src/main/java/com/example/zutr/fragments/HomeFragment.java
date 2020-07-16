package com.example.zutr.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.SessionsAdapter;
import com.example.zutr.models.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    private RecyclerView rvSessions;
    private SessionsAdapter adapter;
    private List<Session> sessions;

    private FirebaseFirestore dataBase;
    private FirebaseUser currentUser;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dataBase = FirebaseFirestore.getInstance();
        sessions = new ArrayList<>();
        rvSessions = view.findViewById(R.id.rvSessions);
        adapter = new SessionsAdapter(getContext(), sessions);

        rvSessions.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvSessions.setLayoutManager(linearLayoutManager);

        querySessions();
    }


    protected void querySessions() {

        final String session_user_id = MainActivity.IS_TUTOR ? Session.KEY_TUTOR_UID : Session.KEY_STUDENT_UID;

        Log.i(TAG, "querySessions: session user id : " + session_user_id);

        final List<Session> newSessions = new ArrayList<>();

        Log.i(TAG, "querySessions: session user id : " + currentUser.getUid());


        dataBase.collection(Session.PATH)
                .whereEqualTo(session_user_id, currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String subject = "subject";     //TODO Subject Implementation

                                String question = document.getString(Session.KEY_QUESTION);
                                Double wage = document.getDouble(Session.KEY_WAGE);

                                String answer = document.getString(Session.KEY_ANSWER);


                                Session session = new Session(null, wage, subject, question);


                                session.setStudentId(document.getString(Session.KEY_STUDENT_UID));
                                session.setTutorId(document.getString(Session.KEY_TUTOR_UID));
                                session.setAnswer(answer);

                                Log.i(TAG, "onComplete: " + document.getString(Session.KEY_TUTOR_UID));
                                newSessions.add(session);

                            }

                            sessions.addAll(newSessions);
                            adapter.notifyDataSetChanged();


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        Log.i(TAG, "onComplete: " + newSessions.toString());


    }
}
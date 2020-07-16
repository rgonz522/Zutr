package com.example.zutr.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class OpenSessionsFragment extends Fragment {


    public static final String TAG = "OpenSessionsFragment";


    private RecyclerView rvSessions;
    private SessionsAdapter adapter;
    private List<Session> sessions;

    private FirebaseFirestore dataBase;
    private FirebaseUser currentUser;


    public OpenSessionsFragment() {
        // Required empty public constructor
    }

    public static OpenSessionsFragment newInstance() {
        OpenSessionsFragment fragment = new OpenSessionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open_sessions, container, false);
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

        //Open Sessions are those without tutor's aka where
        //tutor id is null

        final List<Session> newSessions = new ArrayList<>();

        dataBase.collection(Session.PATH).whereEqualTo(Session.KEY_TUTOR_UID, null).whereEqualTo(Session.KEY_TUTOR_UID, null).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                //if no question associated with the session then don't show it
                                if (documentSnapshot.get(Session.KEY_QUESTION) != null) {
                                    Session session =
                                            new Session((String) documentSnapshot.get(Session.KEY_STUDENT_UID)
                                                    , (Double) documentSnapshot.get(Session.KEY_WAGE)
                                                    , (String) documentSnapshot.get(Session.KEY_SUBJECT)
                                                    , (String) documentSnapshot.get(Session.KEY_QUESTION));


                                    Log.i(TAG, "onComplete: querying " + session.getQuestion());
                                    newSessions.add(session);
                                }
                            }
                        } else {
                            Log.i(TAG, "onComplete: querying task failed" + task.getResult() + task.getException());
                        }
                        sessions.addAll(newSessions);
                        adapter.notifyDataSetChanged();
                    }
                });

    }
}
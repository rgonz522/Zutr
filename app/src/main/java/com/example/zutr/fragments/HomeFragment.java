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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.example.zutr.R;
import com.example.zutr.SessionsAdapter;
import com.example.zutr.models.Session;
import com.example.zutr.models.Student;
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
    private FirebaseUser current_user;

    //TODO  implement recycler view using sessions adapter
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
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        dataBase = FirebaseFirestore.getInstance();
        sessions = new ArrayList<>();
        rvSessions = view.findViewById(R.id.rvSessions);
        adapter = new SessionsAdapter(getContext(), sessions);

        rvSessions.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvSessions.setLayoutManager(linearLayoutManager);

        querySessions();
    }


    private void querySessions() {

        final List<Session> newSessions = new ArrayList<>();

        dataBase.collection(Session.PATH).whereEqualTo(Session.KEY_STUDENT_EMAIL, current_user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Session session =
                                new Session((String) documentSnapshot.get(Session.KEY_STUDENT_EMAIL)
                                        , (String) documentSnapshot.get(Session.KEY_TUTOREMAIL)
                                        , (Double) documentSnapshot.get(Session.KEY_WAGE)
                                        , (String) documentSnapshot.get(Session.KEY_SUBJECT)
                                        , (String) documentSnapshot.get(Session.KEY_QUESTION));

                        Log.i(TAG, "onComplete: querying " + session);
                        newSessions.add(session);
                    }
                }
                sessions.addAll(newSessions);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
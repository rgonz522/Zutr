package com.example.zutr.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zutr.R;
import com.example.zutr.adapters.SessionsAdapter;
import com.example.zutr.models.Session;
import com.example.zutr.user_auth.LogInActivity;
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

        initiatePersonalProfile();
        querySessions();

    }


    private void querySessions() {

        final String sessionUserId = LogInActivity.IS_TUTOR ? Session.KEY_TUTOR_UID : Session.KEY_STUDENT_UID;


        Log.i(TAG, "querySessions: is tutor?" + LogInActivity.IS_TUTOR);
        Log.i(TAG, "querySessions: session user id : " + sessionUserId);

        final List<Session> newSessions = new ArrayList<>();

        Log.i(TAG, "querySessions: session user id : " + currentUser.getUid());


        Log.i(TAG, "querySessions: ");

        dataBase.collection(Session.PATH)
                .whereEqualTo(sessionUserId, currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                Session session = document.toObject(Session.class);

                                Log.i(TAG, "onComplete: " + session.isRatedByStudent());
                                newSessions.add(session);


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                        sessions.addAll(newSessions);
                        adapter.notifyDataSetChanged();

                    }
                });


    }

    private void initiatePersonalProfile() {


        ProfileFragment profileFragment = new ProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flProfile, profileFragment);
        fragmentTransaction.commit();
    }
}
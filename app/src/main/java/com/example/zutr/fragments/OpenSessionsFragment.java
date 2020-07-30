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
import android.widget.SearchView;

import com.example.zutr.R;
import com.example.zutr.adapters.SessionsAdapter;
import com.example.zutr.models.Session;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class OpenSessionsFragment extends Fragment {


    public static final String TAG = "OpenSessionsFragment";


    private SessionsAdapter adapter;
    private List<Session> sessions;

    private FirebaseFirestore dataBase;


    public OpenSessionsFragment() {
        // Required empty public constructor
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


        Log.i(TAG, "onViewCreated: " + "HEllo".substring(0, 1));
        dataBase = FirebaseFirestore.getInstance();
        sessions = new ArrayList<>();

        querySessions();

        RecyclerView rvSessions = view.findViewById(R.id.rvSessions);
        SearchView svSearch = view.findViewById(R.id.svSearch);
        adapter = new SessionsAdapter(getContext(), sessions);


        rvSessions.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvSessions.setLayoutManager(linearLayoutManager);


        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    protected void querySessions() {

        //Open Sessions are those without tutor's aka where
        //tutor id is null

        final List<Session> newSessions = new ArrayList<>();

        dataBase.collection(Session.PATH)
                .orderBy(Session.KEY_CREATED_AT, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {

                            String tutorId = documentSnapshot.getString(Session.KEY_TUTOR_UID);

                            //if no question associated with the session then don't show it
                            if ((documentSnapshot.get(Session.KEY_QUESTION) != null) &&
                                    ((tutorId == null) || tutorId.isEmpty()
                                            || tutorId.equals(Session.NO_TUTOR_YET))) {

                                Session session = documentSnapshot.toObject(Session.class);
                                newSessions.add(session);
                            }
                        }

                    } else {
                        Log.i(TAG, "onComplete: querying task failed" + task.getResult() + task.getException());
                    }
                    sessions.clear();
                    sessions.addAll(newSessions);
                    adapter.notifyDataSetChanged();
                });

    }


    /**
     * Orders sessions by their edit
     * distance to the search string
     * <p>
     * uses Comparator.
     *
     * @param search From searchview
     */

    private void search(String search) {
        Log.i(TAG, "search: word " + search);
        for (Session session : sessions) {
            Log.i(TAG, "search: " + session.getQuestion());
        }

        Collections.sort(sessions, (session, t1) -> {

            int session1 = calculateLCS(search, session.getQuestion());

            int session2 = calculateLCS(search, t1.getQuestion());

            Log.i(TAG, "compare: " + (session2 - session1));
            return session2 - session1;
        });
        for (Session session : sessions) {
            Log.i(TAG, "search: " + session.getQuestion());
        }

        adapter.notifyDataSetChanged();

    }


    public int findLongestCommonSequence(String string1, String string2, Integer[][] lcsValues) {

        int length1 = string1.length() - 1;     //string and array index , not length
        int length2 = string2.length() - 1;


        String new1;
        String new2;

        //empty strings
        if (length1 == 0 || length2 == 0) {
            Log.i(TAG, "findLongestCommonSequence: " + " emptystrings");
            return 0;
        } else if (lcsValues[length1][length2] != null) {
            return lcsValues[length1][length2];
        }


        Log.i(TAG, "findLongestCommonSequence: " + length1);
        Log.i(TAG, "findLongestCommonSequence: " + length2);

        char char1 = string1.charAt(length1 - 1);
        char char2 = string2.charAt(length2 - 1);

        Log.i(TAG, "findLongestCommonSequence: " + " char1: " + char1 + "char2 " + char2);


        if (char1 == char2) {
            new1 = (string1.substring(0, length1));
            new2 = string2.substring(0, length2);

            Log.i(TAG, "findLongestCommonSequence: " + " equal char");

            return 1 + findLongestCommonSequence(new1, new2, lcsValues);

        } else {

            Log.i(TAG, "findLongestCommonSequence: " + " not equal char");
            new1 = (string1.substring(0, length1));
            new2 = string2.substring(0, length2);

            int lcs1 = findLongestCommonSequence(new1, string2, lcsValues);

            int lcs2 = findLongestCommonSequence(string1, new2, lcsValues);

            lcsValues[length1][length2] = Math.max(lcs1, lcs2);

            return lcsValues[length1][length2];
        }


    }

    public int calculateLCS(String string1, String string2) {

        Integer[][] lcsvalues = new Integer[string1.length()][string2.length()];

        return findLongestCommonSequence(string1, string2, lcsvalues);

    }


}
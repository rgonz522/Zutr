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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OpenSessionsFragment extends Fragment {


    public static final String TAG = "OpenSessionsFragment";


    private RecyclerView rvSessions;
    private SearchView svSearch;
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


        Log.i(TAG, "onViewCreated: " + "HEllo".substring(0, 1));
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dataBase = FirebaseFirestore.getInstance();
        sessions = new ArrayList<>();

        querySessions();

        rvSessions = view.findViewById(R.id.rvSessions);
        svSearch = view.findViewById(R.id.svSearch);
        adapter = new SessionsAdapter(getContext(), sessions);


        rvSessions.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvSessions.setLayoutManager(linearLayoutManager);


    }

    protected void querySessions() {

        //Open Sessions are those without tutor's aka where
        //tutor id is null

        final List<Session> newSessions = new ArrayList<>();

        dataBase.collection(Session.PATH)
                .orderBy(Session.KEY_CREATED_AT, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                String tutorId = documentSnapshot.getString(Session.KEY_TUTOR_UID);

                                //if no question associated with the session then don't show it
                                if (documentSnapshot.get(Session.KEY_QUESTION) != null &&
                                        (tutorId == null || tutorId.isEmpty()
                                                || tutorId.equals(Session.NO_TUTOR_YET))) {

                                    Session session =
                                            new Session((String) documentSnapshot.get(Session.KEY_STUDENT_UID)
                                                    , (Double) documentSnapshot.get(Session.KEY_WAGE)
                                                    , (String) documentSnapshot.get(Session.KEY_SUBJECT)
                                                    , (String) documentSnapshot.get(Session.KEY_QUESTION));


                                    Date date = documentSnapshot.getTimestamp(Session.KEY_CREATED_AT).toDate();

                                    newSessions.add(session);
                                }
                            }

                        } else {
                            Log.i(TAG, "onComplete: querying task failed" + task.getResult() + task.getException());
                        }
                        sessions.clear();
                        sessions.addAll(newSessions);
                        adapter.notifyDataSetChanged();
                    }
                });

    }


    /**
     * The Levenshtein distance is a string metric for measuring the difference between two sequences.
     * Informally, the Levenshtein distance between two words is the minimum number of single-character edits
     * (i.e. insertions, deletions or substitutions) required to change one word into the other. The phrase
     * 'edit distance' is often used to refer specifically to Levenshtein distance.
     *
     * @param s String one
     * @param t String two
     * @return the 'edit distance' (Levenshtein distance) between the two strings.
     */
    public static int levenshteinDistance(CharSequence s, CharSequence t) {
        // degenerate cases          s
        if (s == null || "".equals(s)) {
            return t == null || "".equals(t) ? 0 : t.length();
        } else if (t == null || "".equals(t)) {
            return s.length();
        }

        // create two work vectors of integer distances
        int[] v0 = new int[t.length() + 1];
        int[] v1 = new int[t.length() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }

        int sLen = s.length();
        int tLen = t.length();
        for (int i = 0; i < sLen; i++) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < tLen; j++) {
                int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
                v1[j + 1] = (int) Math.min(Math.min(v1[j] + 1, v0[j + 1] + 1), v0[j] + cost);
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            System.arraycopy(v1, 0, v0, 0, v0.length);
        }

        return v1[t.length()];
    }
}
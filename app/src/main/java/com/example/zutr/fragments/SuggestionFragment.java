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

import okhttp3.Headers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.zutr.adapters.ResourceAdapter;
import com.example.zutr.models.Resource;
import com.example.zutr.models.Session;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


public class SuggestionFragment extends Fragment {


    public static final String TAG = "Suggestion Fragments";


    private RecyclerView rvResources;
    private ResourceAdapter adapter;
    private List<Resource> resources;


    public SuggestionFragment() {
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
        return inflater.inflate(R.layout.fragment_suggestion, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvResources = view.findViewById(R.id.rvResource);
        resources = new ArrayList<>();
        adapter = new ResourceAdapter(resources, getContext());

        rvResources.setAdapter(adapter);

        rvResources.setLayoutManager(new LinearLayoutManager(getContext()));

        querySessions();


    }


    protected void querySessions() {

        final String sessionUserId = LogInActivity.IS_TUTOR ? Session.KEY_TUTOR_UID : Session.KEY_STUDENT_UID;

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.i(TAG, "querySessions: is tutor?" + LogInActivity.IS_TUTOR);
        Log.i(TAG, "querySessions: session user id : " + sessionUserId);

        final List<String> sessions = new ArrayList<>();

        Log.i(TAG, "querySessions: session user id : " + userID);


        Log.i(TAG, "querySessions: ");

        FirebaseFirestore.getInstance()
                .collection(Session.PATH)
                .whereEqualTo(sessionUserId, userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sessions.addAll(getSeparateWords(document.getString(Session.KEY_QUESTION)));
                                Log.i(TAG, "onComplete: " + document.getString(Session.KEY_QUESTION));
                            }
                            updateResources(sessions);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }


                    }
                });


    }


    public void updateResources(List<String> searches) {

        resources.clear();

        Set<String> set = new HashSet<String>(searches);

        for (String search : set) {

            Log.i(TAG, "updateResources: " + search);
            if (search.length() > 5) {
                Log.i(TAG, "updateResources: " + queryLOC(search).getDescription());
            }


        }


    }

    public Resource queryLOC(String search) {

        Resource resource = new Resource();
        String url = "https://www.loc.gov/websites/?q=" + search + "&fo=json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "OnSuccess");
                JSONObject jsonObject = json.jsonObject;

                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.d(TAG, results.toString());


                    resource.setTitle(results.getJSONObject(0).getString(Resource.KEY_TITLE));
                    resource.setDescription(results.getJSONObject(0).getJSONArray(Resource.KEY_DESCRIPTION).getString(0));

                    resource.setResrcLink(results.getJSONObject(0).getString(Resource.KEY_URL));


                    if (results.getJSONObject(0).getJSONArray(Resource.KEY_IMAGE).length() > 0) {
                        resource.setImageURL(results.getJSONObject(0).getJSONArray(Resource.KEY_IMAGE).getString(0));
                    }


                    JSONArray subjects = results.getJSONObject(0).getJSONArray(Resource.KEY_SUBJECT);
                    String subject = "";

                    for (int i = 0; i < subjects.length(); i++) {
                        subject += (subjects.get(i) + ",  ");
                    }


                    resource.setSubject(subject);


                    resources.add(resource);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.d(TAG, "Hit JSON Exception");
                    e.printStackTrace();

                }


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "OnFailure" + response, throwable);
            }

        });


        return resource;
    }


    /**
     * @param wholeQuestion
     * @return List of separate words in question
     */

    private List<String> getSeparateWords(String wholeQuestion) {


        List<String> ngrams = new ArrayList<>();


        int index = 0;
        while (wholeQuestion.length() - 1 > index) {


            int nextindex = wholeQuestion.substring(index).indexOf(" ");


            if (nextindex == -1) {
                ngrams.add(wholeQuestion.substring(index));
                break;
            } else if (wholeQuestion.length() - 1 > nextindex) {
                nextindex += index;
                ngrams.add(wholeQuestion.substring(index, nextindex));

                index = nextindex + 1;
            }


        }


        return ngrams;
    }

}

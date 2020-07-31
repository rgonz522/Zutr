package com.example.zutr.fragments;

import android.content.Intent;
import android.net.Uri;
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

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
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


        RecyclerView rvResources = view.findViewById(R.id.rvResource);
        resources = new ArrayList<>();
        adapter = new ResourceAdapter(resources, getContext());

        rvResources.setAdapter(adapter);

        rvResources.setLayoutManager(new LinearLayoutManager(getContext()));

        querySessions();


    }


    protected void querySessions() {

        final String sessionUserId = LogInActivity.IS_TUTOR ? Session.KEY_TUTOR_UID : Session.KEY_STUDENT_UID;

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        final List<String> sessions = new ArrayList<>();


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


        RequestParams params = new RequestParams();
        params.put("query", search);


        String token = getContext().getResources().getString(R.string.MendeleySecret);

        String url = "https://api.mendeley.com/search/catalog?access_token=" + token;


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "OnSuccess");
                //JSONObject jsonObject = json.jsonObject;

                Log.i(TAG, "onSuccess: " + json.toString());
                JSONArray results = json.jsonArray;
                Log.d(TAG, results.toString());


                try {
                    JSONObject jsonObject = results.getJSONObject(0);
                    resource.setTitle(jsonObject.getString(Resource.KEY_TITLE));

                    Log.i(TAG, "onSuccess: title " + jsonObject.getString(Resource.KEY_TITLE));
                    Log.i(TAG, "onSuccess: description" + jsonObject.getString(Resource.KEY_DESCRIPTION));
                    resource.setDescription(jsonObject.getString(Resource.KEY_DESCRIPTION));
                    Log.i(TAG, "onSuccess: link" + jsonObject.getString(Resource.KEY_URL));
                    resource.setResrcLink(jsonObject.getString(Resource.KEY_URL));
                    resource.setCreated(jsonObject.getString(Resource.KEY_CREATED));


                    resources.add(resource);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
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


        List<String> separateWords = new ArrayList<>();


        int index = 0;
        while (wholeQuestion.length() - 1 > index) {


            int nextIndex = wholeQuestion.substring(index).indexOf(" ");


            if (nextIndex == -1) {
                separateWords.add(wholeQuestion.substring(index));
                break;
            } else if (wholeQuestion.length() - 1 > nextIndex) {
                nextIndex += index;
                separateWords.add(wholeQuestion.substring(index, nextIndex));

                index = nextIndex + 1;
            }


        }


        return separateWords;
    }


}

package com.example.zutr.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.zutr.R;
import com.example.zutr.adapters.ResourceAdapter;
import com.example.zutr.models.Resource;
import com.example.zutr.models.Session;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class SuggestionFragment extends Fragment {


    public static final String TAG = "Suggestion Fragments";


    private ResourceAdapter adapter;
    private List<Resource> resources;
    private ProgressBar pbLoading;


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
        pbLoading = view.findViewById(R.id.pbLoading);

        resources = new ArrayList<>();
        adapter = new ResourceAdapter(resources, getContext());

        rvResources.setAdapter(adapter);

        rvResources.setLayoutManager(new LinearLayoutManager(getContext()));

        querySessions();

        pbLoading.setVisibility(View.VISIBLE);

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
                                searchKeywords(document.getString(Session.KEY_QUESTION));
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }


                    }
                });


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

                    resource.setDescription(jsonObject.getString(Resource.KEY_DESCRIPTION));
                    resource.setResrcLink(jsonObject.getString(Resource.KEY_URL));
                    resource.setCreated(jsonObject.getString(Resource.KEY_CREATED));


                    resources.add(resource);
                    adapter.notifyDataSetChanged();


                    pbLoading.setVisibility(View.INVISIBLE);
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


    public void searchKeywords(String text) {


        String api_key = getContext().getResources().getString(R.string.KeywordsAPIkey);
        String host = "https://apis.paralleldots.com/v4/";

        String url = host + "keywords";

        AsyncHttpClient client = new AsyncHttpClient();
        RequestBody params = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", api_key)
                .addFormDataPart("text", text)
                .build();


        RequestHeaders header = new RequestHeaders();
        header.put("cache-control", "no-cache");

        client.post(url, header, null, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {


                try {

                    if (!json.jsonObject.isNull("keywords") && json.jsonObject.get("keywords") instanceof JSONArray) {

                        JSONArray objectKeywords = json.jsonObject.getJSONArray("keywords");


                        JSONObject object = objectKeywords.getJSONObject(0);
                        String keyword = object.getString("keyword");


                        Log.i(TAG, "onSuccess: " + keyword);


                        queryLOC(keyword);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: ", throwable);
            }
        });


    }


}

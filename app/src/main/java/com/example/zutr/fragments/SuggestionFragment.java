package com.example.zutr.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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


public class SuggestionFragment extends Fragment {


    public static final String TAG = "Suggestion Fragments";

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


        queryLOC("baseballcaps");


    }


    public void queryLOC(String search) {
        String url = "https://www.loc.gov/search/?q=" + search + "&fo=json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "OnSuccess");
                JSONObject jsonObject = json.jsonObject;

                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.d(TAG, results.toString());

                } catch (JSONException e) {
                    Log.d(TAG, "Hit JSON Exception");
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "OnFailure");
            }

        });
    }

}
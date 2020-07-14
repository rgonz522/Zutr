package com.example.zutr.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zutr.R;

public class GetZutrQuestionFragment extends Fragment {


    public GetZutrQuestionFragment() {
        // Required empty public constructor
    }

    public static GetZutrQuestionFragment newInstance(String param1, String param2) {
        GetZutrQuestionFragment fragment = new GetZutrQuestionFragment();
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
        return inflater.inflate(R.layout.fragment_get_zutr_question, container, false);
    }
}
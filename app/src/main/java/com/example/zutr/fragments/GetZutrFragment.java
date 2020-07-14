package com.example.zutr.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Session;
import com.example.zutr.models.Student;


public class GetZutrFragment extends Fragment {

    private Button btnStartQuestion;
    private Button btnStartSession;
    private RelativeLayout rlGetZutr;

    public GetZutrFragment() {
        // Required empty public constructor
    }

    public static GetZutrFragment newInstance(String param1, String param2) {
        GetZutrFragment fragment = new GetZutrFragment();
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
        return inflater.inflate(R.layout.fragment_get_zutr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnStartSession = view.findViewById(R.id.btnSession);
        btnStartQuestion = view.findViewById(R.id.btnQuestion);
        rlGetZutr = view.findViewById(R.id.rl_getzutr);

        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment childFragment = new GetZutrSessionFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_container, childFragment).commit();
                rlGetZutr.setVisibility(View.GONE);
            }
        });

        btnStartQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment childFragment = new GetZutrQuestionFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_container, childFragment).commit();

                rlGetZutr.setVisibility(View.GONE);

            }
        });

    }


    public void createSession(String tutorUsername) {


        Session session = new Session("abc123", tutorUsername, 8.56);


        Log.i("button", "onClick: ");

        MainActivity.DataBase.collection("session").document().set(session);

    }
}
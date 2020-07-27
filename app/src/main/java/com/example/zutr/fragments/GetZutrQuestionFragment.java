package com.example.zutr.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zutr.CheckoutActivity;
import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;

public class GetZutrQuestionFragment extends Fragment {

    public static final String PATH = "session";
    private static final String TAG = "GetZutrQuestionFrag";

    private SeekBar sbPrice;
    private Button btnQuestion;
    private EditText etPrice;
    private EditText etQuestion;
    private Spinner spnSubject;     //Future Implementation


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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sbPrice = view.findViewById(R.id.sbWage);
        btnQuestion = view.findViewById(R.id.btnSmtQues);
        etPrice = view.findViewById(R.id.tvWage);
        etQuestion = view.findViewById(R.id.multetQuestion);

        etPrice.setText("$10");
        etPrice.setClickable(false);
        //price is set at 10$ for the time being


        sbPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Toast.makeText(getContext(), "Price is 10$", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double price = getPrice(etPrice.getText().toString());
                String question = etQuestion.getText().toString();
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getUid();


                if (price != null && !question.isEmpty() && !userEmail.isEmpty()) {
                    saveSession(userEmail, price, question);


                }

            }
        });


    }

    private Double getPrice(String price) {
        String number = "";
        Double retprice = 0.0;

        if (price.contains("$")) {
            //if using slider price will be "$2.0"
            //remove first index with 0. into "02.0"
            number = price.replace('$', '0');

        } else {
            number = price.toString();
        }

        try {
            retprice = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            retprice = null;
            Log.e(TAG, "getPrice: ", e);
        }

        return retprice;

    }

    private void saveSession(String currentUser, Double price, String question) {

        //Subject is not yet implemented
        //At the creation of the Session request , no tutor is yet assigned

        Session session = new Session(currentUser, price, null, question);

        FirebaseFirestore.getInstance().collection(PATH).document().set(session)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        startCheckOut();
                    }
                });


    }

    private void startCheckOut() {
        Intent intent = new Intent(getContext(), CheckoutActivity.class);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
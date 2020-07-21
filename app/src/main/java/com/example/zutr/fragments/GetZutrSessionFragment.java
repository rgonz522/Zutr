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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.zutr.MainActivity;
import com.example.zutr.R;
import com.example.zutr.models.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetZutrSessionFragment extends Fragment {

    public static final String TAG = "GetZutrSessionFrag";
    public static final String PATH = "session";
    public static final String VIDEO = "Video";
    public static final String CALL = "Call";
    public static final String TEXT = "Text";
    public static final String IN_PERSON = "In Person";


    private final double CONVERT_SEEK_2_DOLLARS = 4.0;

    private Spinner spnTypeSession;
    private Spinner spnSubject;     //future implementation
    private EditText etPrice;
    private EditText etQuestion;
    private SeekBar sbPrice;
    private Button btnSubmit;


    public GetZutrSessionFragment() {
        // Required empty public constructor
    }

    public static GetZutrSessionFragment newInstance() {
        GetZutrSessionFragment fragment = new GetZutrSessionFragment();
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
        return inflater.inflate(R.layout.fragment_get_zutr_session, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        spnTypeSession = view.findViewById(R.id.spnTypeSession);
        sbPrice = view.findViewById(R.id.sbPrice);
        etPrice = view.findViewById(R.id.etHourlyPrice);
        etQuestion = view.findViewById(R.id.etQuestion);
        btnSubmit = view.findViewById(R.id.btnSubmitSession);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> typeSessionAdapter;
        typeSessionAdapter = ArrayAdapter.createFromResource(getContext(), R.array.type_of_session, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        typeSessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnTypeSession.setAdapter(typeSessionAdapter);

        sbPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                etPrice.setText("$" + i / CONVERT_SEEK_2_DOLLARS);
                Log.i(TAG, "onProgressChanged: :" + spnTypeSession.getSelectedItem());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double price = getPrice(etPrice.getText().toString());
                String question = etQuestion.getText().toString();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                int typeSession = 0;
                switch (spnTypeSession.getSelectedItem().toString()) {
                    case VIDEO:
                        typeSession = Session.SESSION_VIDEO;
                        break;
                    case CALL:
                        typeSession = Session.SESSION_CALL;
                        break;
                    case TEXT:
                        typeSession = Session.SESSION_TEXT;
                        break;

                }
                if (price != null && !question.isEmpty() && !userId.isEmpty() && typeSession != 0) {
                    saveSession(userId, price, question, typeSession);
                    //TODO: Insert Payment/Charging Method.
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

    private void saveSession(String currentUser, Double price, final String question, int typeOfSession) {

        //Subject is not yet implemented
        //At the creation of the Session request , no tutor is yet assigned


        Session session = new Session(currentUser, price, question, typeOfSession);

        FirebaseFirestore.getInstance().collection(PATH).document().set(session)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startMainActivity();


                    }
                });
    }

    private void startMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
}
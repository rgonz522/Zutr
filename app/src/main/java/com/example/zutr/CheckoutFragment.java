package com.example.zutr;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.zutr.fragments.ChatsFragment;
import com.example.zutr.models.Session;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import java.util.HashMap;
import java.util.Map;


public class CheckoutFragment extends Fragment {

    public static final String TAG = "CheckoutActivity";
    public static final String STRIPE_CUSTOMER_PATH = "stripe_customers";
    public static final String CARD_TOKEN = "tokens";


    private Stripe stripe;
    Button payButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Activity activity = getActivity();
        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        // ⚠️ Don't forget to switch this to your live-mode publishable key before publishing your app

        PaymentConfiguration.init(getActivity().getApplicationContext(), "pk_test_51H5x0bGxy48jgmpT1MWiIK6JFLbDZQ3XkP0beQA71Lwxq2zRSOExr8jwBwWtATodIwE8L8hyfiPV2IzwCEOcdtUd00DjIKHFdY"); // Get your key here: https://stripe.com/docs/keys#obtain-api-keys


        stripe = new Stripe(activity.getApplicationContext()
                , PaymentConfiguration.getInstance(activity.getApplicationContext()).getPublishableKey());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_checkout, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Get the card details from the card widget
        CardInputWidget cardInputWidget = view.findViewById(R.id.cardInputWidget);
        Log.i(TAG, "onCreate: ");


        // Hook up the pay button to the card widget and stripe instance
        payButton = view.findViewById(R.id.payButton);

        payButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Log.i(TAG, "onClick: ");
                Card card = cardInputWidget.getCard();

                if (card != null) {
                    // Create a Stripe Token from the card details

                    stripe.createToken(card, new ApiResultCallback<Token>() {

                        @Override
                        public void onSuccess(@NonNull Token result) {

                            // stripe.c(result);
                            resetApp();
                            Log.i(TAG, "onSuccess: token created");
                        }

                        @Override
                        public void onError(@NotNull Exception e) {
                            Log.e(TAG, "onError: ", e);
                        }


                    });
                }

            }


        });
    }

    private void updateSourceStripe(Token result) {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Map<String, Object> source = new HashMap<>();
        source.put("id", result.getId());
        source.put("object", result.component1());

        Log.i(TAG, "updateSourceStripe: " + source.toString());

        database.collection(STRIPE_CUSTOMER_PATH)
                .document(uid)
                .collection(CARD_TOKEN)
                .document(result.getId())
                .set(source)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "onComplete: " + task.getResult());
                        Log.i(TAG, "onComplete: updatedSource");
                    }
                });


    }


    private void resetApp() {

        Intent intent = new Intent(getContext(), LogInActivity.class);
        startActivity(intent);
    }

}
package com.example.zutr;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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


public class CheckoutActivity extends AppCompatActivity {

    public static final String TAG = "CheckoutActivity";
    public static final String STRIPE_CUSTOMER_PATH = "stripe_customers";
    public static final String CARD_TOKEN = "token";


    private Stripe stripe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        // ⚠️ Don't forget to switch this to your live-mode publishable key before publishing your app
        PaymentConfiguration.init(getApplicationContext(), "pk_test_51H5x0bGxy48jgmpT1MWiIK6JFLbDZQ3XkP0beQA71Lwxq2zRSOExr8jwBwWtATodIwE8L8hyfiPV2IzwCEOcdtUd00DjIKHFdY"); // Get your key here: https://stripe.com/docs/keys#obtain-api-keys

        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);
        WeakReference<CheckoutActivity> weakActivity = new WeakReference<>(this);
        payButton.setOnClickListener((View view) -> {
            // Get the card details from the card widget
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            Card card = cardInputWidget.getCard();
            if (card != null) {
                // Create a Stripe Token from the card details
                stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
                stripe.createToken(card, new ApiResultCallback<Token>() {

                    @Override
                    public void onSuccess(@NonNull Token result) {

                        updateSourceStripe(result);
                        Log.i(TAG, "onSuccess: token created");
                    }

                    @Override
                    public void onError(@NotNull Exception e) {
                        Log.e(TAG, "onError: ", e);
                    }


                });
            }

        });


    }

    private void updateSourceStripe(Token result) {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection(STRIPE_CUSTOMER_PATH)
                .document(uid)
                .collection(CARD_TOKEN)
                .document(result.getId())
                .set(result.getId())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "onComplete: updatedSource");
                    }
                });


    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message,
                              boolean restartDemo) {
        Activity activity = this;
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message);
            if (restartDemo) {
                builder.setPositiveButton("Restart demo",
                        (DialogInterface dialog, int index) -> {
                            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                            cardInputWidget.clear();
                        });
            } else {
                builder.setPositiveButton("Ok", null);
            }
            builder.create().show();
        });
    }
}
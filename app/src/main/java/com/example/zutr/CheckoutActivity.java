package com.example.zutr;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zutr.models.Session;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;


public class CheckoutActivity extends AppCompatActivity {

    public static final String TAG = "CheckoutActivity";
    public static final String STRIPE_CUSTOMER_PATH = "stripe_customers";
    public static final String CARD_TOKEN = "tokens";
    public static final String CUSTOMER_ID = "customer_id";


    private Stripe stripe;

    private ProgressBar pbLoading;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        // ⚠️ Don't forget to switch this to your live-mode publishable key before publishing your app

        PaymentConfiguration.init(this.getApplicationContext(), "pk_test_51H5x0bGxy48jgmpT1MWiIK6JFLbDZQ3XkP0beQA71Lwxq2zRSOExr8jwBwWtATodIwE8L8hyfiPV2IzwCEOcdtUd00DjIKHFdY"); // Get your key here: https://stripe.com/docs/keys#obtain-api-keys


        stripe = new Stripe(getApplicationContext()
                , PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());

        // Inflate the layout for this fragment


        Intent intent = getIntent();

        final Session session = (Session) intent.getSerializableExtra(Session.PATH);

        Log.i(TAG, "onCreate: " + session);
        if (session == null) {
            finish();
        }


        // Get the card details from the card widget
        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
        Log.i(TAG, "onCreate: ");


        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvInfo = findViewById(R.id.tvInfo);
        TextView tvService = findViewById(R.id.tvService);
        pbLoading = findViewById(R.id.pbLoading);


        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);


        tvPrice.setText(String.format("%s \t\t\t\t\t%s", "Total: ", session.getWage()));
        tvService.setText(String.format("%s Session, %s"
                , session.getSessionTypeString()
                , session.getSubject()));
        tvInfo.setText(session.getQuestion());
        pbLoading.setVisibility(View.GONE);


        payButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                pbLoading.setVisibility(View.VISIBLE);
                Log.i(TAG, "onClick: ");
                Card card = cardInputWidget.getCard();


                Log.i(TAG, "card:  " + card);
                if (card != null) {


                    checkOut(cardInputWidget);
                }
            }
        });

    }

    public void checkOut(CardInputWidget cardInputWidget) {


        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        ConfirmPaymentIntentParams confirmParams =
                ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams
                        (params, getResources().getString(R.string.StripeClientSecret));

        stripe.confirmPayment(CheckoutActivity.this, confirmParams);
    }


    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("Ok", null);

        builder.create().show();
        resetApp();
    }


    private void resetApp() {
        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(CheckoutActivity.this));
    }


    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        // The response from the server includes the Stripe publishable key and
        // PaymentIntent details.
        // For added security, our sample app gets the publishable key from the server
        String stripePublishableKey = responseMap.get("publishableKey");
        String paymentIntentClientSecret = responseMap.get("clientSecret");

        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull(stripePublishableKey)
        );
    }

    private final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<CheckoutActivity> activityRef;

        PayCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(Request request, @NonNull IOException e) {

            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull final Response response)
                throws IOException {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(() -> Toast.makeText(
                        activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }


    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CheckoutActivity> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                resetApp();
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage());
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            Log.i(TAG, "onError: " + e);
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }


    }
}


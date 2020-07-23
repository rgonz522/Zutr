package com.example.zutr.fragments;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zutr.adapters.MessagesAdapter;
import com.example.zutr.R;
import com.example.zutr.models.Message;
import com.example.zutr.models.Session;
import com.example.zutr.payment.ConnectWithStripeActivity;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class MessagesFragment extends Fragment {


    public static final String MESSAGE_PATH = "messages";
    public static final String CHAT_PATH = "chats";
    public static final String TUTOR_ID_PATH = "tutorID";
    public static final String STUDENT_ID_PATH = "studentID";
    public static final String TAG = "ChatFragment";


    private RecyclerView rvMessage;
    private EditText etNewMsg;
    private Button btnSendMsg;

    private MessagesAdapter adapter;
    private List<Message> messages;

    private FirebaseFirestore dataBase;


    private String localID;
    private String remoteID;
    private String remoteField;
    private String localField;

    public MessagesFragment() {

    }

    public MessagesFragment(String remoteID) {
        this.remoteID = remoteID;
        localID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (LogInActivity.IS_TUTOR) {
            remoteField = STUDENT_ID_PATH;
            localField = TUTOR_ID_PATH;
        } else {
            remoteField = TUTOR_ID_PATH;
            localField = STUDENT_ID_PATH;
        }
        Log.i(TAG, "ChatFragment: " + localID);
        Log.i(TAG, "ChatFragment: " + remoteID);
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataBase = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();
        rvMessage = view.findViewById(R.id.rvMessages);
        etNewMsg = view.findViewById(R.id.etNewMessage);
        btnSendMsg = view.findViewById(R.id.btnSendMsg);
        adapter = new MessagesAdapter(getContext(), messages);

        rvMessage.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvMessage.setLayoutManager(linearLayoutManager);


        if (localField != null && remoteField != null) {
            queryMessages();
        }


        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNewMsg.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Message is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(etNewMsg.getText().toString());
                    queryMessages();
                    etNewMsg.setText("");

                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);


        // Inflate the layout for this fragment
        return view;


    }


    private void queryMessages() {


        final List<Message> newMessages = new ArrayList<>();

        Log.i(TAG, "querySessions: session user id : " + localID);


        Log.i(TAG, "querySessions: ");

        dataBase.collection(CHAT_PATH)
                .whereEqualTo(remoteField, remoteID)
                .whereEqualTo(localField, localID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            dataBase.collection(CHAT_PATH)
                                    .document(documentSnapshot.getId())
                                    .collection(MESSAGE_PATH)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Message message = document.toObject(Message.class);

                                        newMessages.add(message);
                                        Log.i(TAG, "onComplete: MessageL " + message.getBody());
                                    }

                                    updateMessages(newMessages);

                                }
                            });


                        }


                    }
                });


    }

    private void updateMessages(List<Message> newMessages) {

        messages.clear();
        messages.addAll(newMessages);
        adapter.notifyDataSetChanged();


        Collections.sort(messages, new Comparator<Message>() {
            public int compare(Message message1, Message message2) {
                return message1.getCreatedAt().compareTo(message2.getCreatedAt());
            }
        });
    }

    private void sendMessage(String messageBody) {

        //Subject is not yet implemented
        //At the creation of the Session request , no tutor is yet assigned

        Message message = new Message(messageBody, localID, new Date());

        //Chats -> user/remote chat-> Messages
        FirebaseFirestore.getInstance().collection(CHAT_PATH)
                .whereEqualTo(remoteField, remoteID)
                .whereEqualTo(localField, localID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            FirebaseFirestore.getInstance()
                                    .collection(CHAT_PATH)
                                    .document(documentSnapshot.getId())
                                    .collection(MESSAGE_PATH)
                                    .document(new Date().toString())
                                    .set(message);
                            messages.add(message);
                        }

                    }
                });

    }


}
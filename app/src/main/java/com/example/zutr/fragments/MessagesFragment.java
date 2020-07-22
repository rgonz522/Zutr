package com.example.zutr.fragments;

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
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
    private String chatDocName;

    public MessagesFragment(String remoteID) {
        this.remoteID = remoteID;
        localID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatDocName = LogInActivity.IS_TUTOR ? remoteID : localID;

        Log.i(TAG, "ChatFragment: " + chatDocName);
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


        queryMessages();


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
                .document(chatDocName)
                .collection(MESSAGE_PATH)
                .orderBy(Session.KEY_CREATED_AT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Message message = documentSnapshot.toObject(Message.class);
                            newMessages.add(message);
                        }

                        messages.clear();
                        messages.addAll(newMessages);
                        adapter.notifyDataSetChanged();
                    }
                });


    }


    private void sendMessage(String messagebody) {

        //Subject is not yet implemented
        //At the creation of the Session request , no tutor is yet assigned

        Message message = new Message(messagebody, localID, new Date());

        //Chats -> user/remote chat-> Messages
        FirebaseFirestore.getInstance().collection(CHAT_PATH)
                .document(chatDocName)
                .collection(MESSAGE_PATH)
                .document(new Date().toString())
                .set(message)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.i(TAG, "onComplete: created message");
                    }
                });


    }

}
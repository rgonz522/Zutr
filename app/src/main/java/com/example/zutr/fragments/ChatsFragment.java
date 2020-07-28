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

import com.example.zutr.adapters.ChatsAdapter;
import com.example.zutr.adapters.MessagesAdapter;
import com.example.zutr.R;
import com.example.zutr.models.Message;
import com.example.zutr.models.Session;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatsFragment extends Fragment {


    public static final String MESSAGE_PATH = "messages";
    public static final String CHAT_PATH = "chats";
    public static final String TUTOR_ID_PATH = "tutorID";
    public static final String STUDENT_ID_PATH = "studentID";
    public static final String TAG = "ChatFragment";


    private RecyclerView rvChats;


    private ChatsAdapter adapter;
    private List<Message> messages;

    private FirebaseFirestore dataBase;


    private String localID;


    public ChatsFragment() {
        // Required empty public constructor
        localID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        rvChats = view.findViewById(R.id.rvChats);
        adapter = new ChatsAdapter(getContext(), messages);

        rvChats.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvChats.setLayoutManager(linearLayoutManager);


        queryMessages();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        // Inflate the layout for this fragment
        return view;


    }


    private void queryMessages() {

        String field = LogInActivity.IS_TUTOR ? TUTOR_ID_PATH : STUDENT_ID_PATH;
        String remoteField = LogInActivity.IS_TUTOR ? STUDENT_ID_PATH : TUTOR_ID_PATH;

        final List<Message> newMessages = new ArrayList<>();

        Log.i(TAG, "querySessions: session user id : " + localID);

        CollectionReference collectionReference = dataBase.collection(CHAT_PATH);
        Log.i(TAG, "querySessions: ");


        collectionReference
                .orderBy(Session.KEY_CREATED_AT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (documentSnapshot.get(field).equals(localID)) {

                                String remoteID = documentSnapshot.getString(remoteField);

                                //get messages for each Chat object
                                collectionReference.document(documentSnapshot.getId())
                                        .collection(ChatsFragment.MESSAGE_PATH)
                                        .orderBy(Session.KEY_CREATED_AT, Query.Direction.ASCENDING)
                                        .limit(1)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Message message = new Message();
                                            message.setAuthorID(remoteID);
                                            message.setCreatedAt(document.getDate(Message.KEY_CREATEDAT));
                                            message.setBody(document.getString(Message.KEY_MSG_BODY));
                                            Log.i(TAG, "onComplete: THEY MATCH: " + document.get(Message.KEY_MSG_BODY));
                                            newMessages.add(message);
                                            Log.i(TAG, "onComplete: message " + message.getAuthorID());
                                            Log.i(TAG, "onComplete: message was " + message.getRelativeTimeAgo());
                                        }
                                        messages.clear();
                                        messages.addAll(newMessages);


                                        Log.i(TAG, "onComplete: messages" + messages);
                                        adapter.notifyDataSetChanged();
                                    }
                                });


                            }


                        }
                    }
                });


    }


}
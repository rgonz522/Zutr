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

import com.example.zutr.MessagesAdapter;
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
import java.util.List;


public class ChatsFragment extends Fragment {


    public static final String MESSAGE_PATH = "messages";
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


    public ChatsFragment() {
        // Required empty public constructor
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

                    queryMessages();
                    etNewMsg.setText("");
                }
            }
        });


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

        final List<Message> newMessages = new ArrayList<>();

        Log.i(TAG, "querySessions: session user id : " + localID);


        Log.i(TAG, "querySessions: ");

        dataBase.collectionGroup(MESSAGE_PATH)
                .whereEqualTo(field, localID)
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
}
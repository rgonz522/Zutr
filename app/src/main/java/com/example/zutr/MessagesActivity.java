package com.example.zutr;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zutr.adapters.MessagesAdapter;
import com.example.zutr.fragments.ProfileFragment;
import com.example.zutr.models.Message;
import com.example.zutr.models.Session;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MessagesActivity extends AppCompatActivity {


    public static final String MESSAGE_PATH = "messages";
    public static final String CHAT_PATH = "chats";
    public static final String TUTOR_ID_PATH = "tutorID";
    public static final String STUDENT_ID_PATH = "studentID";
    public static final String TAG = "MessageFragment";
    public static final String HIDDEN_BY = "hiddenBy";
    private static final int NO_USER_FOUND = -1;

    private RecyclerView rvMessage;
    private TextView tvOpponent;
    private EditText etNewMsg;
    private Button btnSendMsg;
    private Button btnLTXDialog;

    private MessagesAdapter adapter;
    private List<Message> messages;

    private FirebaseFirestore dataBase;


    private String localID;
    private String remoteID;
    private String remoteField;
    private String localField;

    private String chatDocID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_messages);


        Intent intent = getIntent();
        final Session session = (Session) intent.getSerializableExtra(Session.PATH);


        if (LogInActivity.IS_TUTOR) {
            remoteField = STUDENT_ID_PATH;
            remoteID = session.getStudentId();
            localField = TUTOR_ID_PATH;
            localID = session.getTutorId();

        } else {
            remoteField = TUTOR_ID_PATH;
            remoteID = session.getTutorId();
            localField = STUDENT_ID_PATH;
            localID = session.getStudentId();
        }
        Log.i(TAG, "ChatFragment: " + localID);
        Log.i(TAG, "ChatFragment: " + remoteID);
        // Required empty public constructor


        dataBase = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();
        rvMessage = findViewById(R.id.rvMessages);
        tvOpponent = findViewById(R.id.tvOpponentName);
        etNewMsg = findViewById(R.id.etNewMessage);
        btnLTXDialog = findViewById(R.id.btnLatexDialog);
        btnSendMsg = findViewById(R.id.btnSendMsg);
        adapter = new MessagesAdapter(this, messages);

        rvMessage.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvMessage.setLayoutManager(linearLayoutManager);


        rvMessage.scrollToPosition(messages.size() - 1);

        if (localField != null && remoteField != null) {
            queryMessages(session.getQuestion());
        }

        Log.i(TAG, "onCreate: ");

        getUserRealName(remoteField, remoteID);

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNewMsg.getText().toString().isEmpty()) {
                    Toast.makeText(MessagesActivity.this, "Message is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(etNewMsg.getText().toString());
                    etNewMsg.setText("");

                }
            }
        });


        btnLTXDialog.setOnClickListener(view -> {

            showLatexDialog();
        });

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT |
                ItemTouchHelper.LEFT) {

            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark));


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {


                return false;

            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();

                Message removedMsg = messages.remove(swipedPosition);

                adapter.notifyDataSetChanged();

                eraseMessage(removedMsg.getBody(), removedMsg.getHiddenBy());


                Log.i(TAG, "onSwiped: ");
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }


                background.draw(c);
            }
        };

        ItemTouchHelper mIth = new ItemTouchHelper(callback);
        mIth.attachToRecyclerView(rvMessage);


        rvMessage.smoothScrollToPosition(0);


        tvOpponent.setOnClickListener(view -> {

            startProfileFragment();
        });
    }


    private void queryMessages(String question) {


        final List<Message> newMessages = new ArrayList<>();

        Log.i(TAG, "querySessions: session user id : " + localID);
        Log.i(TAG, "queryMessages: local field" + localField);
        Log.i(TAG, "queryMessages: remote id" + remoteID);
        Log.i(TAG, "queryMessages: remoted field " + remoteField);
        Log.i(TAG, "queryMessages: " + question);

        Log.i(TAG, "querySessions: ");

        dataBase.collection(CHAT_PATH)
                .whereEqualTo(remoteField, remoteID)
                .whereEqualTo(localField, localID)
                .whereEqualTo(Session.KEY_QUESTION, question)
                .get()
                .addOnCompleteListener(task -> {

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        Log.i(TAG, "onComplete: THEY MATCH: " + documentSnapshot.get(Message.KEY_MSG_BODY));

                        chatDocID = documentSnapshot.getId();
                        dataBase.collection(CHAT_PATH)
                                .document(chatDocID)
                                .collection(MESSAGE_PATH)
                                .get().addOnCompleteListener(task1 -> {

                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                Message message = document.toObject(Message.class);


                                if (message.getBody() == null || message.getBody().isEmpty()) {

                                } else {
                                    Log.i(TAG, "queryMessages: hiddenby" + message.getHiddenBy());
                                    Log.i(TAG, "queryMessages: currentUser " + localID);
                                    if (message.getHiddenBy() == null || !message.getHiddenBy().equals(localID)) {

                                        newMessages.add(message);
                                        Log.i(TAG, " Message " + message.getBody());
                                        Log.i(TAG, "queryMessages: " + message.getAuthorID());
                                        Log.i(TAG, "Adding the nothidden message...... ");
                                    }
                                }
                            }

                            updateMessages(newMessages);

                        });


                    }


                });


    }


    private void updateMessages(List<Message> newMessages) {

        for (Message message : newMessages) {
            Log.i(TAG, "updateMessages: " + message.getBody());
        }
        messages.clear();
        messages.addAll(newMessages);


        Collections.sort(messages, (message1, message2) ->
                message1.getCreatedAt().compareTo(message2.getCreatedAt()));

        adapter.notifyDataSetChanged();
        rvMessage.scrollToPosition(messages.size() - 1);
    }

    private void sendMessage(String messageBody) {

        //Subject is not yet implemented
        //At the creation of the Session request , no tutor is yet assigned

        Message message = new Message(messageBody, null, localID, new Date());

        //Chats -> user/remote chat-> Messages
        FirebaseFirestore.getInstance().collection(CHAT_PATH)
                .whereEqualTo(remoteField, remoteID)
                .whereEqualTo(localField, localID)
                .get()
                .addOnCompleteListener(task -> {

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        FirebaseFirestore.getInstance()
                                .collection(CHAT_PATH)
                                .document(documentSnapshot.getId())
                                .collection(MESSAGE_PATH)
                                .document(new Date().toString())
                                .set(message);

                        messages.add(message);
                        adapter.notifyDataSetChanged();
                    }

                });

    }


    private void eraseMessage(String body, String hiddenBy) {


        CollectionReference colRef = dataBase.collection(CHAT_PATH)
                .document(chatDocID)
                .collection(MESSAGE_PATH);

        //              colRef.whereEqualTo(Message.KEY_MSG_BODY, body)
        colRef.get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.i(TAG, "eraseMessage: lookign for " + body);

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Log.i(TAG, "eraseMessage: body found: " + document.getString(Message.KEY_MSG_BODY));
                            if (document.getString(Message.KEY_MSG_BODY) != null) {
                                if (document.getString(Message.KEY_MSG_BODY).equals(body)) {
                                    Log.i(TAG, "eraseMessage: both bodies match");
                                    Log.i(TAG, "eraseMessage: hiddeby:" + hiddenBy);
                                    if (hiddenBy == null || hiddenBy.isEmpty()) {
                                        colRef.document(document.getId())
                                                .update(HIDDEN_BY, localID)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Log.i(TAG, "onComplete: " + "message hidden");

                                                        refreshMessages();
                                                    } else {
                                                        Log.e(TAG, "onComplete: ", task1.getException());
                                                    }

                                                });

                                    } else if (hiddenBy.equals(remoteID)) {
                                        colRef.document(document.getId())
                                                .delete().addOnCompleteListener(task12 -> {
                                            if (task12.isSuccessful()) {
                                                Log.i(TAG, "onComplete: " + task12.getResult());
                                                Log.i(TAG, "onComplete: " + "message deleted");

                                                refreshMessages();
                                            } else {
                                                Log.e(TAG, "onComplete: ", task12.getException());
                                            }
                                        });

                                    }

                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "eraseMessage: ", task.getException());
                    }

                });


    }


    private void refreshMessages() {
        dataBase
                .collection(CHAT_PATH)
                .document(chatDocID)
                .collection(MESSAGE_PATH)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        List<Message> newMessages = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            String hiddenBy = documentSnapshot.getString(HIDDEN_BY);

                            Log.i(TAG, "--------------------------- ");
                            Log.i(TAG, "Testing Hidden");
                            Log.i(TAG, "refreshMessages: " + hiddenBy);
                            Log.i(TAG, "refreshMessages: " + localID);


                            if (hiddenBy == null || !hiddenBy.equals(localID)) {

                                Message message = documentSnapshot.toObject(Message.class);
                                newMessages.add(message);
                                Log.i(TAG, "Adding this message:" + message.getBody());
                            }
                        }

                        for (Message message : newMessages) {

                            Log.i(TAG, "new message: " + message.getBody());
                        }
                        Log.i(TAG, "--------------------------- ");
                        updateMessages(newMessages);


                    }
                });
    }


    public String getUserRealName(String fieldPath, final String userID) {

        String collectionPath = fieldPath.equals(TUTOR_ID_PATH) ? Tutor.PATH : Student.PATH;


        Log.i(TAG, "getUserRealName: " + collectionPath);
        Log.i(TAG, "getUserRealName: " + userID);
        FirebaseFirestore database = FirebaseFirestore.getInstance();


        final StringBuilder userRealName = new StringBuilder();
        //has to be an array in order to be changed within
        //inner CompleteListener Class

        database.collection(collectionPath).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    userRealName.append(task.getResult().getString(User.KEY_FIRSTNAME));
                    userRealName.append("   ");
                    userRealName.append(task.getResult().getString(User.KEY_LASTNAME));
                    Log.i(TAG, "getUserRealName: " + userRealName);

                    if (userRealName.indexOf("null") == NO_USER_FOUND) {
                        tvOpponent.setText(userRealName);
                    }
                }
            }
        });

        return userRealName.toString();


    }


    private void showLatexDialog() {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.latextips);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(image);
        builder.create().show();
    }

    private void startProfileFragment() {

        String path = remoteField.equals(TUTOR_ID_PATH) ? Tutor.PATH : Student.PATH;

        findViewById(R.id.rlMessages).setVisibility(View.GONE);
        Fragment fragment = new ProfileFragment(remoteID, path);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_right, R.anim.enter_from_left, R.anim.exit_from_left);
        transaction.replace(R.id.fl_container, fragment);
        transaction.commit();

    }
}
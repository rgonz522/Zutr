package com.example.zutr.qbChatVideo;

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
import android.widget.TextView;

import com.example.zutr.R;
import com.example.zutr.SessionsAdapter;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatMessagingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatMessagingFragment extends Fragment {

    EditText etMessage;
    TextView tvMessages;
    Button btnSendMessage;


    QBChatDialog currentchatDialog;
    VideoAuth mvauth;


    public static final String TAG = "ChatMessagingFragment";

    public ChatMessagingFragment() {

        mvauth = VideoAuth.getInstance(getContext());

        // Required empty public constructor
    }


    public static ChatMessagingFragment newInstance() {
        ChatMessagingFragment fragment = new ChatMessagingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createChat(116348506);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_zutr, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, "onClick: is currentChatDialog empty? " + (currentchatDialog.toString()));
                sendMessage(etMessage.getText().toString(), currentchatDialog);

                updateUI();

            }
        });


    }

    public void createChat(int opponentID) {

        ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
        occupantIdsList.add(opponentID);

        QBChatDialog dialog = new QBChatDialog();
        dialog.setType(QBDialogType.PRIVATE);
        dialog.setOccupantsIds(occupantIdsList);

        // or just use DialogUtils
        //
        currentchatDialog = DialogUtils.buildPrivateDialog(opponentID);

        Log.i(TAG, "createChat: ");

       /* QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle params) {
                currentchatDialog = result;

                Log.i(TAG, "onSuccess: created chat and got ChatDialog" + result);
            }

            @Override
            public void onError(QBResponseException responseException) {
                Log.e(TAG, "onError: could not start chat", responseException);
            }
        });*/

    }


    public void receiveMessagesAllDialogs() {


        QBChatService chatService = QBChatService.getInstance();
        QBIncomingMessagesManager incomingMessagesManager = chatService.getIncomingMessagesManager();

        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String dialogID, QBChatMessage qbChatMessage, Integer senderID) {

            }

            @Override
            public void processError(String dialogID, QBChatException e, QBChatMessage qbChatMessage, Integer senderID) {

            }
        });
    }

    public void receiveMessagesSingleDialog(QBChatDialog privateDialog) {

        privateDialog.addMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String dialogId, QBChatMessage qbChatMessage, Integer senderId) {

            }

            @Override
            public void processError(String dialogId, QBChatException e, QBChatMessage qbChatMessage, Integer senderId) {

            }
        });
    }


    public List<QBChatMessage> getMessages(QBChatDialog qbChatDialog) {


        List<QBChatMessage> qbChatMessages = new ArrayList<>();


        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(100);

        // If retrieve messages using filtering:
        //messageGetBuilder.gte("date_sent", "508087800");
        //messageGetBuilder.lte("date_sent", "1170720000");
        //messageGetBuilder.markAsRead(false);

        QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                qbChatMessages.addAll(qbChatMessages);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        return qbChatMessages;
    }


    public List<QBChatDialog> getDialogs() {


        List<QBChatDialog> convos = new ArrayList<>();

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(50);


        //requestBuilder.setSkip(100);
        //requestBuilder.sortAsc("last_message_date_sent");

        QBRestChatService.getChatDialogs(null, requestBuilder)
                .performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
                    @Override
                    public void onSuccess(ArrayList<QBChatDialog> result, Bundle params) {
                        convos.addAll(result);

                        Log.i(TAG, "onSuccess: retrieved all convos: " + result);
                    }

                    @Override
                    public void onError(QBResponseException responseException) {
                        Log.e(TAG, "onError: could not retrieve convo", responseException);
                    }
                });

        return convos;
    }


    public void sendMessage(String messageBody, QBChatDialog dialog) {

        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageBody);
        chatMessage.setSaveToHistory(true);

        // If you want to use this feature without callbacks:
        //try{
        //    privateDialog.sendMessage(chatMessage);
        //} catch (SmackException.NotConnectedException e) {
        //
        //}

        dialog.sendMessage(chatMessage, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    public void updateUI() {


        for (QBChatMessage message : getMessages(currentchatDialog)) {

            String currentMessages = tvMessages.getText().toString();

            tvMessages.setText(currentMessages + " \n" + message.getBody());
        }

    }
}
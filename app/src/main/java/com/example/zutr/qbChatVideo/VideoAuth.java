package com.example.zutr.qbChatVideo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import com.example.zutr.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;

import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.StoringMechanism;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.webrtc.ContextUtils.getApplicationContext;

public class VideoAuth {

    public static final String TAG = "VideoAuthActivity";

    private final String UNIVERSAL_KEY = "1234568";
    private final int UNIVERSAL_ID = 116900280;
    private static VideoAuth videoAuthActivity = null;
    private static Context context;


    String applicationID;
    String authKey;
    String authSecret;
    String accountKey;

    QBUser currentUser;

    FirebaseUser currentFireUser;

    QBRTCClient rtcClient;
    QBChatService chatService;

    private static final String API_DOMAIN = "https://apicustomdomain.quickblox.com";       //try empty fields
    private static final String CHAT_DOMAIN = "chatcustomdomain.quickblox.com";


    private VideoAuth() {


        destroySession();
        //enable Carbon copies for multidevice support
       /* try {
            QBChatService.getInstance().enableCarbons();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
            */


        applicationID = context.getString(R.string.applicationID);
        authKey = context.getString(R.string.authorizationKey);
        authSecret = context.getString(R.string.authorizationSecret);
        accountKey = context.getString(R.string.accountKey);


        Log.i(TAG, "VideoAuth: " + QBSettings.getInstance().init(context, applicationID, authKey, authSecret).toString());
        QBSettings.getInstance().setAccountKey(accountKey);
        QBSettings.getInstance().setStoringMehanism(StoringMechanism.UNSECURED);
        QBSettings.getInstance().setEndpoints(API_DOMAIN, CHAT_DOMAIN, ServiceZone.PRODUCTION);
        QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);


        currentFireUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = new QBUser();
        currentUser.setPassword(UNIVERSAL_KEY);
        currentUser.setId(UNIVERSAL_ID);
        currentUser.setEmail("dummy10@gmail.com");


        loginUser(currentUser);


        connectChatServer();

        addConnectionListeners();


        QBChatService.getInstance().setReconnectionAllowed(true);


        loginToChat(currentUser);
        Log.i(TAG, "VideoAuth: right afterLoginIntoChat ");
       /* QBSessionManager.getInstance().addListener(new QBSessionManager.QBSessionListener() {
            @Override
            public void onSessionCreated(QBSession session) {
                // calls when session was created firstly or after it has been expired
            }

            @Override
            public void onSessionUpdated(QBSessionParameters sessionParameters) {
                // calls when user signed in or signed up
                // QBSessionParameters stores information about signed in user.
            }

            @Override
            public void onSessionDeleted() {
                // calls when user signed Out or session was deleted
            }

            @Override
            public void onSessionRestored(QBSession session) {
                // calls when session was restored from local storage
            }

            @Override
            public void onSessionExpired() {
                // calls when session is expired
            }

            @Override
            public void onProviderSessionExpired(String provider) {
                // calls when provider's access token is expired or invalid
            }
        });


        // CREATE SESSION WITH USER
        // If you use create session with user data,
        // then the user will be logged in automatically
      QBAuth.createSession(currentUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {

                currentUser.setId(session.getUserId());

                // INIT CHAT SERVICE
                chatService = QBChatService.getInstance();

                // LOG IN CHAT SERVICE
                chatService.login(currentUser, new QBEntityCallback<QBUser>() {

                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e(TAG, "onError: chat login", errors );
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {
                //error
            }
        });

*/


        Log.i(TAG, "VideoAuth: are we logged into Session" + isLoggedIn());
    }


    public boolean isLoggedIn() {

        return QBSessionManager.getInstance().getSessionParameters() != null;
    }


    public void signUpUser(String login, String password) {
        final QBUser user = new QBUser();
        user.setLogin(login);
        user.setPassword(password);

        QBUsers.signUp(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

                Log.i(TAG, "onSuccess: ");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });
    }

    public void loginUser(String login, String password) {

        final QBUser user = new QBUser();
        user.setLogin(login);
        user.setPassword(password);

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.i(TAG, "onSuccess: login ");
            }

            @Override
            public void onError(QBResponseException error) {
                Log.e(TAG, "onError: ", error);
            }
        });
    }

    public void loginUser(QBUser user) {

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.i(TAG, "onSuccess: logged in user");
            }

            @Override
            public void onError(QBResponseException error) {
                Log.e(TAG, "onError: logged in user ", error);
            }
        });
    }

    private void logout() {
        if (rtcClient != null) {
            rtcClient.destroy();
        }

        if (chatService != null) {
            chatService.logout(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    Log.d(TAG, "Logout Successful");
                    chatService.destroy();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d(TAG, "Logout Error: " + e.getMessage());
                    chatService.destroy();
                }
            });
        }

    }


    public void updateEmail(String email, String password) {

        QBUser user = new QBUser();
        user.setEmail(email);
        user.setPassword(password);

        QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }


    public static VideoAuth getInstance(Context paramcontext) {

        context = paramcontext;

        if (videoAuthActivity == null) {
            videoAuthActivity = new VideoAuth();
        }

        return videoAuthActivity;
    }


    public void connectChatServer() {
        if (chatService == null) {
            // Chat connection configuration
            QBChatService.ConfigurationBuilder configurationBuilder = new QBChatService.ConfigurationBuilder();
            configurationBuilder.setSocketTimeout(300);
            configurationBuilder.setUseTls(true);
            configurationBuilder.setKeepAlive(true);
            configurationBuilder.setAutojoinEnabled(false);
            configurationBuilder.setAutoMarkDelivered(true);
            configurationBuilder.setReconnectionAllowed(true);
            configurationBuilder.setAllowListenNetwork(true);
            configurationBuilder.setPort(5223);

            QBChatService.setConfigurationBuilder(configurationBuilder);

            chatService = QBChatService.getInstance();
            Log.i(TAG, "connectChatServer: " + chatService.getUser());

        }
    }

    private void initQBRTCClient() {


        rtcClient = QBRTCClient.getInstance(getApplicationContext());
        // Add signalling manager
        chatService.getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    rtcClient.addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });

        // Configure
        QBRTCConfig.setDebugEnabled(true);


        // Add service as callback to RTCClient

        rtcClient.prepareToProcessCalls();

        addClientListeners();
    }

    private void addClientListeners() {
        rtcClient.addSessionCallbacksListener(new QBRTCClientSessionCallbacks() {
            @Override
            public void onReceiveNewSession(QBRTCSession qbrtcSession) {

            }

            @Override
            public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

            }

            @Override
            public void onSessionStartClose(QBRTCSession qbrtcSession) {

            }

            @Override
            public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {

            }

            @Override
            public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

            }

            @Override
            public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

            }

            @Override
            public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

            }

            @Override
            public void onSessionClosed(QBRTCSession qbrtcSession) {

            }
        });


    }

    private void loginToChat(QBUser user) {

        QBAuth.createSession(user).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success, login to chat

                Log.i(TAG, "onSuccess: create session ");
                user.setId(session.getUserId());

                chatService.login(user, new QBEntityCallback() {

                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        Log.i(TAG, "onSuccess: log in to chat");
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e(TAG, "onError: login to chat ", errors);
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e(TAG, "onError: create session ", errors);
            }

        });


    }

    public QBRTCClient getRtcClient() {
        return rtcClient;
    }

    public QBChatService getChatService() {
        return chatService;
    }

    public void addConnectionListeners() {
        ConnectionListener connectionListener = new ConnectionListener() {
            @Override
            public void connected(XMPPConnection xmppConnection) {
                Log.i(TAG, "connected: ");
            }

            @Override
            public void authenticated(XMPPConnection xmppConnection, boolean b) {
                Log.i(TAG, "authenticated: ");
            }

            @Override
            public void connectionClosed() {
                Log.i(TAG, "connectionClosed: ");
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.e(TAG, "connectionClosedOnError: ", e);
            }

            @Override
            public void reconnectionSuccessful() {
                Log.i(TAG, "reconnectionSuccessful: ");
            }

            @Override
            public void reconnectingIn(int i) {
                Log.i(TAG, "reconnectingIn: ");
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.e(TAG, "reconnectionFailed: ", e);
            }
        };

        chatService.addConnectionListener(connectionListener);
    }


    public void destroySession() {

        QBAuth.deleteSession().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

}
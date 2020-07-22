package com.example.zutr.qbChatVideo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.zutr.R;
import com.quickblox.chat.QBChatService;

import com.quickblox.videochat.webrtc.BaseSession;
import com.quickblox.videochat.webrtc.QBMediaStreamManager;
import com.quickblox.videochat.webrtc.QBRTCAudioTrack;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCMediaConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientAudioTracksCallback;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;

import com.quickblox.videochat.webrtc.view.QBRTCSurfaceView;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VideoChatActivity extends AppCompatActivity {

    public static final String RECEIVER_KEY = "opponent";
    public static final int DEFAULT_OPP = 404;

    public static final String TAG = "VideoChatActivity";

    QBRTCSurfaceView remoteView;
    QBRTCSurfaceView localView;

    QBRTCSession rtcSession;
    QBRTCClient rtcClient;
    QBChatService chatService;

    QBRTCVideoTrack videoTrack;

    VideoAuth mVidAuth;

    EglBase eglContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);


        mVidAuth = VideoAuth.getInstance(this);
        chatService = mVidAuth.getChatService();
        rtcClient = mVidAuth.getRtcClient();
        // Add signalling manager

        int opponentID = getIntent().getIntExtra(RECEIVER_KEY, DEFAULT_OPP);


        //Video Call
        QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;


        // Init session
        rtcSession = QBRTCClient.getInstance(getApplicationContext()).createNewSessionWithOpponents(Collections.singletonList(opponentID), qbConferenceType);
        // Start call


        QBMediaStreamManager mediaStreamManager = rtcSession.getMediaStreamManager();

        mediaStreamManager.setAudioEnabled(true); // enable audio stream
        mediaStreamManager.setVideoEnabled(true); // enable video stream


        QBRTCVideoTrack localVideoTrack = mediaStreamManager.getLocalVideoTrack();
        localVideoTrack.setEnabled(true); // enable video stream

        QBRTCAudioTrack localAudioTrack = mediaStreamManager.getLocalAudioTrack();
        localAudioTrack.setEnabled(true); // enable or disable audio stream


        configureMediaSettings();
        startCall();


        // Set how the video will fill the allowed layout area
        remoteView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        localView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);


        videoTrack.addRenderer(remoteView);

        rtcSession.addVideoTrackCallbacksListener(new QBRTCClientVideoTracksCallbacks() {
            @Override
            public void onLocalVideoTrackReceive(BaseSession baseSession, QBRTCVideoTrack qbrtcVideoTrack) {
                fillVideoView(localView, qbrtcVideoTrack);
                videoTrack.addRenderer(localView);
                Log.i(TAG, "onLocalVideoTrackReceive: filledLocalVideo");
            }

            @Override
            public void onRemoteVideoTrackReceive(BaseSession baseSession, QBRTCVideoTrack qbrtcVideoTrack, Integer integer) {
                fillVideoView(remoteView, qbrtcVideoTrack);
                Log.i(TAG, "onLocalVideoTrackReceive: filledRemoteWork");
            }
        });


        rtcSession.addAudioTrackCallbacksListener(new QBRTCClientAudioTracksCallback() {
            @Override
            public void onLocalAudioTrackReceive(BaseSession baseSession, QBRTCAudioTrack qbrtcAudioTrack) {

            }

            @Override
            public void onRemoteAudioTrackReceive(BaseSession baseSession, QBRTCAudioTrack qbrtcAudioTrack, Integer integer) {

            }
        });


    }

    private void configureMediaSettings() {
        QBRTCMediaConfig.setAudioCodec(QBRTCMediaConfig.AudioCodec.ISAC);
        QBRTCMediaConfig.setAudioCodec(QBRTCMediaConfig.AudioCodec.OPUS);

        QBRTCMediaConfig.setVideoCodec(QBRTCMediaConfig.VideoCodec.H264);
        QBRTCMediaConfig.setVideoCodec(QBRTCMediaConfig.VideoCodec.VP8);
        QBRTCMediaConfig.setVideoCodec(QBRTCMediaConfig.VideoCodec.VP9);

        int audioStartBitrate = 80;
        QBRTCMediaConfig.setAudioStartBitrate(audioStartBitrate);

        int videoStartBitrate = 80;
        QBRTCMediaConfig.setVideoStartBitrate(videoStartBitrate);

        int videoWidth = 80;
        QBRTCMediaConfig.setVideoWidth(videoWidth);
        int videoHeight = 80;
        QBRTCMediaConfig.setVideoHeight(videoHeight);

        // Enable Hardware Acceleration if device supports it
        QBRTCMediaConfig.setVideoHWAcceleration(true);

        // Set frames-per-second in transmitting video stream
        int videoFPS = 80;
        QBRTCMediaConfig.setVideoFps(videoFPS);

        // Enable built-in AEC if device supports it
        QBRTCMediaConfig.setUseBuildInAEC(true);

        // Enable OpenSL ES audio if device supports it
        QBRTCMediaConfig.setUseOpenSLES(true);

        QBRTCMediaConfig.setAudioProcessingEnabled(true);
    }


    public void startCall() {
        // Create collection of opponents ID


        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("key", "value");
        rtcSession.startCall(userInfo);

        // You can set any string key and value in user info
        // Then retrieve this data from sessions which is returned in callbacks
        // and parse them as you wish

        // There are two call types: Audio or Video Call
        // Audio call:  QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
        // or

    }

    private void fillVideoView(QBRTCSurfaceView videoView, QBRTCVideoTrack videoTrack) {
        // To remove renderer if Video Track already has another one
        videoTrack.cleanUp();

        if (videoView != null) {
            videoTrack.addRenderer(videoView);
            updateVideoView(videoView);
        }
    }

    private void updateVideoView(SurfaceViewRenderer videoView) {
        RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
        videoView.setScalingType(scalingType);
        videoView.setMirror(false);
        videoView.requestLayout();
    }

    private void endCall() {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("key", "value");

        rtcSession.hangUp(userInfo);
    }

    public void loggedOut() {
        QBRTCClient.getInstance(this).destroy();
    }


}
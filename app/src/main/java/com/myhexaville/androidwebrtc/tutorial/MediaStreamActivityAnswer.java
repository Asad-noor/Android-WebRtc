package com.myhexaville.androidwebrtc.tutorial;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.myhexaville.androidwebrtc.R;
import com.myhexaville.androidwebrtc.databinding.ActivitySamplePeerConnectionBinding;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;

import static com.myhexaville.androidwebrtc.app_rtc_sample.web_rtc.PeerConnectionClient.VIDEO_TRACK_ID;

/*
 * Shows how to use PeerConnection to connect clients and stream video using MediaStream
 * without any networking
 * */
public class MediaStreamActivityAnswer extends AppCompatActivity {
    private static final String TAG = " tttt";
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";

    private ActivitySamplePeerConnectionBinding binding;
    private EglBase rootEglBase;
    private VideoTrack videoTrackFromCamera;
    private PeerConnectionFactory factory;
    private PeerConnection remotePeerConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sample_peer_connection);

        initializeSurfaceViews();

        initializePeerConnectionFactory();

        createVideoTrackFromCameraAndShowIt();

        initializePeerConnections();

        startStreamingVideo();
    }

    private void initializeSurfaceViews() {
        rootEglBase = EglBase.create();
        binding.surfaceView.init(rootEglBase.getEglBaseContext(), null);
        binding.surfaceView.setEnableHardwareScaler(true);
        binding.surfaceView.setMirror(true);

        binding.surfaceView2.init(rootEglBase.getEglBaseContext(), null);
        binding.surfaceView2.setEnableHardwareScaler(true);
        binding.surfaceView2.setMirror(true);
    }

    private void initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        factory = new PeerConnectionFactory(null);
        factory.setVideoHwAccelerationOptions(rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());
    }

    private void createVideoTrackFromCameraAndShowIt() {
        VideoCapturer videoCapturer = createVideoCapturer();
        VideoSource videoSource = factory.createVideoSource(videoCapturer);
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrackFromCamera.setEnabled(true);
        videoTrackFromCamera.addRenderer(new VideoRenderer(binding.surfaceView));
    }

    private void initializePeerConnections() {
        //localPeerConnection = createPeerConnection(factory, true);
        remotePeerConnection = createPeerConnection(factory, false);
    }

    private void startStreamingVideo() {
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(videoTrackFromCamera);
        remotePeerConnection.addStream(mediaStream);

        MediaConstraints sdpMediaConstraints = new MediaConstraints();

//        localPeerConnection.createOffer(new SimpleSdpObserver() {
//            @Override
//            public void onCreateSuccess(SessionDescription sessionDescription) {
//                Log.d(TAG, "onCreateSuccess: ");
//                localPeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
//
//                Log.d(TAG, "onCreateSuccess: des >"+sessionDescription.description);
//                Log.d(TAG, "onCreateSuccess: type >"+sessionDescription.type);


                //remotePeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);

                remotePeerConnection.createAnswer(new SimpleSdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {

                        //localPeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);

                        SessionDescription sessionDescriptionRemote = new SessionDescription(SessionDescription.Type.ANSWER,
                                "v=0\n" +
                                        "    o=- 1046850356470366532 2 IN IP4 127.0.0.1\n" +
                                        "    s=-\n" +
                                        "    t=0 0\n" +
                                        "    a=group:BUNDLE video\n" +
                                        "    a=msid-semantic: WMS ARDAMS\n" +
                                        "    m=video 9 UDP/TLS/RTP/SAVPF 96 98 100 127 125 97 99 101 104\n" +
                                        "    c=IN IP4 0.0.0.0\n" +
                                        "    a=rtcp:9 IN IP4 0.0.0.0\n" +
                                        "    a=ice-ufrag:63Zt\n" +
                                        "    a=ice-pwd:g8zy62yMdm0RpJ7kfkAKMA+t\n" +
                                        "    a=ice-options:renomination\n" +
                                        "    a=fingerprint:sha-256 22:68:31:40:B0:1B:CA:66:97:FE:04:9C:13:4E:05:25:F7:6A:FD:E5:A2:5A:BC:BA:FE:24:40:98:CA:2F:75:6D\n" +
                                        "    a=setup:actpass\n" +
                                        "    a=mid:video\n" +
                                        "    a=extmap:2 urn:ietf:params:rtp-hdrext:toffset\n" +
                                        "    a=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\n" +
                                        "    a=extmap:4 urn:3gpp:video-orientation\n" +
                                        "    a=extmap:5 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\n" +
                                        "    a=extmap:6 http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\n" +
                                        "    a=sendrecv\n" +
                                        "    a=rtcp-mux\n" +
                                        "    a=rtcp-rsize\n" +
                                        "    a=rtpmap:96 VP8/90000\n" +
                                        "    a=rtcp-fb:96 ccm fir\n" +
                                        "    a=rtcp-fb:96 nack\n" +
                                        "    a=rtcp-fb:96 nack pli\n" +
                                        "    a=rtcp-fb:96 goog-remb\n" +
                                        "    a=rtcp-fb:96 transport-cc\n" +
                                        "    a=rtpmap:98 VP9/90000\n" +
                                        "    a=rtcp-fb:98 ccm fir\n" +
                                        "    a=rtcp-fb:98 nack\n" +
                                        "    a=rtcp-fb:98 nack pli\n" +
                                        "    a=rtcp-fb:98 goog-remb\n" +
                                        "    a=rtcp-fb:98 transport-cc\n" +
                                        "    a=rtpmap:100 red/90000\n" +
                                        "    a=rtpmap:127 ulpfec/90000\n" +
                                        "    a=rtpmap:125 H264/90000\n" +
                                        "    a=rtcp-fb:125 ccm fir\n" +
                                        "    a=rtcp-fb:125 nack\n" +
                                        "    a=rtcp-fb:125 nack pli\n" +
                                        "    a=rtcp-fb:125 goog-remb\n" +
                                        "    a=rtcp-fb:125 transport-cc\n" +
                                        "    a=fmtp:125 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\n" +
                                        "    a=rtpmap:97 rtx/90000\n" +
                                        "    a=fmtp:97 apt=96\n" +
                                        "    a=rtpmap:99 rtx/90000\n" +
                                        "    a=fmtp:99 apt=98\n" +
                                        "    a=rtpmap:101 rtx/90000\n" +
                                        "    a=fmtp:101 apt=100\n" +
                                        "    a=rtpmap:104 rtx/90000\n" +
                                        "    a=fmtp:104 apt=125\n" +
                                        "    a=ssrc-group:FID 2366995235 3650127275\n" +
                                        "    a=ssrc:2366995235 cname:WqQb5SO0jdUa9pRJ\n" +
                                        "    a=ssrc:2366995235 msid:ARDAMS ARDAMSv0\n" +
                                        "    a=ssrc:2366995235 mslabel:ARDAMS\n" +
                                        "    a=ssrc:2366995235 label:ARDAMSv0\n" +
                                        "    a=ssrc:3650127275 cname:WqQb5SO0jdUa9pRJ\n" +
                                        "    a=ssrc:3650127275 msid:ARDAMS ARDAMSv0\n" +
                                        "    a=ssrc:3650127275 mslabel:ARDAMS\n" +
                                        "    a=ssrc:3650127275 label:ARDAMSv0");

                        remotePeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescriptionRemote);
                    }
                }, sdpMediaConstraints);
          //  }
        //}, sdpMediaConstraints);
    }

    private PeerConnection createPeerConnection(PeerConnectionFactory factory, boolean isLocal) {
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(new ArrayList<>());
        MediaConstraints pcConstraints = new MediaConstraints();

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: ");
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                //Log.d(TAG, "onIceCandidate: >" + iceCandidate);
                //if (isLocal) {
                    IceCandidate candidate = new IceCandidate("video", 0,
                            "candidate:2131708102 1 udp 2122260223 192.168.0.100 51678 typ host generation 0 ufrag 63Zt network-id 3 network-cost 10");
                    remotePeerConnection.addIceCandidate(candidate);
//                } else {
//                    localPeerConnection.addIceCandidate(iceCandidate);
//                }
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream: " + mediaStream.videoTracks.size());
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                remoteVideoTrack.setEnabled(true);
                remoteVideoTrack.addRenderer(new VideoRenderer(binding.surfaceView2));

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }
        };

        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    /*
     * Read more about Camera2 here
     * https://developer.android.com/reference/android/hardware/camera2/package-summary.html
     * */
    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this);
    }

}
package com.myhexaville.androidwebrtc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.myhexaville.androidwebrtc.app_rtc_sample.main.AppRTCMainActivity;
import com.myhexaville.androidwebrtc.tutorial.CameraRenderActivity;
import com.myhexaville.androidwebrtc.tutorial.CompleteActivity;
import com.myhexaville.androidwebrtc.tutorial.DataChannelActivity;
import com.myhexaville.androidwebrtc.tutorial.MediaStreamActivity;
import com.myhexaville.androidwebrtc.tutorial.MediaStreamActivityAnswer;
import com.myhexaville.androidwebrtc.tutorial.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.PeerConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class LauncherActivity extends AppCompatActivity {

    private ArrayList<PeerConnection.IceServer> turnServers;
    private static final int TURN_HTTP_TIMEOUT_MS = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        turnServers = new ArrayList<>();
        //turnServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        send("https://networktraversal.googleapis.com/v1alpha/iceconfig?key=AIzaSyAJdh2HkajseEIltlZ3SIXO02Tze9sO3NY");
    }

    public void openAppRTCActivity(View view) {
        startActivity(new Intent(this, AppRTCMainActivity.class));
    }

    public void openSampleActivity(View view) {
        startActivity(new Intent(this, CameraRenderActivity.class));
    }

    public void openSamplePeerConnectionOffer(View view) {
        startActivity(new Intent(this, MediaStreamActivity.class));
    }

    public void openSamplePeerConnectionAnswer(View view) {
        startActivity(new Intent(this, MediaStreamActivityAnswer.class));
    }

    public void openSampleDataChannelActivity(View view) {
        startActivity(new Intent(this, DataChannelActivity.class));
    }

    public void openSampleSocketActivity(View view) {
        startActivity(new Intent(this, CompleteActivity.class));
    }



    private void send(final String url) {
        Runnable runHttp = new Runnable() {
            @Override
            public void run() {
                try {
                    requestTurnServers(url);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runHttp).start();
    }

    private void requestTurnServers(String url)
            throws IOException, JSONException {
        //LinkedList<PeerConnection.IceServer> turnServers = new LinkedList<PeerConnection.IceServer>();
        //Log.d(TAG, "Request TURN from: " + url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("REFERER", "https://appr.tc");
        connection.setConnectTimeout(TURN_HTTP_TIMEOUT_MS);
        connection.setReadTimeout(TURN_HTTP_TIMEOUT_MS);
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Non-200 response when requesting TURN server from " + url + " : "
                    + connection.getHeaderField(null));
        }
        InputStream responseStream = connection.getInputStream();
        String response = drainStream(responseStream);
        connection.disconnect();
        Log.d("tttt", "TURN response: " + response);
        JSONObject responseJSON = new JSONObject(response);
        JSONArray iceServers = responseJSON.getJSONArray("iceServers");
        for (int i = 0; i < iceServers.length(); ++i) {
            JSONObject server = iceServers.getJSONObject(i);
            JSONArray turnUrls = server.getJSONArray("urls");
            Log.d("tttt", "urls >"+turnUrls.toString());
            String username = server.has("username") ? server.getString("username") : "";
            String credential = server.has("credential") ? server.getString("credential") : "";
            for (int j = 0; j < turnUrls.length(); j++) {
                String turnUrl = turnUrls.getString(j);
                Log.d("tttt", " single url > "+turnUrl);
                turnServers.add(new PeerConnection.IceServer(turnUrl, username, credential));
            }
        }

        Util.setServers(turnServers);
        //return turnServers;
    }

    // Return the contents of an InputStream as a String.
    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}

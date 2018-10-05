package com.myhexaville.androidwebrtc.tutorial;

import org.webrtc.PeerConnection;

import java.util.ArrayList;

public class Util {

    private static ArrayList<PeerConnection.IceServer> turnServers = new ArrayList<>();

    public static void setServers(ArrayList<PeerConnection.IceServer> turServers){
        turnServers.addAll(turServers);
    }

    public static ArrayList<PeerConnection.IceServer> getTurnServers(){
        return turnServers;
    }
}

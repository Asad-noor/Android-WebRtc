package com.myhexaville.androidwebrtc;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class RtcApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this);
    }
}

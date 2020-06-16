package com.imagine.myapplication;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.out.println("OLLAH!");
    }
}

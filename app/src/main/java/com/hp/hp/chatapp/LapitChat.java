package com.hp.hp.chatapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
//for offline services  PART 20
public class LapitChat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

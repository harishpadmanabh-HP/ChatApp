package com.hp.hp.chatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

public class Firebaseidservice extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        System.out.println("+++++++++++++++++++++++++++++++++++++"+refreshedToken);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("userid", refreshedToken).commit();

        SharedPreferences  sharedpreferencesDeviceid = getSharedPreferences("sharedpreferencesDeviceid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferencesDeviceid.edit();

        editor.putString("DeviceID", refreshedToken);

        editor.commit();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {

    }
}

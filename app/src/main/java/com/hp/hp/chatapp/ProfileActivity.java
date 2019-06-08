package com.hp.hp.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    TextView mDisplayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       String userid= getIntent().getStringExtra("userid");
        mDisplayId=findViewById(R.id.profiledisplayname);
        mDisplayId.setText(userid);
    }
}

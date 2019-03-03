package com.hp.hp.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
private Toolbar mToolbar;
private TextInputLayout mstatus;
private Button mSavebtn;
private DatabaseReference mStatusDatabase;
private FirebaseUser mcurrentuser;
private ProgressDialog mprogressdiaolog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mcurrentuser=FirebaseAuth.getInstance().getCurrentUser();
        String currentuid=mcurrentuser.getUid();
        mStatusDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(currentuid);


        mToolbar=findViewById(R.id.statusappbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ACCOUNT STATUS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String statusvalue=getIntent().getStringExtra("statusvalue");

        mstatus=findViewById(R.id.statusinput);
        mSavebtn=findViewById(R.id.savebutn);
//setcurrent status value
        mstatus.getEditText().setText(statusvalue);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mprogressdiaolog=new ProgressDialog(StatusActivity.this);

                mprogressdiaolog.setTitle("Updating Status");

                mprogressdiaolog.show();

                String status=mstatus.getEditText().getText().toString();
               //update status
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       mprogressdiaolog.dismiss();
                   }else {
                       Toast.makeText(StatusActivity.this, "error", Toast.LENGTH_SHORT).show();
                   }
                    }
                });
            }
        });



    }
}

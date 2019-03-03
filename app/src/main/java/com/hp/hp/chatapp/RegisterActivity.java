package com.hp.hp.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.widget.Toast.*;

public class RegisterActivity extends AppCompatActivity {
private TextInputLayout mDispalyname,mEmail,mPassword;
private Button mCreateBtn;
//auth
    private FirebaseAuth mAuth;
    DatabaseReference database;
    private android.support.v7.widget.Toolbar mToolbar;

    //progreess

    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolbar=findViewById(R.id.regtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SIGN UP");//set title to toolbar
        //set up button in toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

mProgressDialog=new ProgressDialog(this);
        //auth
        mAuth = FirebaseAuth.getInstance();
        mDispalyname=(TextInputLayout) findViewById(R.id.textInputLayout);
        mEmail=(TextInputLayout)findViewById(R.id.textInputLayout2);
        mPassword=(TextInputLayout)findViewById(R.id.textInputLayout3);
        mCreateBtn=findViewById(R.id.regcreatebtn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayname=mDispalyname.getEditText().getText().toString();
                String email=mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();
                //check null
                if(!TextUtils.isEmpty(displayname)|| !TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)) {
                    mProgressDialog.setTitle("REGISTERING");
                    mProgressDialog.setMessage("PLEASE WAIT ! ");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    reister_user(displayname, email, password);
                }


            }
        });

    }

    private void reister_user(final String displayname, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentuser=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=currentuser.getUid();

                            //database
                            database = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String> userMap=new HashMap<>();
                            userMap.put("name",displayname);
                            userMap.put("status","Hi there !!!");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");
                            database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgressDialog.dismiss();
                            Intent mainintent=new Intent(RegisterActivity.this,MainActivity.class);
                            mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(mainintent);
                            finish();
                                    }
                                }
                            });








                        } else {
                            mProgressDialog.hide();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "error!", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}

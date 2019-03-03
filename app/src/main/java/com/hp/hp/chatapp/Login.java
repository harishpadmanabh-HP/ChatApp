package com.hp.hp.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextInputLayout mEmail,mPassword;
    private Button mLogin;
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mToolbar=findViewById(R.id.logintoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("LOG IN");//set title to toolbar
        //set up button in toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog=new ProgressDialog(this);

        mEmail=(TextInputLayout)findViewById(R.id.textInputLayout2);
        mPassword=(TextInputLayout)findViewById(R.id.textInputLayout3);
        mLogin=findViewById(R.id.login);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();
                  if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password))
                  {
                      mProgressDialog.setTitle("SIGNING IN");
                      mProgressDialog.setMessage("PLEASE WAIT ! ");
                      mProgressDialog.setCanceledOnTouchOutside(false);
                      mProgressDialog.show();
                      loginuser(email,password);
                  }

            }
        });


    }

    private void loginuser(String email, String password) {
   mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
       @Override
       public void onComplete(@NonNull Task<AuthResult> task) {

           if(task.isSuccessful())//user log in just now not already logged in
           {
               mProgressDialog.dismiss();
               Intent mainintent=new Intent(Login.this,MainActivity.class);
               mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

               startActivity(mainintent);
               finish();
           }
           else
           {               mProgressDialog.dismiss();

               Toast.makeText(Login.this, "error login", Toast.LENGTH_SHORT).show();
           }

       }
   });
    }
}

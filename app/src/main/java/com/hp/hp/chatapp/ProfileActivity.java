package com.hp.hp.chatapp;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class ProfileActivity extends AppCompatActivity {

    TextView mProfilename,profilestatus,profilefrndcount;
    ImageView imageViewprofile;
    Button  sendreq,declinereq;
    DatabaseReference mUsersDatabase,mFriendrequestDatabase,mFriendDatabase,mNotifydatabase;
    AlertDialog dialog;
    String mcurrent_state;
    FirebaseUser mCurrentuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String userid= getIntent().getStringExtra("userid");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mcurrent_state="not_friend";
        dialog = new SpotsDialog(ProfileActivity.this,"LOADING");
        dialog.show();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        mFriendrequestDatabase= FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");

        mNotifydatabase=FirebaseDatabase.getInstance().getReference().child("notification");
        mCurrentuser= FirebaseAuth.getInstance().getCurrentUser();

        imageViewprofile=findViewById(R.id.displayprofileimage);
        mProfilename=findViewById(R.id.profiledisplayname);
        profilestatus=findViewById(R.id.profiledisplaystatus);
        profilefrndcount=findViewById(R.id.profiledisplaytotalfrnds);
        sendreq=findViewById(R.id.sendreq);
        declinereq=findViewById(R.id.decliereq);
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayname=dataSnapshot.child("name").getValue().toString();
                String displaystatus=dataSnapshot.child("status").getValue().toString();
                String displayimage=dataSnapshot.child("image").getValue().toString();

                mProfilename.setText(displayname);
                profilestatus.setText(displaystatus);
                Glide
                        .with(ProfileActivity.this)
                        .load(displayimage)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(imageViewprofile);

                //friend list/reqfeature======================

                mFriendrequestDatabase.child(mCurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userid))
                        {
                             String reqtype=dataSnapshot.child(userid).child("request_type").getValue().toString();
                            if(reqtype.equalsIgnoreCase("recieved"))
                            {
                                //sent req to us
                                sendreq.setEnabled(true);
                                mcurrent_state="req_recieved";
                                sendreq.setText("Accept Friend Request");

                                declinereq.setVisibility(View.VISIBLE);
                                declinereq.setEnabled(true);

                            }else if(reqtype.equalsIgnoreCase("sent")){
                                sendreq.setEnabled(true);
                                mcurrent_state="req_sent";
                                sendreq.setText("Cancel Friend Request");
                                declinereq.setVisibility(View.INVISIBLE);
                                declinereq.setEnabled(false);

                            }


                        }else{
                            mFriendDatabase.child(mCurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(userid))
                                    {
                                        mcurrent_state="friends";
                                        sendreq.setText("Send Request");
                                        declinereq.setVisibility(View.INVISIBLE);
                                        declinereq.setEnabled(false);

                                        /*    <!-- TODO: Cancel request delete friend from friends table -->*/

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        dialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendreq.setEnabled(false);
                //not friends state============================
                if(mcurrent_state.equals("not_friend")){
                    mFriendrequestDatabase.child(mCurrentuser.getUid()).child(userid).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mFriendrequestDatabase.child(userid).child(mCurrentuser.getUid()).child("request_type")
                                                .setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                HashMap<String,String> notifydata=new HashMap<>();
                                                notifydata.put("from",mCurrentuser.getUid());
                                                notifydata.put("type","request");

                                                mNotifydatabase.child(userid).push().setValue(notifydata).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        sendreq.setEnabled(true);
                                                        mcurrent_state="req_sent";
                                                        sendreq.setText("Cancel Friend Request");
                                                        declinereq.setVisibility(View.INVISIBLE);
                                                        declinereq.setEnabled(false);
                                                    }
                                                });




                                                Toast.makeText(ProfileActivity.this, "Request sent !!!", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }else{
                                        Toast.makeText(ProfileActivity.this, "Failed sending request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                //cancel request==============sstate
                if(mcurrent_state.equals("req_sent")){
                    mFriendrequestDatabase.child(mCurrentuser.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendrequestDatabase.child(userid).child(mCurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendreq.setEnabled(true);
                                    mcurrent_state="not_friend";
                                    sendreq.setText("Send Friend Request");





                                    declinereq.setVisibility(View.INVISIBLE);
                                    declinereq.setEnabled(false);

                                }
                            });
                        }
                    });
                }
                //req recieved
                if(mcurrent_state.equalsIgnoreCase("req_recieved"))
                {
                    final String currentdate= DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentuser.getUid()).child(userid).setValue(currentdate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                       mFriendDatabase.child(userid).child(mCurrentuser.getUid()).setValue(currentdate)
                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                       mFriendrequestDatabase.child(mCurrentuser.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               mFriendrequestDatabase.child(userid).child(mCurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                   @Override
                                                   public void onSuccess(Void aVoid) {
                                                       sendreq.setEnabled(true);
                                                       mcurrent_state="friends";
                                                       sendreq.setText("UnFriend this Person");




                                                       declinereq.setVisibility(View.INVISIBLE);
                                                       declinereq.setEnabled(false);

                                                   }
                                               });
                                           }
                                       });
                                   }
                               });

                        }
                    });
                }
            }
        });



    }
}

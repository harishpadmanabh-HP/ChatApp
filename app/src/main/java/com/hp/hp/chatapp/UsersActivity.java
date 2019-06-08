package com.hp.hp.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hp.hp.chatapp.Models.Users;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
Toolbar mtoolbar;
RecyclerView muserslist;
DatabaseReference musersdb;
ArrayList<String> names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

names=new ArrayList<>();

        mtoolbar=findViewById(R.id.users_appbar);
        muserslist=findViewById(R.id.userslist);
        muserslist.setHasFixedSize(true);
        muserslist.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ALL USERS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


musersdb=FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {


            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_singlelayout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users users) {
                // Bind the Chat object to the ChatHolder
                // ...
                holder.setname(users.getName(),users.getStatus(),users.getImage());

                Glide
                        .with(UsersActivity.this)
                        .load(users.getImage())
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.profileimage);

                final String userid=getRef(position).getKey();


                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profile=new Intent(UsersActivity.this,ProfileActivity.class);

                        profile.putExtra("userid",userid);
                        startActivity(profile);
                    }
                });
            }

        };
adapter.startListening();
muserslist.setAdapter(adapter);

//        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class,R.layout.users_singlelayout,UsersViewHolder.class,musersdb) {
//            @Override
//            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
//
//            }
//
//            @NonNull
//            @Override
//            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                return null;
//            }
//        };

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
       View mview;
        TextView usernameView;
        TextView userstatus;
        ImageView profileimage;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
             usernameView=itemView.findViewById(R.id.usersinglename);
            userstatus=itemView.findViewById(R.id.usersinglestatus);
            profileimage=itemView.findViewById(R.id.usersingleimage);


        }


        public void setname(String name,String Status,String imgURL) {
usernameView.setText(name);
userstatus.setText(Status);

        }
    }
}

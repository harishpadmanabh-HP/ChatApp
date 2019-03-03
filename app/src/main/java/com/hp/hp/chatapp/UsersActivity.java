package com.hp.hp.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("Users")
//                .limitToLast(50);
//        FirebaseRecyclerOptions<Chat> options =
//                new FirebaseRecyclerOptions.Builder<Users>()
//                        .setQuery(query, Users.class)
//                        .build();
//
//FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
//
//
//            @Override
//            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                // Create a new instance of the ViewHolder, in this case we are using a custom
//                // layout called R.layout.message for each item
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.users_singlelayout, parent, false);
//
//                return new UsersViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model) {
//                // Bind the Chat object to the ChatHolder
//                // ...
//            }
//
//        };


    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            TextView textView=itemView.findViewById(R.id.usersinglename);
           // textView.setText();


        }
        public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

            private List<String> horizontalList;

            public class MyViewHolder extends RecyclerView.ViewHolder {
                public TextView txtView;

                public MyViewHolder(View view) {
                    super(view);
                    txtView = (TextView) view.findViewById(R.id.usersinglename);

                }
            }


            public HorizontalAdapter(List<String> horizontalList) {
                this.horizontalList = horizontalList;
            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_singlelayout, parent, false);

                return new MyViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(final MyViewHolder holder, final int position) {
                holder.txtView.setText(horizontalList.get(position));

                holder.txtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(UsersActivity.this,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return horizontalList.size();
            }
        }




    }
}

package com.hp.hp.chatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
   //check login status
    private FirebaseAuth mAuth;

    private android.support.v7.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private  SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //add toolbar
        mToolbar=findViewById(R.id.mainpagetoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("LAPIT CHAT APP");//set title to toolbar

//tabs
        mViewPager=findViewById(R.id.tabpager);
        //adapter class created
        mSectionPagerAdapter=new SectionPagerAdapter(getSupportFragmentManager());
        //set adapter
        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout=findViewById(R.id.maintabs);
        //set viewpager to tabs
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

       if(item.getItemId()==R.id.mainlogout)
       {
           //sign out
           FirebaseAuth.getInstance().signOut();
           sendtostart();
       }
        if(item.getItemId()==R.id.mainsettingsbtn)
        {
           Intent settingsintent=new Intent(MainActivity.this,SettingsActivity.class);
           startActivity(settingsintent);
        }
        if(item.getItemId()==R.id.mainallusers)
        {
            Intent users=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(users);
        }
         return true;
    }

    //for login status
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
      //current user
        //if user not signed in currentuser is ull
        FirebaseUser currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);
        if(currentUser==null)
        {//if user not signed in go to splash
            sendtostart();
             finish();
        }
    }

    private void sendtostart() {
        Intent startintent=new Intent(MainActivity.this,StartActivity.class);
        startActivity(startintent);
    }
}

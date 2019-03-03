package com.hp.hp.chatapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
//implement methods and constructor
class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                RequestsFragment requestsFragment=new RequestsFragment();
                return requestsFragment;

            case 1:
                ChatFragment chatFragment=new ChatFragment();
                return  chatFragment;

            case 2:
                FreiendsFragment freiendsFragment=new FreiendsFragment();
                return freiendsFragment;

            default: return null;
        }

        //return null;
    }

    @Override
    public int getCount() {
        //no of tabs
        return 3;
    }
    //set tab title
    public CharSequence getPageTitle(int position){

        switch (position)
        {
            case 0 :
                return "REQUESTS";
            case 1:
                return "CHAT";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }

    }
}

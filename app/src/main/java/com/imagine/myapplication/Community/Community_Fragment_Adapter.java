package com.imagine.myapplication.Community;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;

public class Community_Fragment_Adapter extends FragmentStateAdapter {

    public Activity activity;

    HashMap<String,String> args;
    public Community_Fragment_Adapter(@NonNull FragmentActivity fragmentActivity, HashMap<String,String> args) {
        super(fragmentActivity);
        this.args = args;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(args.get("displayOption").equals("fact")){
            if(position == 0){
                CommunityAddonsFragment fragmentThree = new CommunityAddonsFragment(args);
                return fragmentThree;
            } else if (position == 1){
                CommunityFactsFragment fragmentOne = new CommunityFactsFragment(args);
                return fragmentOne;
            } else if(position == 2) {
                CommunityFeedFragment fragmentTwo = new CommunityFeedFragment(args);
                fragmentTwo.activity = this.activity;
                return fragmentTwo;
            } else{
                return null;
            }
        } else {
            if(position == 0) {
                CommunityAddonsFragment fragment = new CommunityAddonsFragment(args);
                return fragment;
            }   else if ( position == 1){
                CommunityFeedFragment fragmentTwo = new CommunityFeedFragment(args);
                fragmentTwo.activity = this.activity;
                return fragmentTwo;
            }

            else{
                return null;
            }
        }
    }

    @Override
    public int getItemCount() {
        if(args.get("displayOption").equals("fact")){
            return 3;
        } else {
            return 2;
        }

    }
}

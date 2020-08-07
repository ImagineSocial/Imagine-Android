package com.imagine.myapplication.Community;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;

public class TestCollectionAdapter extends FragmentStateAdapter {
    HashMap<String,String> args;
    public TestCollectionAdapter(@NonNull FragmentActivity fragmentActivity, HashMap<String,String> args) {
        super(fragmentActivity);
        this.args = args;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(args.get("displayOption").equals("fact")){
            if(position == 0){
                CommunityFactsFragment fragmentOne = new CommunityFactsFragment(args);
                return fragmentOne;
            } else if (position == 1){
                CommunityFeedFragment fragmentTwo = new CommunityFeedFragment(args);
                return fragmentTwo;
            }else{
                return null;
            }
        } else {
            if(position == 0) {
                CommunityFeedFragment fragmentTwo = new CommunityFeedFragment(args);
                return fragmentTwo;
            }else{
                return null;
            }
        }
    }

    @Override
    public int getItemCount() {
        if(args.get("displayOption").equals("fact")){
            return 2;
        } else {
            return 1;
        }

    }
}
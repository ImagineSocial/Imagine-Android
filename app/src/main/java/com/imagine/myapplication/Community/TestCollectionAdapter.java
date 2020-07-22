package com.imagine.myapplication.Community;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;
import java.util.Map;

public class TestCollectionAdapter extends FragmentStateAdapter {
    HashMap<String,String> args;
    public TestCollectionAdapter(@NonNull FragmentActivity fragmentActivity, HashMap<String,String> args) {
        super(fragmentActivity);
        this.args = args;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            CommunityFragmentOne fragmentOne = new CommunityFragmentOne(args);
            return fragmentOne;
        } else if (position == 1){
            CommunityFragmentTwo fragmentTwo = new CommunityFragmentTwo(args);
            return fragmentTwo;
        }else if ( position == 2){
            CommunityFragmentThree fragmentThree = new CommunityFragmentThree(args);
            return fragmentThree;
        }else{
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

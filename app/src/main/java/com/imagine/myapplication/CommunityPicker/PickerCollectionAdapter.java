package com.imagine.myapplication.CommunityPicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PickerCollectionAdapter extends FragmentStateAdapter {

    public CommunityPickActivity activity;

    public PickerCollectionAdapter(@NonNull FragmentActivity fragmentActivity, CommunityPickActivity activity) {
        super(fragmentActivity);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return  new TopicsFragment(activity);
            case 1:
                return new FactsFragment(activity);
            case 2:
                return new OwnCommsFragment(activity);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

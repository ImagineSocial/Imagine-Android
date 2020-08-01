package com.imagine.myapplication.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.imagine.myapplication.R;

import java.util.HashMap;

public class CommunityAddonsFragment extends Fragment {
    HashMap<String,String> args;

    public CommunityAddonsFragment(HashMap<String, String> args) {
        this.args = args;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.community_viewpager_addons,container,false);
    }
}

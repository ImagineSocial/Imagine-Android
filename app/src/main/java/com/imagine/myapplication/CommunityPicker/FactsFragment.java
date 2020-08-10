package com.imagine.myapplication.CommunityPicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.CommunityCallback;
import com.imagine.myapplication.R;

import java.util.ArrayList;

public class FactsFragment extends Fragment {

    public CommunityPickActivity activity;
    public RecyclerView recyclerView;
    public ArrayList<Community> facts;

    public FactsFragment(CommunityPickActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_communities,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.recyclerView = getView().findViewById(R.id.communities_recyclerview);
        Communities_Helper helper = new Communities_Helper();
        helper.getFacts(new CommunityCallback() {
            @Override
            public void onCallback(ArrayList<Community> commList) {
                facts = commList;
                initRecyclerView();
            }
        });
    }

    public void initRecyclerView(){
        CommunityPickerAdapter adapter = new CommunityPickerAdapter(this.facts,getContext(),activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
    }

}

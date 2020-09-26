package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.imagine.myapplication.R;

import java.util.ArrayList;

public class Community_Addon_New_Addon_Activity extends AppCompatActivity {

    public Community comm;
    public ArrayList<PreView> preViews = new ArrayList<>();
    public Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_addon_activity);
        this.mContext = this;
        Intent intent = getIntent();
        String commString = intent.getStringExtra("comm");
        Gson gson = new Gson();
        this.comm = gson.fromJson(commString,Community.class);
        PreView pre1 = new PreView();
        pre1.comm = this.comm;
        pre1.type = "one";
        PreView pre2 = new PreView();
        pre2.comm = this.comm;
        pre2.type = "two";
        PreView pre3 = new PreView();
        pre3.comm = this.comm;
        pre3.type = "three";
        preViews.add(pre1);
        preViews.add(pre2);
        preViews.add(pre3);
        this.initRecyclerView();
    }

    public void initRecyclerView(){
        Community_Addons_PreView_Adapter adapter = new Community_Addons_PreView_Adapter(mContext,preViews);
        RecyclerView recyclerView = findViewById(R.id.addOnStore_recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    }
}

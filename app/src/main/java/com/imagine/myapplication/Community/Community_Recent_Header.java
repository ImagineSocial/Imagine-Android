package com.imagine.myapplication.Community;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Community_Recent_Header extends Community_ViewHolder {

    public ArrayList<Community> recents = new ArrayList<>();
    public RecyclerView recyclerView;
    public Recent_Communities_Adapter adapter;
    public Communities_Fragment fragment;

    public Community_Recent_Header(@NonNull View itemView , Context mContext, Communities_Fragment fragment) {
        super(itemView);
        this.mContext = mContext;
        this.fragment = fragment;
    }

    @Override
    public void bind(Community comm) {
        TextView header = itemView.findViewById(R.id.communities_recent_title);
        header. setText("Kürzlich besucht:");
        this.recyclerView = itemView.findViewById(R.id.communities_recent_recycler);
        getRecents();
        this.fragment.setHeaderRef(this);
    }

    public void initRecyclerView(){
        this.adapter = new Recent_Communities_Adapter(this.recents,this.mContext);
        this.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager =  new LinearLayoutManager(this.mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager( layoutManager);
    }

    public void getRecents(){
        String recentString = "";
        try{
            FileInputStream inputStreamReader = mContext.openFileInput("recents.txt");
            if(inputStreamReader != null){
                InputStreamReader reader = new InputStreamReader(inputStreamReader);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String onjString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while((onjString = bufferedReader.readLine()) != null){
                    stringBuilder.append("\n").append(onjString);
                }
                inputStreamReader.close();
                recentString = stringBuilder.toString();
                Gson gson = new Gson();
                Community[] recents = gson.fromJson(recentString,Community[].class);
                ArrayList<Community> recentsList = new ArrayList<>();
                for(Community comm : recents){
                    recentsList.add(comm);
                }
                this.recents = recentsList;
                initRecyclerView();
                System.out.println("!");
            }
        }
        catch(FileNotFoundException e){
            Log.e("login activity", "File not found: " + e.toString());
        }
        catch(IOException e){
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }


}

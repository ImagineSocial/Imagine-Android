package com.imagine.myapplication.nav_fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_Adapter;
import com.imagine.myapplication.Community.Community_Recent_Header;
import com.imagine.myapplication.CommunityCallback;
import com.imagine.myapplication.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Communities_Fragment extends Fragment{
    private static final String TAG = "Communities_Fragment";
    public Communities_Helper helper = new Communities_Helper();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public Community_Recent_Header recent_header;
    public RecyclerView recyclerView;
    public int lastPosition;
    public GridLayoutManager gridLayoutManager;
    public ArrayList<Community> commList = new ArrayList<>();
    public ArrayList<Community> recents = new ArrayList<>();
    public Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_communities,container,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // fetches the the communities
        super.onViewCreated(view, savedInstanceState);
        this.mContext = getContext();
        
        if(this.commList.size() == 0){
            helper.getCommunities(new CommunityCallback() {
                @Override
                public void onCallback(ArrayList<Community> communities) {
                    commList = communities;
                    initRecyclerView(view);
                }
            },auth.getCurrentUser().getUid());
        }
        else{
            initRecyclerView(view);
            loadPosition();
        }
    }

    private void initRecyclerView (final View view){
        // initializes the recyclerView
        this.recyclerView = view.findViewById(R.id.communities_recyclerview);
        final Community_Adapter adapter = new Community_Adapter(commList,this.mContext,this);
        recyclerView.setAdapter(adapter);
        this.gridLayoutManager = new GridLayoutManager(this.mContext,2);
        this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(adapter.getItemViewType(position)){
                    case R.layout.communities_header:
                        return 2;
                    case R.layout.communities_sub_header:
                        return 2;
                    case R.layout.community_recent_header:
                        return 2;
                    case R.layout.community_topic:
                        return 1;
                    case R.layout.community_footer:
                        return 2;
                    case R.layout.community_own_comms:
                        return 2;
                    default:
                        System.out.println("default case! "+TAG);
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(this.gridLayoutManager);
    }

    public void loadPosition(){
        gridLayoutManager.scrollToPosition(this.lastPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        //lastPosition = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    public void addRecent(Community comm){

        if(recents.size() == 10){
            recents.remove(1);
            recents.add(comm);
        }else{
            recents.add(comm);
        }
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
                for(Community community : recents){
                    recentsList.add(community);
                }
                recentsList.add(comm);
                String newRecents = gson.toJson(recentsList);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext
                        .openFileOutput("recents.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(newRecents);
                outputStreamWriter.close();
            }
        }
        catch(FileNotFoundException e){
            Log.e("login activity", "File not found: " + e.toString());
        }
        catch(IOException e){
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }

    public void setHeaderRef(Community_Recent_Header recent_header){
        this.recent_header = recent_header;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.recent_header != null){
            this.recent_header.getRecents();
        }
    }
}

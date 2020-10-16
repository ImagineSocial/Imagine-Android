package com.imagine.myapplication.Community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.CommunityCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

import java.util.ArrayList;

public class Community_All_Communities_Activity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ArrayList<Community> topicsList;
    public ArrayList<Community> factsList;
    public Communities_Helper helper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_communities);
        this.recyclerView = findViewById(R.id.communities_recyclerview);

        this.helper = new Communities_Helper(this);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if(type.equals("topic")){
            helper.getTopics(new CommunityCallback() {
                @Override
                public void onCallback(ArrayList<Community> commList) {
                    topicsList = commList;
                    initTopicRecycler();
                }
            });
        }else{
            helper.getFacts(new CommunityCallback() {
                @Override
                public void onCallback(ArrayList<Community> commList) {
                    factsList = commList;
                    initFactRecycler();
                }
            });
        }
    }

    public void initTopicRecycler(){
        Community_Adapter adapter = new Community_Adapter(topicsList,this,new Communities_Fragment());
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            boolean loading = true;
            int previousTotal =0;
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = lm.getChildCount();
                int totalItemCount = lm.getItemCount();
                int pastVisibleItems = lm.findFirstVisibleItemPosition();

                if(loading && totalItemCount > previousTotal){
                    loading = false;
                    previousTotal = totalItemCount;
                }
                if((totalItemCount-(pastVisibleItems+visibleItemCount))<=4&&!loading){
                    loading = true;
                    helper.getMoreTopics(new CommunityCallback() {
                        @Override
                        public void onCallback(ArrayList<Community> comms) {
                            topicsList = comms;
                            Community_Adapter adapter =(Community_Adapter) recyclerView.getAdapter();
                            adapter.addMoreCommunities(topicsList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public void initFactRecycler(){
        Community_Adapter adapter = new Community_Adapter(factsList,this,new Communities_Fragment());
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            boolean loading = true;
            int previousTotal =0;
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = lm.getChildCount();
                int totalItemCount = lm.getItemCount();
                int pastVisibleItems = lm.findFirstVisibleItemPosition();

                if(loading && totalItemCount > previousTotal){
                    loading = false;
                    previousTotal = totalItemCount;
                }
                if((totalItemCount-(pastVisibleItems+visibleItemCount))<=4&&!loading){
                    loading = true;
                    helper.getMoreFacts(new CommunityCallback() {
                        @Override
                        public void onCallback(ArrayList<Community> comms) {
                            factsList = comms;
                            Community_Adapter adapter =(Community_Adapter) recyclerView.getAdapter();
                            adapter.addMoreCommunities(factsList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}

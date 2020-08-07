package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

import java.util.ArrayList;


public class Community_Recent_Header extends Community_ViewHolder {

    public ArrayList<Community> recents = new ArrayList<>();
    public RecyclerView recyclerView;
    public Recent_Communities_Adapter adapter;

    public Community_Recent_Header(@NonNull View itemView, Communities_Fragment fragment, Context mContext) {
        super(itemView,fragment);
        this.mContext = mContext;
    }

    @Override
    public void bind(Community comm) {
        fragment.setHeaderRef(this);
        TextView header = itemView.findViewById(R.id.communities_recent_title);
        header. setText("Kürzlich besucht:");
        this.recyclerView = itemView.findViewById(R.id.communities_recent_recycler);
        initRecyclerView();
    }

    public void initRecyclerView(){
        this.adapter = new Recent_Communities_Adapter(this.recents,this.mContext);
        this.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager =  new LinearLayoutManager(this.mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager( layoutManager);
    }

    public void getRecents(ArrayList<Community> recents){
        this.recents = recents;
        adapter.getRecents(recents);
        adapter.notifyDataSetChanged();
    }
}

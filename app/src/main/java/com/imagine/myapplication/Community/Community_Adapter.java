package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

import java.util.ArrayList;

public class Community_Adapter extends RecyclerView.Adapter<Community_ViewHolder> {
    private static final String TAG = "Community_Adapter";
    public ArrayList<Community> commList = new ArrayList<>();
    public Communities_Fragment fragment;
    public Context mContext;

    // the adapter for the communityFragment

    public Community_Adapter(ArrayList<Community> commList, Context context, Communities_Fragment fragment){
        this.commList = commList;
        this.mContext = context;
        this.fragment = fragment;
    }
    public void addMoreCommunities(ArrayList<Community> comms){
        this.commList = comms;
    }
    @NonNull
    @Override
    public Community_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater inflater = LayoutInflater.from(mContext);
        //if(viewType == R.layout.communities_header){
        //    View view = inflater.inflate(R.layout.communities_header, parent,false);
        //    Communities_Header commHeader = new Communities_Header(view);
        //    return commHeader;
        //}
        //View view = inflater.inflate(R.layout.community_topic, parent, false);
        //Community_ViewHolder commVH = new Community_ViewHolder(view);
        //int spacing = 30;
        //ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        //layoutParams.height = (int) ((parent.getWidth() / 2) - spacing);
        //view.setLayoutParams(layoutParams);
        //return commVH;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        switch(viewType){
            case R.layout.communities_header:
                view = inflater.inflate(R.layout.communities_header,parent,false);
                Communities_Header header = new Communities_Header(view);
                return header;
            case R.layout.communities_sub_header:
                view = inflater.inflate(R.layout.communities_sub_header, parent, false);
                Communities_Sub_Header subheader = new Communities_Sub_Header(view);
                return subheader;
            case R.layout.community_recent_header:
                view = inflater.inflate(R.layout.community_recent_header,parent,false);
                Community_Recent_Header recent_header = new Community_Recent_Header(view,mContext,this.fragment);
                return recent_header;
            case R.layout.community_topic:
                view = inflater.inflate(R.layout.community_topic,parent,false);
                Community_ViewHolder community_viewHolder = new Community_ViewHolder(view);
                return community_viewHolder;
            case R.layout.community_fact:
                view = inflater.inflate(R.layout.community_fact,parent,false);
                Community_Fact_Viewholder community_fact_viewholder = new Community_Fact_Viewholder(view);
                int spacing = 30;
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (int) ((parent.getWidth() / 2) - spacing);
                view.setLayoutParams(layoutParams);
                return community_fact_viewholder;
            case R.layout.community_footer:
                view = inflater.inflate(R.layout.community_footer,parent,false);
                Community_Footer_Viewholder community_footer_viewholder = new Community_Footer_Viewholder(view);
                return community_footer_viewholder;
            case R.layout.community_own_comms:
                view = inflater.inflate(R.layout.community_own_comms,parent,false);
                Community_OwnComms_ViewHolder community_ownComms_viewHolder = new Community_OwnComms_ViewHolder(view);
                return community_ownComms_viewHolder;
            default:
                view = inflater.inflate(R.layout.community_footer,parent,false);
                Community_Footer_Viewholder community_footer_viewholder2 = new Community_Footer_Viewholder(view);
                return community_footer_viewholder2;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Community_ViewHolder holder, int position) {
        Community comm = commList.get(position);
        holder.bind(comm);
    }

    @Override
    public int getItemViewType(int position) {
        Community comm = commList.get(position);
        switch(comm.type){
            case "commHeader":
                return R.layout.communities_header;
            case "recentHeader":
                return R.layout.community_recent_header;
            case "topicsHeader":
                return R.layout.communities_sub_header;
            case "factsHeader":
                return R.layout.communities_sub_header;
            case "ownHeader":
                return R.layout.communities_sub_header;
            case "topic":
                return R.layout.community_topic;
            case "fact":
                return R.layout.community_fact;
            case "ownComms":
                return R.layout.community_own_comms;
            case "footer":
                return R.layout.community_footer;
            default:
                return 1;
        }
    }

    @Override
    public int getItemCount() {
        return commList.size();
    }
}

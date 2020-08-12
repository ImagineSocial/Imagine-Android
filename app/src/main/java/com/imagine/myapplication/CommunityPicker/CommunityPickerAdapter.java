package com.imagine.myapplication.CommunityPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.R;

import java.util.ArrayList;

public class CommunityPickerAdapter extends RecyclerView.Adapter<Community_Picker_ViewHolder> {
    private static final String TAG = "CommunityPickerAdapter";
    public ArrayList<Community> commList = new ArrayList<>();
    public CommunityPickActivity parent;
    public Context mContext;

    // works like all the other adapters
    // used for the recyclerview inside the CommunityPicker
    // for the linkedFact id in the newPostFragement

    public CommunityPickerAdapter(ArrayList<Community> commList, Context mContext, CommunityPickActivity picker) {
        this.commList = commList;
        this.mContext = mContext;
        this.parent = picker;
    }

    public void addMoreCommunities(ArrayList<Community> comms){
        this.commList = comms;
    }

    @NonNull
    @Override
    public Community_Picker_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch(viewType){
            case R.layout.community_topic:
                View viewTopic = inflater.inflate(R.layout.community_topic, parent, false);
                Community_Picker_ViewHolder topicVH = new Community_Picker_ViewHolder(viewTopic);
                int spacingTopic = 30;
                ViewGroup.LayoutParams layoutParamsTopic = viewTopic.getLayoutParams();
                layoutParamsTopic.height = (int) ((parent.getWidth() / 2) - spacingTopic);
                viewTopic.setLayoutParams(layoutParamsTopic);
                return topicVH;
            case R.layout.community_fact:
                View viewFact = inflater.inflate(R.layout.community_fact, parent, false);
                Community_Picker_ViewHolder factVH = new Community_Picker_ViewHolder(viewFact);
                int spacingFact = 30;
                ViewGroup.LayoutParams layoutParamsFact = viewFact.getLayoutParams();
                layoutParamsFact.height = (int) ((parent.getWidth() / 2) - spacingFact);
                viewFact.setLayoutParams(layoutParamsFact);
                return factVH;
            case R.layout.community_own_comms:
                View viewOwnComms = inflater.inflate(R.layout.community_own_comms, parent, false);
                Community_Picker_ViewHolder ownCommsVH = new Community_Picker_ViewHolder(viewOwnComms);
                return ownCommsVH;
        }

        View view = inflater.inflate(R.layout.community_topic, parent, false);
        Community_Picker_ViewHolder commVH = new Community_Picker_ViewHolder(view);
        int spacing = 30;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) ((parent.getWidth() / 2) - spacing);
        view.setLayoutParams(layoutParams);
        return commVH;
    }

    @Override
    public void onBindViewHolder(@NonNull Community_Picker_ViewHolder holder, int position) {

        Community comm = commList.get(position);
        holder.bind(comm,parent);
    }

    @Override
    public int getItemViewType(int position) {
        Community comm = commList.get(position);
        switch(comm.type){
            case "topic":
                return R.layout.community_topic;
            case "fact":
                return R.layout.community_fact;
            case "ownComms":
                return R.layout.community_own_comms;
            default:
                return 0;
        }
    }

    @Override
    public int getItemCount() {
        return commList.size();
    }
}

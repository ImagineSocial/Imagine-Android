package com.imagine.myapplication.CommunityPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Community.Communities_Header;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_Adapter;
import com.imagine.myapplication.Community.Community_ViewHolder;
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
    public Community_Picker_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(viewType == R.layout.communities_header){
            View view = inflater.inflate(R.layout.communities_header, parent,false);
            Community_Picker_Header header = new Community_Picker_Header(view);
            return header;
        }
        View view = inflater.inflate(R.layout.community, parent, false);
        Community_Picker_ViewHolder commVH = new Community_Picker_ViewHolder(view);
        int spacing = 30;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) ((parent.getWidth() / 2) - spacing);
        view.setLayoutParams(layoutParams);
        return commVH;
    }

    @Override
    public void onBindViewHolder(@NonNull Community_Picker_ViewHolder holder, int position) {
        if(position ==0){
            return;
        }
        Community comm = commList.get(position-1);
        holder.bind(comm,parent);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return R.layout.communities_header;
        }
        else return R.layout.community;
    }

    @Override
    public int getItemCount() {
        return commList.size()+1;
    }
}

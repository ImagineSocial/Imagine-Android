package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

import java.util.ArrayList;

public class Community_Adapter extends RecyclerView.Adapter<Community_ViewHolder> {
    private static final String TAG = "Community_Adapter";
    public ArrayList<Community> commList = new ArrayList<>();
    public Context mContext;

    // the adapter for the communityFragment

    public Community_Adapter(ArrayList<Community> commList, Context context){
        this.commList = commList;
        this.mContext = context;
    }
    public void addMoreCommunities(ArrayList<Community> comms){
        this.commList = comms;
    }
    @NonNull
    @Override
    public Community_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(viewType == R.layout.communities_header){
            View view = inflater.inflate(R.layout.communities_header, parent,false);
            Communities_Header commHeader = new Communities_Header(view);
            return commHeader;
        }
        View view = inflater.inflate(R.layout.community, parent, false);
        Community_ViewHolder commVH = new Community_ViewHolder(view);
        int spacing = 30;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) ((parent.getWidth() / 2) - spacing);
        view.setLayoutParams(layoutParams);
        return commVH;
    }

    @Override
    public void onBindViewHolder(@NonNull Community_ViewHolder holder, int position) {
        if(position ==0){
            return;
        }
        Community comm = commList.get(position-1);
        holder.bind(comm);
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

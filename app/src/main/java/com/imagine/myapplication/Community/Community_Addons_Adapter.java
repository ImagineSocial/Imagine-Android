package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class Community_Addons_Adapter extends RecyclerView.Adapter<Community_Addons_ViewHolder> {
    public ArrayList<Addon> addons;
    public Context mContext;

    public Community_Addons_Adapter(ArrayList<Addon> addons, Context mContext) {
        this.addons = addons;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Community_Addons_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        switch(viewType){
            case R.layout.community_addons_viewholder:
                view = inflater.inflate(R.layout.community_addons_viewholder,parent,false);
                Community_Addons_ViewHolder viewHolder = new Community_Addons_ViewHolder(view,mContext);
                return viewHolder;
            case R.layout.community_addons_community_viewholder:
                view = inflater.inflate(R.layout.community_addons_community_viewholder,parent,false);
                Community_Addons_Community_ViewHolder commHolder = new Community_Addons_Community_ViewHolder(view,mContext);
                return commHolder;
            case R.layout.addons_header_viewholder:
                view = inflater.inflate(R.layout.addons_header_viewholder,parent ,false);
                Community_Addons_Header_Viewholder header = new Community_Addons_Header_Viewholder(view,mContext);
                return header;
            default:
                view = inflater.inflate(R.layout.community_addons_viewholder,parent,false);
                Community_Addons_ViewHolder viewHolderDefault = new Community_Addons_ViewHolder(view,mContext);
                return viewHolderDefault;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Community_Addons_ViewHolder holder, int position) {
        if(holder instanceof Community_Addons_Community_ViewHolder){
            ((Community_Addons_Community_ViewHolder)holder).bind(addons.get(position));
        }else if(holder instanceof Community_Addons_Header_Viewholder){
            ((Community_Addons_Header_Viewholder)holder).bind(addons.get(position));
        }else {
            holder.bind(addons.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Addon addon = addons.get(position);
        if(addon.isHeader){
            return  R.layout.addons_header_viewholder;
        }
        if(addon.linkedFactID.equals("")){
            return R.layout.community_addons_viewholder;
        }else{
            return R.layout.community_addons_community_viewholder;
        }
    }

    @Override
    public int getItemCount() {
        return addons.size();
    }
}

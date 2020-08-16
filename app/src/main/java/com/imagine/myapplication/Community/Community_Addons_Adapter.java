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
        View view = inflater.inflate(R.layout.community_addons_viewholder,parent,false);
        Community_Addons_ViewHolder viewHolder = new Community_Addons_ViewHolder(view,mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Community_Addons_ViewHolder holder, int position) {
        holder.bind(addons.get(position));
    }

    @Override
    public int getItemCount() {
        return addons.size();
    }
}

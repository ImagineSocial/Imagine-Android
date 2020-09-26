package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

import java.util.ArrayList;

public class Community_Addons_PreView_Adapter extends RecyclerView.Adapter<Community_Addons_PreView_ViewHolder> {

    public Context mContext;
    public ArrayList<PreView> prevs;

    public Community_Addons_PreView_Adapter(Context mContext, ArrayList<PreView> prevs) {
        this.mContext = mContext;
        this.prevs = prevs;
    }

    @NonNull
    @Override
    public Community_Addons_PreView_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.new_addon_viewholder,parent,false);
        Community_Addons_PreView_ViewHolder holder = new Community_Addons_PreView_ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Community_Addons_PreView_ViewHolder holder, int position) {
        holder.bind(prevs.get(position));
    }

    @Override
    public int getItemCount() {
        return prevs.size();
    }
}

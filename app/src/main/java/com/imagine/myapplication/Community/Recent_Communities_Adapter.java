package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

import java.util.ArrayList;

public class Recent_Communities_Adapter extends RecyclerView.Adapter<Community_Recent_Viewholder> {

    public ArrayList<Community> recents = new ArrayList<>();
    public Context mConttext;

    public Recent_Communities_Adapter(ArrayList<Community> recents, Context mConttext) {
        this.recents = recents;
        this.mConttext = mConttext;
    }

    public void getRecents(ArrayList<Community> recents){
        this.recents = recents;
    }

    @NonNull
    @Override
    public Community_Recent_Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mConttext);
        View view = inflater.inflate(R.layout.community_recent_viewholder,parent,false);
        Community_Recent_Viewholder holder = new Community_Recent_Viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Community_Recent_Viewholder holder, int position) {
        Community comm = recents.get(position);
        holder.bind(comm);
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }

    public void setRecents(ArrayList<Community> recents){
        this.recents = recents;
    }
}

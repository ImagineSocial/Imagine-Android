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

    public ArrayList<Community> commList = new ArrayList<>();
    public Context mContext;

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
        View view = inflater.inflate(R.layout.community, parent, false);
        Community_ViewHolder commVH = new Community_ViewHolder(view);

        Integer spacing = 30;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) ((parent.getWidth() / 2) - spacing);
        view.setLayoutParams(layoutParams);
        return commVH;
    }

    @Override
    public void onBindViewHolder(@NonNull Community_ViewHolder holder, int position) {
        Community comm = commList.get(position);
        holder.bind(comm);
    }

    @Override
    public int getItemCount() {
        return commList.size();
    }
}

package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.DefaultPost;

import java.util.ArrayList;

public class Community_Items_Adapter extends RecyclerView.Adapter<Community_Items_ViewHolder> {
    public ArrayList<Object> items;
    public Context mContext;

    public Community_Items_Adapter(ArrayList<Object> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Community_Items_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        switch(viewType){
            case R.layout.addon_items_default_viewholder:
                view = inflater.inflate(R.layout.addon_items_default_viewholder,parent,false);
                return new Community_Items_ViewHolder(view);
            case R.layout.addon_items_community_viewholder:
                view = inflater.inflate(R.layout.addon_items_community_viewholder,parent,false);
                return new Community_Items_ViewHolder(view);
            case R.layout.addon_items_post_viewholder:
                view = inflater.inflate(R.layout.addon_items_post_viewholder,parent,false);
                return new Community_Items_ViewHolder(view);
            default:
                view = inflater.inflate(R.layout.addon_items_default_viewholder,parent,false);
                return new Community_Items_ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull Community_Items_ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = items.get(position);
        if (obj instanceof DefaultPost) {
            return R.layout.addon_items_default_viewholder;
        } else if (obj instanceof Community) {
            return R.layout.addon_items_community_viewholder;
        } else {
            return R.layout.addon_items_post_viewholder;
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

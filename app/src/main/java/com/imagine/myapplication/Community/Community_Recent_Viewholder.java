package com.imagine.myapplication.Community;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;

public class Community_Recent_Viewholder extends RecyclerView.ViewHolder {
    public Community_Recent_Viewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Community comm){
        ImageView imageView = itemView.findViewById(R.id.recent_image);
        if(comm.imageURL != null){
            Glide.with(itemView).load(comm.imageURL).into(imageView);
        }else{
            Glide.with(itemView).load(R.drawable.fact_stamp).into(imageView);
        }

    }
}

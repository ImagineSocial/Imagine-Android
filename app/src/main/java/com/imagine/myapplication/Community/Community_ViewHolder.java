package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;

public class Community_ViewHolder extends RecyclerView.ViewHolder {

    public Context mContext;

    public Community_ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(Community comm){
        TextView title_tv = itemView.findViewById(R.id.comm_title);
        TextView description_tv = itemView.findViewById(R.id.comm_description);
        ImageView imageView = itemView.findViewById(R.id.comm_picture);

        String name = comm.name;
        String description = comm.description;
        String imageURL = comm.imageURL;
        String commID = comm.topicID;

        title_tv.setText(name);
        description_tv.setText(description);
        if( imageURL != null)
            Glide.with(itemView).load(imageURL).into(imageView);
        else System.out.println("NO IMAGEURL: " +commID);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(),Community_Activity.class);
            }
        });
    }
}

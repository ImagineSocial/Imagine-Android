package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.Feed.viewholder_classes.CustomViewHolder;
import com.imagine.myapplication.R;

public class Community_Feed_Header_Viewholder extends CustomViewHolder {

    Context mContext;

    public Community_Feed_Header_Viewholder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(Community community){
        TextView title_tv = itemView.findViewById(R.id.comm_activity_title);
        TextView description_tv = itemView.findViewById(R.id.comm_activity_description);
        ImageView image_iv = itemView.findViewById(R.id.comm_activity_picture);
        image_iv.setClipToOutline(true);

        title_tv.setText(community.name);
        description_tv.setText(community.description);
        if(community.imageURL == null || community.imageURL.equals("")){
            Glide.with(itemView).load(R.drawable.default_user).into(image_iv);
        }else{
            Glide.with(itemView).load(community.imageURL).into(image_iv);
        }
    }
}

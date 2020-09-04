package com.imagine.myapplication.CommunityPicker;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_Activity;
import com.imagine.myapplication.Community.Community_ViewHolder;
import com.imagine.myapplication.Community.Community_ViewPager_Activity;
import com.imagine.myapplication.R;

import javax.xml.datatype.Duration;

public class Community_Picker_ViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "Community_Picker_ViewHo";
    public Context mContext;

    // same as the normal CommunityViewholder but has a different onClick
    // event
    public Community_Picker_ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final Community comm, final CommunityPickActivity picker){
        switch(comm.type){
            case "topic":
                this.bindTopic(comm,picker);
                return;
            case "fact":
                this.bindFact(comm, picker);
                return;
            case "ownComms":
                this.bindOwnComm(comm,picker);
                return;
        }
    }

    public void bindTopic(final Community comm, final CommunityPickActivity parent){
        TextView title_tv = itemView.findViewById(R.id.comm_title);
        TextView description_tv = itemView.findViewById(R.id.comm_description);
        ImageView imageView = itemView.findViewById(R.id.comm_picture);
        ConstraintLayout contentView = itemView.findViewById(R.id.community_content_view);
        contentView.setClipToOutline(true);
        final String name = comm.name;
        final String description = comm.description;
        final String imageURL = comm.imageURL;
        title_tv.setText(name);
        description_tv.setText(description);
        if(imageURL != null) {
            Glide.with(itemView).load(imageURL).into(imageView);
        } else {
            Glide.with(itemView).load(R.drawable.fact_stamp);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name",comm.name);
                resultIntent.putExtra("imageURL", comm.imageURL);
                resultIntent.putExtra("commID", comm.topicID);
                resultIntent.putExtra("postID",parent.postID);
                parent.setResult(parent.RESULT_OK,resultIntent);
                parent.finish();
            }
        });
    }

    public void bindFact(final Community comm, final CommunityPickActivity parent){
        ImageView imageView = itemView.findViewById(R.id.community_fact_image);
        imageView.setClipToOutline(true);
        TextView title_tv = itemView.findViewById(R.id.community_fact_title);
        TextView description_tv = itemView.findViewById(R.id.community_fact_description);
        if(comm.imageURL != null){
            Glide.with(itemView).load(comm.imageURL).into(imageView);
        }else{
            Glide.with(itemView).load(R.drawable.fact_stamp).into(imageView);
        }
        title_tv.setText(comm.name);
        description_tv.setText(comm.description);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name",comm.name);
                resultIntent.putExtra("imageURL", comm.imageURL);
                resultIntent.putExtra("commID", comm.topicID);
                parent.setResult(parent.RESULT_OK,resultIntent);
                parent.finish();
            }
        });
    }

    public void bindOwnComm(final Community comm, final CommunityPickActivity parent){
        ImageView image = itemView.findViewById(R.id.community_own_image);
        TextView title = itemView.findViewById(R.id.community_own_title);

        if(comm.imageURL != null){
            Glide.with(itemView).load(comm.imageURL).into(image);
        }else{
            Glide.with(itemView).load(R.drawable.fact_stamp).into(image);
        }

        title.setText(comm.name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name",comm.name);
                resultIntent.putExtra("imageURL", comm.imageURL);
                resultIntent.putExtra("commID", comm.topicID);
                parent.setResult(parent.RESULT_OK,resultIntent);
                parent.finish();
            }
        });
    }
}

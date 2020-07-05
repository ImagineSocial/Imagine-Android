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
import com.imagine.myapplication.R;

import javax.xml.datatype.Duration;

public class Community_Picker_ViewHolder extends RecyclerView.ViewHolder {
    public Context mContext;

    public Community_Picker_ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final Community comm, final CommunityPickActivity picker){
        TextView title_tv = itemView.findViewById(R.id.comm_title);
        TextView description_tv = itemView.findViewById(R.id.comm_description);
        ImageView imageView = itemView.findViewById(R.id.comm_picture);

        ConstraintLayout contentView = itemView.findViewById(R.id.community_content_view);
        contentView.setClipToOutline(true);

        final String name = comm.name;
        final String description = comm.description;
        final String imageURL = comm.imageURL;
        final String commID = comm.topicID;

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
                resultIntent.putExtra("imageURl", comm.imageURL);
                resultIntent.putExtra("commID", comm.topicID);
                picker.setResult(picker.RESULT_OK,resultIntent);
                picker.finish();
            }
        });
    }
}

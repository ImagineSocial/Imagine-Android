package com.imagine.myapplication.Community;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

public class Community_Fact_Viewholder extends Community_ViewHolder {

    public Community_Fact_Viewholder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final Community comm) {
        ImageView imageView = itemView.findViewById(R.id.community_fact_image);
        imageView.setClipToOutline(true);
        TextView title_tv = itemView.findViewById(R.id.community_fact_title);
        TextView description_tv = itemView.findViewById(R.id.community_fact_description);
        if(comm.imageURL != null){
            Glide.with(itemView).load(comm.imageURL).into(imageView);
        }else{
            Glide.with(itemView).load(R.drawable.fact_stamp).into(imageView);
        }
        final String name = comm.name;
        final String description = comm.description;
        final String imageURL = comm.imageURL;
        final String commID = comm.topicID;
        final String displayOption = comm.displayOption;
        this.comm = new Community(name,imageURL,commID,description);
        this.comm.displayOption = displayOption;
        title_tv.setText(comm.name);
        description_tv.setText(comm.description);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToRecents(comm);
                Intent intent = new Intent(itemView.getContext(),Community_ViewPager_Activity.class);
                Gson gson = new Gson();
                String jsonComm = gson.toJson(comm);
                intent.putExtra("comm", jsonComm);
                mContext.startActivity(intent);
            }
        });
    }
}

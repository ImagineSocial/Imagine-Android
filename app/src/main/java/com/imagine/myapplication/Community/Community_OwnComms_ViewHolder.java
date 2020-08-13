package com.imagine.myapplication.Community;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

public class Community_OwnComms_ViewHolder extends Community_ViewHolder {
    public Community_OwnComms_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final Community comm) {
        ImageView image = itemView.findViewById(R.id.community_own_image);
        TextView title = itemView.findViewById(R.id.community_own_title);

        if(comm.imageURL != null){
            Glide.with(itemView).load(comm.imageURL).into(image);
        }else{
            Glide.with(itemView).load(R.drawable.fact_stamp).into(image);
        }
        final String name = comm.name;
        final String description = comm.description;
        final String imageURL = comm.imageURL;
        final String commID = comm.topicID;
        final String displayOption = comm.displayOption;
        this.comm = new Community(name,imageURL,commID,description);
        this.comm.displayOption = displayOption;
        title.setText(name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToRecents(comm);
                Intent intent = new Intent(itemView.getContext(),Community_ViewPager_Activity.class);
                intent.putExtra("name", name);
                intent.putExtra("description",description);
                intent.putExtra("imageURL", imageURL);
                intent.putExtra("commID", commID);
                intent.putExtra("displayOption",displayOption);
                mContext.startActivity(intent);
            }
        });
    }
}

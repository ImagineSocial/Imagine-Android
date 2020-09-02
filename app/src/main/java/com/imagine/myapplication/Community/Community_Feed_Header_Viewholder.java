package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.Feed.viewholder_classes.CustomViewHolder;
import com.imagine.myapplication.R;

public class Community_Feed_Header_Viewholder extends CustomViewHolder {
    private static final String TAG = "Community_Feed_Header_V";
    Context mContext;

    // works as an header for the community_topic feed
    // shows the communityPicture and description

    public Community_Feed_Header_Viewholder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(Community community){
        TextView title_tv = itemView.findViewById(R.id.comm_activity_title);
        TextView description_tv = itemView.findViewById(R.id.comm_activity_description);
        ImageView image_iv = itemView.findViewById(R.id.comm_activity_picture);
        View backgroundView = itemView.findViewById(R.id.comm_background_view);
        final TextView followerCountLabel = itemView.findViewById(R.id.comm_header_follower_label);
        final TextView postCountLabel = itemView.findViewById(R.id.comm_header_post_count_label);
        backgroundView.setClipToOutline(true);
        image_iv.setClipToOutline(true);
        title_tv.setText(community.name);
        description_tv.setText(community.description);
        if(community.imageURL == null || community.imageURL.equals("")){
            Glide.with(itemView).load(R.drawable.placeholder_picture).into(image_iv);
        }else{
            Glide.with(itemView).load(community.imageURL).into(image_iv);
        }
        community.getFollowerCount(new IntegerCallback() {
            @Override
            public void onCallback(int count) {
                followerCountLabel.setText(count+"");
            }
        });
        community.getPostCount(new IntegerCallback() {
            @Override
            public void onCallback(int count) {
                postCountLabel.setText(count+"");
            }
        });
    }
}

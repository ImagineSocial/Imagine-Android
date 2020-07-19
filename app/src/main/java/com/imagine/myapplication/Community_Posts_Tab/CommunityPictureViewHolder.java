package com.imagine.myapplication.Community_Posts_Tab;

import android.content.ClipData;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.PicturePost;

public class CommunityPictureViewHolder extends CommunityCustomViewHolder {
    public CommunityPictureViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(PicturePost post){
        ImageView imageView = itemView.findViewById(R.id.community_picture_imageView);
        Glide.with(itemView).load(post.imageURL).into(imageView);
    }
}

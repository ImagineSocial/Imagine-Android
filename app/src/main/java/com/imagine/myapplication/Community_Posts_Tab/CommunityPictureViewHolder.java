package com.imagine.myapplication.Community_Posts_Tab;

import android.content.ClipData;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.PostActivitys.MultiPicturePostActivity;
import com.imagine.myapplication.PostActivitys.PicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.PicturePost;
import com.imagine.myapplication.user_classes.User;

public class CommunityPictureViewHolder extends CommunityCustomViewHolder {
    public CommunityPictureViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(final PicturePost post){
        setLinkedFact(post.linkedFactId);
        if(post.user == null){
            post_helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                    finishBind(post);
                }
            });
        }else{
            finishBind(post);
        }
    }

    public void finishBind(final PicturePost post){
        ImageView imageView = itemView.findViewById(R.id.community_picture_imageView);
        Glide.with(itemView).load(post.imageURL).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String objString = gson.toJson(post);
                Intent intent = new Intent(itemView.getContext(), PicturePostActivity.class);
                intent.putExtra("post",objString);
                if(community != null && !community.equals("")){
                    String commString = gson.toJson(community);
                    intent.putExtra("comm",commString);
                }
                itemView.getContext().startActivity(intent);
            }
        });
    }
}

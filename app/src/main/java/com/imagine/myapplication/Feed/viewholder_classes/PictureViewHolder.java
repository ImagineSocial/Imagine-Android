package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_Activity;
import com.imagine.myapplication.Community.FeedCommunityCallback;
import com.imagine.myapplication.PostActivitys.PicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.PicturePost;

public class PictureViewHolder extends CustomViewHolder {
     public Context mContext;
     public User userObj;

    public PictureViewHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();
    }

    public void bind(final PicturePost post){
        init(post);
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        ImageView picture_imageView = itemView.findViewById(R.id.picture_imageView);


        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);
        Glide.with(itemView).load(post.imageURL).into(picture_imageView);

        picture_imageView.setClipToOutline(true);
        if(post.originalPoster == "anonym"){
            name_textView.setText("Anonym");
            Glide.with(itemView).load(R.drawable.anonym_user).into(
                    profilePicture_imageView);
        }else{
                getUser(post.originalPoster, new UserCallback() {
                    @Override
                    public void onCallback(User user) {
                        userObj = user;
                        post.user = user;
                        setName(post);
                    }
                });
        }

        if (post.linkedFactId != "") {
            setLinkedFact(post.linkedFactId);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String jsonObj = gson.toJson(post);
                Intent intent = new Intent(itemView.getContext(), PicturePostActivity.class);
                intent.putExtra("post",jsonObj);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    public void setName(final PicturePost post){
        TextView username_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        username_textView.setText(post.user.name);
        if(post.user.imageURL == null || post.user.imageURL == ""){
            Glide.with(itemView).load(R.drawable.default_user).into(
                    profilePicture_imageView
            );
        }
        else{
            Glide.with(itemView).load(post.user.imageURL).into(
                    profilePicture_imageView
            );
            profilePicture_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    String userString = gson.toJson(userObj);
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("user",userString);
                    mContext.startActivity(intent);
                }
            });
        }
    }

//    public void setLinkedCommunity(final Community community) {
//        ImageView communityImageView = itemView.findViewById(R.id.topicImageView);
//        if (community.imageURL != "") {
//            Glide.with(itemView).load(community.imageURL).into(communityImageView);
//        } else {
//            Glide.with(itemView).load(R.drawable.fact_stamp).into(communityImageView);
//        }
//
//        communityImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Gson gson = new Gson();
//                String userString = gson.toJson(linkedCommunity);
//                Intent intent = new Intent(mContext, Community_Activity.class);
//                intent.putExtra("name", community.name);
//                intent.putExtra("description",community.description);
//                intent.putExtra("imageURL", community.imageURL);
//                intent.putExtra("commID", community.topicID);
//                mContext.startActivity(intent);
//            }
//        });
//    }

    @Override
    public String getType() {
        return "picture";
    }
}

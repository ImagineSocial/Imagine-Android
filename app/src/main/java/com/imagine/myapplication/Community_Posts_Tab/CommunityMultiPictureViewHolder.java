package com.imagine.myapplication.Community_Posts_Tab;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.PostActivitys.GifPostActivity;
import com.imagine.myapplication.PostActivitys.MultiPicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.MultiPicturePost;
import com.imagine.myapplication.user_classes.User;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class CommunityMultiPictureViewHolder extends CommunityCustomViewHolder {
    public CommunityMultiPictureViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public void bind(final MultiPicturePost post){
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


    public void finishBind(final MultiPicturePost post){
        CarouselView carousel = itemView.findViewById(R.id.community_multiPicture_carousel);
        carousel.setIndicatorVisibility(-1);
        carousel.setSlideInterval(6000);
        carousel.setPageTransformInterval(800);
        carousel.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Glide.with(itemView).load(post.imageURLs[position]).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    String objString = gson.toJson(post);
                    Intent intent = new Intent(itemView.getContext(), MultiPicturePostActivity.class);
                    intent.putExtra("post",objString);
                    if(community != null && !community.equals("")){
                        String commString = gson.toJson(community);
                        intent.putExtra("comm",commString);
                    }
                    itemView.getContext().startActivity(intent);
                }
        });
            }
        });
        carousel.setPageCount(post.imageURLs.length);
    }
}

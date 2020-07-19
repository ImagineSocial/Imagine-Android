package com.imagine.myapplication.Community_Posts_Tab;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.PostActivitys.MultiPicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.MultiPicturePost;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class CommunityMultiPictureViewHolder extends CommunityCustomViewHolder {
    public CommunityMultiPictureViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(final MultiPicturePost post){
        CarouselView carousel = itemView.findViewById(R.id.community_multiPicture_carousel);
        carousel.setPageCount(post.imageURLs.length);
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
                        itemView.getContext().startActivity(intent);
                    }
                });
            }
        });
    }
}

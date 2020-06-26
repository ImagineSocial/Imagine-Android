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
import com.imagine.myapplication.PostActivitys.GifPostActivity;
import com.imagine.myapplication.PostActivitys.MultiPicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.User;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.MultiPicturePost;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class MultiPictureViewHolder extends  CustomViewHolder {
    public Context mContext;

    public MultiPictureViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final MultiPicturePost post){
        init(post);
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        CarouselView carouselView = itemView.findViewById(R.id.carouselView);

        final String [] imageArray = post.imageURLs;
        String dateString =dateToString(post.createTime);
        title_textView.setText(post.title);
        createTime_textView.setText(dateString);
        carouselView.setPageCount(imageArray.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Glide.with(itemView).load(imageArray[position]).into(imageView);
            }
        });


        if(post.originalPoster == "anonym"){
            name_textView.setText("Anonym");
            Glide.with(itemView).load(R.drawable.anonym_user).into(
                    profilePicture_imageView);
        }else{
            getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                    setName(post);
                }
            });
        }
        itemView.setOnClickListener(new View.OnClickListener() {
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

    public void setName(MultiPicturePost post){
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
        }
    }

    public String getType(){
        return "multiPicture";
    }
}

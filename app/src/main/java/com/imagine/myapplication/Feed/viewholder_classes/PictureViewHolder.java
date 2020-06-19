package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.PostActivitys.PicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.User;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.PicturePost;

public class PictureViewHolder extends CustomViewHolder {
     public Context mContext;

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


        String dateString =dateToString(post.createTime);
        title_textView.setText(post.title);
        createTime_textView.setText(dateString);
        Glide.with(itemView).load(post.imageURL).into(picture_imageView);

        picture_imageView.setClipToOutline(true);
        if(post.originalPoster == "anonym"){
            name_textView.setText("anonym");
            Glide.with(itemView).load(R.drawable.default_user).into(
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
                Intent intent = new Intent(itemView.getContext(), PicturePostActivity.class);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    public void setName(PicturePost post){
        TextView username_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        username_textView.setText(post.user.name+ "  PicturePost");
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

    @Override
    public String getType() {
        return "picture";
    }
}

package com.imagine.myapplication.Feed.viewholder_classes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.DefaultPost;

import de.hdodenhof.circleimageview.CircleImageView;

public class DefaultViewHolder extends CustomViewHolder {

    public DefaultViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(DefaultPost post){
        // bind method of the default post
        // sets up the defaultpost views to default values
        TextView name_tv = itemView.findViewById(R.id.name_textView);
        CircleImageView profile_imageView = itemView.findViewById(R.id.profile_picture_imageView);
        TextView title_tv = itemView.findViewById(R.id.title_textView);
        TextView date_tv = itemView.findViewById(R.id.createDate_textView);
        date_tv.setText(itemView.getResources().getString(R.string.default_viewholder_unknow_date));
        name_tv.setText(itemView.getResources().getString(R.string.default_viewholder_unknow_post));
        title_tv.setText(post.documentID);
        Glide.with(itemView).load(R.drawable.default_user).into(profile_imageView);
    }
}

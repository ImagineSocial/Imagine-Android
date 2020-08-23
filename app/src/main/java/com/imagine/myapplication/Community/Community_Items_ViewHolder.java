package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.icu.text.MessagePattern;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.PostActivitys.PicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.PicturePost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.user_classes.User;

public class Community_Items_ViewHolder extends RecyclerView.ViewHolder {
    public PicturePost post;
    public Community comm;
    public Context mContext;
    public Post_Helper helper = new Post_Helper();
    public TextView title;
    public TextView description;
    public ImageView image;


    public Community_Items_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Object object){
        this.mContext = itemView.getContext();
        if(object instanceof PicturePost){
            this.post = (PicturePost) object;
            this.bindPicturePost(post);
        }else if(object instanceof Community){

        }
    }


    public void bindPicturePost(final PicturePost post){
        this.title = itemView.findViewById(R.id.community_item_title);
        this.description = itemView.findViewById(R.id.community_item_description);
        this.image = itemView.findViewById(R.id.community_item_image);
        title.setText(this.post.title);
        description.setText(this.post.description);
        Glide.with(itemView).load(this.post.imageURL).into(image);
        if(post.user == null){
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                    setPictureOnClicks();
                }
            });
        }else{
            setPictureOnClicks();
        }
    }

    public void setPictureOnClicks(){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Gson gson = new Gson();
                    String jsonObj = gson.toJson(post);
                    Intent intent = new Intent(itemView.getContext(), PicturePostActivity.class);
                    intent.putExtra("post",jsonObj);
                    mContext.startActivity(intent);
            }
        });
    }


}

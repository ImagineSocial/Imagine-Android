package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.LinkedCommunityCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.CommunityPost;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;

public class CommunityPostViewHolder extends CustomViewHolder {

    public CommunityPost post;
    public Communities_Helper helper = new Communities_Helper();
    public Post_Helper postHelper = new Post_Helper();
    public Community community;

    public CommunityPostViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(final CommunityPost post){
        this.post = post;

        if(post.linkedFactId != null){
            helper.fetchLinkedCommunity(post.linkedFactId, new LinkedCommunityCallback() {
                @Override
                public void onCallback(Community comm) {
                    community = comm;
                    setCommData();
                }
            });
        }
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        TextView commentCountLabel = itemView.findViewById(R.id.commentCountLabel);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);

        if(post.originalPoster.equals("anonym")){
            name_textView.setText(itemView.getResources().getString(R.string.anonym));
            Glide.with(itemView).load(R.drawable.anonym_user).into(
                    profilePicture_imageView);
        }else{
            postHelper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {

                    post.user = user;
                    setName();
                }
            });
        }

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);
    }

    public void setName(){
        // sets up the users views
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
                    String userString = gson.toJson(post.user);
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("user",userString);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void setCommData(){
        final TextView followerCountLabel = itemView.findViewById(R.id.comm_header_follower_label);
        final TextView postCountLabel = itemView.findViewById(R.id.comm_header_post_count_label);
    }
}

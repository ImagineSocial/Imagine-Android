package com.imagine.myapplication.notifications;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.PostActivitys.GifPostActivity;
import com.imagine.myapplication.PostActivitys.LinkPostActivity;
import com.imagine.myapplication.PostActivitys.MultiPicturePostActivity;
import com.imagine.myapplication.PostActivitys.PicturePostActivity;
import com.imagine.myapplication.PostActivitys.RepostPostActivity;
import com.imagine.myapplication.PostActivitys.ThoughtPostActivity;
import com.imagine.myapplication.PostActivitys.TranslationPostActivity;
import com.imagine.myapplication.PostActivitys.YouTubePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.user_classes.User;

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    public Post_Helper helper;
    public Notification not;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
        helper = new Post_Helper(itemView.getContext());
    }
    public void bind(final Notification not) {
        this.not = not;
        TextView title = itemView.findViewById(R.id.notification_title_textView);
        TextView body = itemView.findViewById(R.id.notification_body_titleView);
        if (not.post != null) {
            if (not.post.user == null) {
                helper.getUser(not.post.originalPoster, new UserCallback() {
                    @Override
                    public void onCallback(User user) {
                        not.post.user = user;
                        itemView.setOnClickListener(listenerWithPost);
                    }
                });
            }
        }else{
            itemView.setOnClickListener(listenerNoPost);
        }
        body.setText(not.title);
        switch (not.type) {
            case "upvote":
                title.setText(itemView.getResources().getString(R.string.notification_viewholder_post)+" "+not.count+" "+ itemView.getResources().getString(R.string.notification_viewholder_upvote));
                break;
            case "comment":
                title.setText(itemView.getResources().getString(R.string.notification_viewholder_post) +" "+not.count+" "+itemView.getResources().getString(R.string.notification_viewholder_comment));
                body.setText(not.comment);
                break;
            case "friend":
                title.setText(itemView.getResources().getString(R.string.notification_viewholder_friend_title));
                body.setText(not.friendRequestName+" "+itemView.getResources().getString(R.string.notification_viewholder_friend_body));
                break;
        }
    }

    public void setOnClicks(final Notification not){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Gson gson = new Gson();
                String postString;
                if(not.post == null){
                    Toast.makeText(itemView.getContext(),
                            itemView.getResources().getString(R.string.notification_viewholder_no_post),
                            Toast.LENGTH_SHORT).show();
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                }else{
                    switch(not.postType){
                        case "thought":
                            intent = new Intent(itemView.getContext(), ThoughtPostActivity.class);
                            postString = gson.toJson(not.post);
                            intent.putExtra("post",postString);
                            itemView.getContext().startActivity(intent);
                            break;
                        case "youTubeVideo":
                            intent = new Intent(itemView.getContext(), YouTubePostActivity.class);
                            postString = gson.toJson(not.post);
                            intent.putExtra("post",postString);
                            itemView.getContext().startActivity(intent);
                            break;
                        case "link":
                            intent = new Intent(itemView.getContext(), LinkPostActivity.class);
                            postString = gson.toJson(not.post);
                            intent.putExtra("post",postString);
                            itemView.getContext().startActivity(intent);
                            break;
                        case "GIF":
                            intent = new Intent(itemView.getContext(), GifPostActivity.class);
                            postString = gson.toJson(not.post);
                            intent.putExtra("post",postString);
                            itemView.getContext().startActivity(intent);
                            break;
                        case "picture":
                            intent = new Intent(itemView.getContext(), PicturePostActivity.class);
                            postString = gson.toJson(not.post);
                            intent.putExtra("post",postString);
                            itemView.getContext().startActivity(intent);
                            break;
                        case "multiPicture":
                            intent = new Intent(itemView.getContext(), MultiPicturePostActivity.class);
                            postString = gson.toJson(not.post);
                            intent.putExtra("post",postString);
                            itemView.getContext().startActivity(intent);
                            break;
                        case "translation":
                            intent = new Intent(itemView.getContext(), TranslationPostActivity.class);
                            postString = gson.toJson(not.post);
                            intent.putExtra("post",postString);
                            itemView.getContext().startActivity(intent);
                            break;
                        case "repost":
                            intent = new Intent(itemView.getContext(), RepostPostActivity.class);
                            postString = gson.toJson(not.post);
                            intent.putExtra("post",postString);
                            itemView.getContext().startActivity(intent);
                            break;
                        case "singleTopic":
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }

    public View.OnClickListener listenerWithPost = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            Gson gson = new Gson();
            String postString;
            switch(not.postType){
                case "thought":
                    intent = new Intent(itemView.getContext(), ThoughtPostActivity.class);
                    postString = gson.toJson(not.post);
                    intent.putExtra("post",postString);
                    itemView.getContext().startActivity(intent);
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                    break;
                case "youTubeVideo":
                    intent = new Intent(itemView.getContext(), YouTubePostActivity.class);
                    postString = gson.toJson(not.post);
                    intent.putExtra("post",postString);
                    itemView.getContext().startActivity(intent);
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                    break;
                case "link":
                    intent = new Intent(itemView.getContext(), LinkPostActivity.class);
                    postString = gson.toJson(not.post);
                    intent.putExtra("post",postString);
                    itemView.getContext().startActivity(intent);
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                    break;
                case "GIF":
                    intent = new Intent(itemView.getContext(), GifPostActivity.class);
                    postString = gson.toJson(not.post);
                    intent.putExtra("post",postString);
                    itemView.getContext().startActivity(intent);
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                    break;
                case "picture":
                    intent = new Intent(itemView.getContext(), PicturePostActivity.class);
                    postString = gson.toJson(not.post);
                    intent.putExtra("post",postString);
                    itemView.getContext().startActivity(intent);
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                    break;
                case "multiPicture":
                    intent = new Intent(itemView.getContext(), MultiPicturePostActivity.class);
                    postString = gson.toJson(not.post);
                    intent.putExtra("post",postString);
                    itemView.getContext().startActivity(intent);
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                    break;
                case "translation":
                    intent = new Intent(itemView.getContext(), TranslationPostActivity.class);
                    postString = gson.toJson(not.post);
                    intent.putExtra("post",postString);
                    itemView.getContext().startActivity(intent);
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                    break;
                case "repost":
                    intent = new Intent(itemView.getContext(), RepostPostActivity.class);
                    postString = gson.toJson(not.post);
                    intent.putExtra("post",postString);
                    itemView.getContext().startActivity(intent);
                    helper.deleteNotificationsFromPostID(not.post.documentID);
                    break;
                case "singleTopic":
                    break;
                default:
                    break;
            }
        }
    };

    public View.OnClickListener listenerNoPost = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(not.post == null){
                Toast.makeText(itemView.getContext(),
                        itemView.getResources().getString(R.string.notification_viewholder_no_post),
                        Toast.LENGTH_SHORT).show();
                helper.deleteNotificationsFromPostID(not.postID);
            }
        }
    };
}

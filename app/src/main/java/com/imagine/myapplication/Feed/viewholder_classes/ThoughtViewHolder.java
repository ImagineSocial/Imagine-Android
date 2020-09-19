package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.PostActivitys.ThoughtPostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.ThoughtPost;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThoughtViewHolder extends CustomViewHolder {
    private static final String TAG = "ThoughtViewHolder";
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public ThoughtPost post;
    public User userObj;
    public Post_Helper helper = new Post_Helper();

    public ThoughtViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final ThoughtPost post){
        // calls the init method ad sets up the post specific views
        init(post);
        this.post = post;
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView username_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);
        if(post.originalPoster.equals("anonym")){
            username_textView.setText("Anonym");
            Glide.with(itemView).load(R.drawable.anonym_user).into(
                    profilePicture_imageView);
        }else{
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                    userObj = user;
                    setName(post);
                }
            });
        }

        setLinkedFact(post.linkedFactId);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String jsonObj = gson.toJson(post);
                Intent intent = new Intent(itemView.getContext(), ThoughtPostActivity.class );
                intent.putExtra("post",jsonObj);
                if(community != null && !community.equals("")){
                    String commString = gson.toJson(community);
                    intent.putExtra("comm",commString);
                }
                itemView.getContext().startActivity(intent);
            }
        });

        CircleImageView topicPostImageView = itemView.findViewById(R.id.topicPostImageView);
        if (post.isTopicPost) {
            topicPostImageView.setVisibility(View.VISIBLE);
        }

        ImageButton options = itemView.findViewById(R.id.feed_menu_button);
        if(auth.getCurrentUser()!= null&& post.originalPoster.equals(auth.getCurrentUser().getUid())){
            options.setVisibility(View.VISIBLE);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu();
                }
            });
        } else {
            options.setVisibility(View.INVISIBLE);
        }
    }

    public void setName(final ThoughtPost post){
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
                    String userString = gson.toJson(userObj);
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("user",userString);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void showMenu(){
        ImageButton options = itemView.findViewById(R.id.feed_menu_button);
        PopupMenu menu = new PopupMenu(itemView.getContext(),options);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.remove_post:
                        removePost(post);
                        return true;
                    case R.id.link_community:
                        linkCommunity(post);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.feed_post_menu, menu.getMenu());
        menu.show();
    }

    @Override
    public String getType() {
        return "thought";
    }
}

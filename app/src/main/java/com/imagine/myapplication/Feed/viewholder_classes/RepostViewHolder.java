package com.imagine.myapplication.Feed.viewholder_classes;

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
import com.imagine.myapplication.PostActivitys.RepostPostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.RepostPost;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepostViewHolder extends CustomViewHolder{
    private static final String TAG = "RepostViewHolder";
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public RepostPost post;
    public User userObj;
    public Post_Helper helper;

    public RepostViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final RepostPost post){
        // calls init method and sets up the post specific views
        init(post);
        helper = new Post_Helper(itemView.getContext());
        this.post = post;
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        TextView commentCountLabel = itemView.findViewById(R.id.commentCountLabel);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);
        commentCountLabel.setText(post.commentCount+"");

        if(post.originalPoster.equals("anonym")){
            name_textView.setText(itemView.getResources().getString(R.string.anonym));
            Glide.with(itemView).load(R.drawable.anonym_user).into(
                    profilePicture_imageView);
        }else{
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    userObj = user;
                    post.user = user;
                    setName(post);
                }
            });
        }

        if(post.linkedFactId != null){
            setLinkedFact(post.linkedFactId);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String jsonObj = gson.toJson(post);
                Intent intent = new Intent(itemView.getContext(), RepostPostActivity.class );
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
        options.setVisibility(View.VISIBLE);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }

    public void setName(final RepostPost post){
        // sets up the users views
        try{
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
        } catch(IllegalArgumentException e){
            System.out.println("IllegalArgumentException RepostVH");
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
                    case R.id.report_post:
                        showReportDialog(post);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = menu.getMenuInflater();
        if(auth.getCurrentUser()!= null&& post.originalPoster.equals(auth.getCurrentUser().getUid())){
            inflater.inflate(R.menu.feed_post_menu_own, menu.getMenu());
        }else{
            inflater.inflate(R.menu.feed_post_menu_foreign, menu.getMenu());
        }
        menu.show();
    }

    public String getType(){
        return "repost";
    }
}

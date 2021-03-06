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
import com.imagine.myapplication.PostActivitys.YouTubePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.YouTubePost;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class YouTubeViewHolder extends CustomViewHolder {
    private static final String TAG = "YouTubeViewHolder";
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public YouTubePost post;
    public User userObj;
    public Post_Helper helper;

    public YouTubeViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final YouTubePost post){
        // call init method and sets up the post specific views
        init(post);
        helper = new Post_Helper(itemView.getContext());
        this.post = post;
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        YouTubePlayerView youTubePlayerView = itemView.findViewById(R.id.youtube_player);
        TextView commentCountLabel = itemView.findViewById(R.id.commentCountLabel);
        youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
            @Override
            public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
                Pattern compilesPattern = Pattern.compile(pattern);
                Matcher matcher = compilesPattern.matcher(post.link);
                if(matcher.find()){
                    String result = matcher.group();
                    youTubePlayer.cueVideo(result,0);
                }
            }
        });
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
                    post.user = user;
                    userObj = user;
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
                String objString = gson.toJson(post);
                Intent intent = new Intent(itemView.getContext(), YouTubePostActivity.class);
                intent.putExtra("post",objString);
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

    public void setName(final YouTubePost post){
        // sets up the users view
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
            System.out.println("IllegalArgumentException YouTubeVH");
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
        return "youTube";
    }
}

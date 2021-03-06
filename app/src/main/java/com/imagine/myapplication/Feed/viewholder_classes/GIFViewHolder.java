package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.PostActivitys.GifPostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.GIFPost;

import de.hdodenhof.circleimageview.CircleImageView;

public class GIFViewHolder extends CustomViewHolder {
    private static final String TAG = "GIFViewHolder";
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public GIFPost post;
    public User userObj;
    public Post_Helper helper;
    public int frameWidth;
    public int frameHeight;
    private MediaPlayer mediaPlayer;

    public GIFViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }



    public void bind(final GIFPost post){
        // calls the init method and sets up the post specific views
        this.post = post;
        helper = new Post_Helper(itemView.getContext());
        init(post);
        //GIF Widgets
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        TextView commentCountLabel = itemView.findViewById(R.id.commentCountLabel);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);

        final VideoView videoView = itemView.findViewById(R.id.gif_videoView);

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);
        commentCountLabel.setText(post.commentCount+"");

        videoView.setVideoPath(post.link);
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
        ConstraintLayout videoFrame = itemView.findViewById(R.id.video_frame);
        videoFrame.setClipToOutline(true);
        //Adjust the videoView to show the right ratio

        ViewTreeObserver viewTreeObserver = videoView.getViewTreeObserver();
        if(viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    videoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    frameWidth = videoView.getWidth();
                    frameHeight = videoView.getHeight();
                }
            });
        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0f,0f);

                //Get your video's width and height
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                //Get VideoView's current width and height
                float scale = (float) frameWidth / videoWidth;
                //For Center Crop use the Math.max to calculate the scale
                //float scale = Math.max(xScale, yScale);
                //For Center Inside use the Math.min scale.
                //I prefer Center Inside so I am using Math.min
                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;
                //Set the new size for the VideoView based on the dimensions of the video
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.width = (int)scaledWidth;
                layoutParams.height = (int)scaledHeight;
                videoView.setLayoutParams(layoutParams);
                videoView.start();
            }
        });
        ImageButton options = itemView.findViewById(R.id.feed_menu_button);
        options.setVisibility(View.VISIBLE);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
        CircleImageView topicPostImageView = itemView.findViewById(R.id.topicPostImageView);
        if (post.isTopicPost) {
            topicPostImageView.setVisibility(View.VISIBLE);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String objString = gson.toJson(post);
                Intent intent = new Intent(itemView.getContext(), GifPostActivity.class);
                intent.putExtra("post",objString);
                if(community != null && !community.equals("")){
                    String commString = gson.toJson(community);
                    intent.putExtra("comm",commString);
                }
                itemView.getContext().startActivity(intent);
            }
        });
    }

    public void setName(final GIFPost post){
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
            System.out.println("IllegalArgumentException GIFPostVH");
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
        return "GIF";
    }
}

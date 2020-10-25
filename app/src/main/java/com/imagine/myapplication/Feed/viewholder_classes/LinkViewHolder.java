package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Intent;
import android.net.Uri;
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
import com.imagine.myapplication.PostActivitys.LinkPostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.LinkPost;

import de.hdodenhof.circleimageview.CircleImageView;

public class LinkViewHolder extends CustomViewHolder {
    private static final String TAG = "LinkViewHolder";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public LinkPost post;
    public User userObj;
    public Post_Helper helper;

    public LinkViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final LinkPost post){
        // calls the init method and sets up the post specific views
        this.post = post;
        helper = new Post_Helper(itemView.getContext());
        init(post);
        resetPreview();
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        TextView commentCountLabel = itemView.findViewById(R.id.commentCountLabel);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        // PreView Widgets
        final ImageView preViewImage = itemView.findViewById(R.id.preView_image);
        final TextView preViewLink = itemView.findViewById(R.id.link_preView_link);
        final TextView previewTitle = itemView.findViewById(R.id.link_preView_title);
        final TextView previewDescription = itemView.findViewById(R.id.link_preView_description);

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);
        commentCountLabel.setText(post.commentCount+"");

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = post.link;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
            }
        };

        if(!post.linkImageURL.equals("")){
            Glide.with(itemView).load(post.linkImageURL).into(preViewImage);
        }else{
            Glide.with(itemView).load(R.drawable.link_preview_image).into(preViewImage);
        }
        preViewImage.setOnClickListener(listener);
        preViewLink.setOnClickListener(listener);
        previewDescription.setOnClickListener(listener);
        previewTitle.setOnClickListener(listener);
        preViewLink.setText(post.linkShortURL);
        previewTitle.setText(post.linkTitle);
        previewDescription.setText(post.linkDescription);
        preViewImage.setClipToOutline(true);
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
        setLinkedFact(post.linkedFactId);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String objString = gson.toJson(post);
                Intent intent = new Intent(itemView.getContext(), LinkPostActivity.class);
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

    public void setName(final LinkPost post){
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
            System.out.println("IllegalArgumentException LinkVH");
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
        return "link";
        }

    public void resetPreview(){
        // resets the link preview when the viewholder is recycled
        TextView linkPreviewText = itemView.findViewById(R.id.link_preView_link);
        ImageView linkPreviewImage = itemView.findViewById(R.id.preView_image);
        linkPreviewText.setText("");
        Glide.with(itemView).load(R.drawable.link_preview_image).into(linkPreviewImage);
    }
}





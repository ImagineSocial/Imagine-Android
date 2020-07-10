package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.PostActivitys.LinkPostActivity;
import com.imagine.myapplication.PostActivitys.MultiPicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.LinkPost;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

public class LinkViewHolder extends CustomViewHolder {
    private static final String TAG = "LinkViewHolder";
    public Context mContext;
    public User userObj;

    public LinkViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }


    public void bind(final LinkPost post){
        init(post);
        resetPreview();
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        // PreView Widgets
        final ImageView preViewImage = itemView.findViewById(R.id.preView_image);
        final TextView preViewLink = itemView.findViewById(R.id.preView_link);

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = post.link;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
            }
        };

        RichPreview richPreview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                String imageURL = metaData.getImageurl();
                String link = metaData.getSitename();
                if((imageURL != null) && (!imageURL.equals(""))){
                    Glide.with(itemView).load(imageURL).into(preViewImage);
                    preViewImage.setOnClickListener(listener);
                }
                if((link != null) && (!link.equals(""))){
                    preViewLink.setText(link);
                }
            }

            @Override
            public void onError(Exception e) {
                System.out.println("");
            }
        });
        preViewImage.setClipToOutline(true);
        richPreview.getPreview(post.link);

        if(post.originalPoster == "anonym"){
            name_textView.setText("Anonym");
            Glide.with(itemView).load(R.drawable.anonym_user).into(
                    profilePicture_imageView);
        }else{
            getUser(post.originalPoster, new UserCallback() {
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
                itemView.getContext().startActivity(intent);
            }
        });
    }
    public void setName(final LinkPost post){
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

    public String getType(){
        return "link";
        }

    public void resetPreview(){
        TextView linkPreviewText = itemView.findViewById(R.id.preView_link);
        ImageView linkPreviewImage = itemView.findViewById(R.id.preView_image);
        linkPreviewText.setText("");
        Glide.with(itemView).load(R.drawable.link_preview_image).into(linkPreviewImage);
    }
}





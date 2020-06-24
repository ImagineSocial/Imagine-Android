package com.imagine.myapplication.PostActivitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.imagine.myapplication.Comment;
import com.imagine.myapplication.CommentsCallback;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.Post_Fragment_Classes.LinkPostFragment;
import com.imagine.myapplication.Post_Fragment_Classes.ThoughtPostFragment;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.LinkPost;
import com.imagine.myapplication.post_classes.ThoughtPost;

import java.util.ArrayList;
import java.util.Date;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

public class LinkPostActivity extends AppCompatActivity {

    public ArrayList<Comment> comments;
    public LinkPost post;
    public Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_post);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.post_frame, new LinkPostFragment()).commit();

        Intent intent = getIntent();
        String objString = intent.getStringExtra("post");
        Gson gson = new Gson();
        post = gson.fromJson(objString, LinkPost.class);
        System.out.println("!");
    }

    @Override
    protected void onStart() {
        super.onStart();
        bind();
        Post_Helper helper = new Post_Helper();
        helper.getComments(post.documentID, new CommentsCallback() {
            @Override
            public void onCallback(ArrayList<Comment> comms) {
                System.out.println("!!");
                comments = comms;
                initRecyclerView();
            }
        });
    }

    public void bind(){
        TextView title_textView = findViewById(R.id.title_textView);
        TextView createTime_textView = findViewById(R.id.createDate_textView);
        TextView name_textView = findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = findViewById(
                R.id.profile_picture_imageView);
        // PreView Widgets
        final ImageView preViewImage = findViewById(R.id.preView_image);
        final TextView preViewLink = findViewById(R.id.preView_link);

        String dateString =dateToString(post.createTime);
        title_textView.setText(post.title);
        createTime_textView.setText(dateString);
        /*
        RichPreview richPreview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                String imageURL = metaData.getImageurl();
                String link = metaData.getSitename();
                if((imageURL != null) && (!imageURL.equals(""))){
                    Glide.with(mContext).load(imageURL).into(preViewImage);
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
        richPreview.getPreview(post.link);
         */
        if(post.originalPoster == "anonym"){
            name_textView.setText("Anonym");
            Glide.with(this).load(R.drawable.default_user).into(
                    profilePicture_imageView);
        }else{
            name_textView.setText(post.user.name);
            if(post.user.imageURL != null || !post.user.imageURL.equals("")){
                Glide.with(this).load(post.user.imageURL).into(
                        profilePicture_imageView);
            } else  {
                Glide.with(this).load(R.drawable.default_user).into(
                        profilePicture_imageView);
            }
        }
    }

    public void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.post_activity_recyclerView);
        Comments_Adapter adapter = new Comments_Adapter(comments,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public String dateToString(Timestamp timestamp){
        Date date = timestamp.toDate();
        return date.toString();
    }
}

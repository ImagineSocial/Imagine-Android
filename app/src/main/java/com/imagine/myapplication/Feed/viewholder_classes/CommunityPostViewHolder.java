package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_Items_Adapter;
import com.imagine.myapplication.Community.Community_ViewPager_Activity;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.LinkedCommunityCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.CommunityPost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CommunityPostViewHolder extends CustomViewHolder {

    public CommunityPost post;
    public Communities_Helper helper;
    public Post_Helper postHelper;
    public Community community;
    public ArrayList<Post> postList;
    public FirebaseAuth auth = FirebaseAuth.getInstance();

    public CommunityPostViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(final CommunityPost post){
        helper = new Communities_Helper(itemView.getContext());
        postHelper = new Post_Helper(itemView.getContext());
        this.post = post;
        init(post);
        if(post.linkedFactId != null){
            helper.fetchLinkedCommunity(post.linkedFactId, new LinkedCommunityCallback() {
                @Override
                public void onCallback(Community comm) {
                    community = comm;
                    setCommData();
                    setUpLinkedCommunity(community);
                }
            });
            postHelper.getPostsForCommunityFeed(post.linkedFactId, new FirebaseCallback() {
                @Override
                public void onCallback(ArrayList<Post> values) {
                    postList = values;
                    initRecyclerView();
                }
            });
        }
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        ImageView commentImage = itemView.findViewById(R.id.imageView3);
        TextView commentCountLabel = itemView.findViewById(R.id.commentCountLabel);
        commentCountLabel.setVisibility(View.GONE);
        commentImage.setVisibility(View.GONE);
        CardView cardView = itemView.findViewById(R.id.addOn_singleCommunity_cardView);
        cardView.setCardElevation(0);

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
        ImageButton options = itemView.findViewById(R.id.feed_menu_button);
        options.setVisibility(View.VISIBLE);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }

    public void setName(){
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
                        String userString = gson.toJson(post.user);
                        Intent intent = new Intent(mContext, UserActivity.class);
                        intent.putExtra("user",userString);
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch(IllegalArgumentException e){
            System.out.println("IllegalArgumentException CommunityVH");
        }
    }

    public void setCommData(){
        ImageView image = itemView.findViewById(R.id.addon_community_item_image);
        ConstraintLayout cl = itemView.findViewById(R.id.community_addOn_constraintLayout);
        if(this.community.imageURL != null && !this.community.equals("")){
            Glide.with(itemView).load(this.community.imageURL).into(image);
        }else{
            Glide.with(itemView).load(R.drawable.fact_stamp).into(image);
        }
        TextView title = itemView.findViewById(R.id.addon_community_item_title_label);
        TextView description = itemView.findViewById(R.id.addon_community_item_description_label);
        final TextView followerCountLabel = itemView.findViewById(R.id.addon_community_item_followercount_label);
        final TextView postCountLabel = itemView.findViewById(R.id.addon_community_item_postcount_label);
        title.setText(community.name);
        description.setText(community.description);
        postCountLabel.setText(itemView.getResources().getString(R.string.postcount)+community.postCount);
        followerCountLabel.setText(itemView.getResources().getString(R.string.followercount)+community.followerCount);
        cl.setVisibility(View.GONE);
    }


    public void initRecyclerView() {
        RecyclerView recyclerView = itemView.findViewById(R.id.community_addon_recyclerView);
        Community_Items_Adapter adapter = new Community_Items_Adapter(postList, itemView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
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

    public void setUpLinkedCommunity(final Community comm){
        final ImageView linkedTopicImageView = itemView.findViewById(R.id.topicImageView);
        if (comm.imageURL != "") {
            Glide.with(itemView).load(comm.imageURL).into(linkedTopicImageView);
        } else {
            Glide.with(itemView).load(R.drawable.fact_stamp).into(linkedTopicImageView);
        }
        linkedTopicImageView.setClipToOutline(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recentString = "";
                try{
                    FileInputStream inputStreamReader = mContext.openFileInput("recents.txt");
                    if(inputStreamReader != null){
                        InputStreamReader reader = new InputStreamReader(inputStreamReader);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String onjString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while((onjString = bufferedReader.readLine()) != null){
                            stringBuilder.append("\n").append(onjString);
                        }
                        inputStreamReader.close();
                        recentString = stringBuilder.toString();
                        Gson gson = new Gson();
                        Community[] recents = gson.fromJson(recentString,Community[].class);
                        ArrayList<Community> recentsList = new ArrayList<>();
                        int counter = 0;
                        for(Community comm : recents){
                            if(!comm.topicID.equals(comm.topicID)){
                                recentsList.add(comm);
                                counter++;
                                if(counter == 10) break;
                            }
                        }
                        recentsList.add(comm);
                        String newRecents = gson.toJson(recentsList);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext
                                .openFileOutput("recents.txt", Context.MODE_PRIVATE));
                        outputStreamWriter.write(newRecents);
                        outputStreamWriter.close();
                    }
                }
                catch(FileNotFoundException e){
                    Log.e("login activity", "File not found: " + e.toString());
                }
                catch(IOException e){
                    Log.e("login activity", "Can not read file: " + e.toString());
                }
                Intent intent = new Intent(itemView.getContext(), Community_ViewPager_Activity.class);
                Gson gson = new Gson();
                String jsonComm = gson.toJson(comm);
                intent.putExtra("comm", jsonComm);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}

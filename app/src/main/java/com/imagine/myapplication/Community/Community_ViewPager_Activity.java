package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

import java.util.HashMap;

public class Community_ViewPager_Activity extends AppCompatActivity {

    public String name;
    public String description;
    public String imageURL;
    public String commID;
    public String displayOption;
    public Communities_Fragment fragment;
    public Context mContext;
    public TestCollectionAdapter adapter;
    public ViewPager2 viewPager2;

    //Header
    public Button followButton;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_test);
        mContext = this;
        Intent intent = getIntent();
        this.name = intent.getStringExtra("name");
        this.description = intent.getStringExtra("description");
        this.imageURL = intent.getStringExtra("imageURL");
        this.commID = intent.getStringExtra("commID");
        this.displayOption = intent.getStringExtra("displayOption");

        HashMap<String,String> args = new HashMap<>();
        args.put("name",intent.getStringExtra("name"));
        args.put("description",intent.getStringExtra("description"));
        args.put("imageURL",intent.getStringExtra("imageURL"));
        args.put("commID",intent.getStringExtra("commID"));
        args.put("displayOption",intent.getStringExtra("displayOption"));
        this.viewPager2 = findViewById(R.id.containerViewPager);
        this.adapter = new TestCollectionAdapter(this,args);
        this.adapter.activity = this;
        this.viewPager2.setAdapter(this.adapter);
        if(intent.getStringExtra("displayOption").equals("fact")){
            this.viewPager2.setCurrentItem(1);
        }else{
            this.viewPager2.setCurrentItem(1);
        }

        Community community = new Community(name,imageURL,commID,description);
        setCommunityHeader(community);
    }

    public void setCommunityHeader(final Community community) {
        TextView title_tv = findViewById(R.id.comm_activity_title);
        TextView description_tv = findViewById(R.id.comm_activity_description);
        ImageView image_iv = findViewById(R.id.comm_activity_picture);
        View backgroundView = findViewById(R.id.comm_background_view);
        ImageButton newPostButton = findViewById(R.id.community_new_post_button);
        TabLayout tabLayout = findViewById(R.id.community_feed_tab_layout);
        if(this.displayOption.equals("fact")){
            new TabLayoutMediator(tabLayout, this.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    switch(position){
                        case 0:
                            tab.setText("Themen");
                            return;
                        case 1:
                            tab.setText("Diskussion");
                            return;
                        case 2:
                            tab.setText("Feed");
                            return;
                    }
                }
            }).attach();
        }else{
            new TabLayoutMediator(tabLayout, this.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    switch(position){
                        case 0:
                            tab.setText("Themen");
                            return;
                        case 1:
                            tab.setText("Feed");
                            return;
                    }
                }
            }).attach();
        }
        final Button followButton = findViewById(R.id.community_follow_button);
        this.followButton = followButton;
        final TextView followerCountLabel = findViewById(R.id.comm_header_follower_label);
        final TextView postCountLabel = findViewById(R.id.comm_header_post_count_label);
        backgroundView.setClipToOutline(true);
        image_iv.setClipToOutline(true);
        title_tv.setText(community.name);
        description_tv.setText(community.description);

        if(community.imageURL == null || community.imageURL.equals("")){
            Glide.with(this).load(R.drawable.placeholder_picture).into(image_iv);
        }else{
            Glide.with(this).load(community.imageURL).into(image_iv);
        }

        community.checkIfCommunityIsFollowed(new BooleanCallback() {
            @Override
            public void onCallback(Boolean bool) {
                followButton.setEnabled(true);
                if (bool) {
                    followButton.setText("Unfollow");
                    community.isBeingFollowed = true;
                }
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (community.isBeingFollowed) {
                    unfollowCommunityTapped(community);
                } else {
                    followCommunityTapped(community);
                }
            }
        });

        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,Community_New_Post.class);
                Gson gson = new Gson();
                String commString = gson.toJson(community);
                intent.putExtra("comm",commString);
                mContext.startActivity(intent);

            }
        });

        community.getFollowerCount(new IntegerCallback() {
            @Override
            public void onCallback(int count) {
                followerCountLabel.setText(count+"");
            }
        });
        community.getPostCount(new IntegerCallback() {
            @Override
            public void onCallback(int count) {
                postCountLabel.setText(count+"");
            }
        });
    }

    public void followCommunityTapped(final Community community) {
        if (currentUser != null) {
            DocumentReference topicRef = db.collection("Users").document(currentUser.getUid()).collection("topics").document(community.topicID);

            HashMap<String,Object> dataMap = new HashMap<>();
            dataMap.put("createDate", Timestamp.now());
            topicRef.set(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        updateFollowerCount(community,true);
                        followButton.setText("Unfollow");
                        community.isBeingFollowed = true;
                    }
                }
            });
        }
    }

    public void unfollowCommunityTapped(final Community community) {
        if (currentUser != null) {
            DocumentReference topicRef = db.collection("Users").document(currentUser.getUid()).collection("topics").document(community.topicID);
            topicRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        community.isBeingFollowed = false;
                        updateFollowerCount(community, false);
                        followButton.setText("Follow");
                    } else {
                        System.out.println("####Not deleted community");
                    }
                }
            });
        }
    }

    public void updateFollowerCount(Community community, Boolean follow) {
        if (currentUser != null) {
            DocumentReference ref = db.collection("Facts").document(community.topicID);

            if (follow) {
                ref.update("follower", FieldValue.arrayUnion(currentUser.getUid()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    System.out.println("Community follow list updated! ");
                                } else if(task.isCanceled()){
                                    System.out.println("Community follow list update failed! ");
                                }
                            }
                        });
            } else {
                ref.update("follower", FieldValue.arrayRemove(currentUser.getUid()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    System.out.println("Community follow list updated! ");
                                } else if(task.isCanceled()){
                                    System.out.println("Community follow list failed! ");
                                }
                            }
                        });
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("!");
    }
}

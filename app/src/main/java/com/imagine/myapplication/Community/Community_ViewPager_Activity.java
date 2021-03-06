package com.imagine.myapplication.Community;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.ImagineCommunity.InfoDialogFragment;
import com.imagine.myapplication.ImagineCommunity.InfoDialogType;
import com.imagine.myapplication.MainActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

import java.util.HashMap;
import java.util.Locale;

public class Community_ViewPager_Activity extends AppCompatActivity {

    public Community comm;
    public Communities_Fragment fragment;
    public Context mContext;
    public Community_Fragment_Adapter adapter;
    public ViewPager2 viewPager2;
    public Post_Helper helper;
    public FirebaseAuth auth = FirebaseAuth.getInstance();

    //Header
    public Button followButton;
    FirebaseUser currentUser = auth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_test);
        helper = new Post_Helper(this);
        mContext = this;
        Intent intent = getIntent();
        String jsonComm = intent.getStringExtra("comm");
        Gson gson = new Gson();
        this.comm = gson.fromJson(jsonComm, Community.class);


        HashMap<String,String> args = new HashMap<>();
        args.put("name", this.comm.name);
        args.put("description",this.comm.description);
        args.put("imageURL", this.comm.imageURL);
        args.put("commID", this.comm.topicID);
        args.put("displayOption", this.comm.displayOption);
        this.viewPager2 = findViewById(R.id.containerViewPager);
        this.adapter = new Community_Fragment_Adapter(this,args);
        this.adapter.activity = this;
        this.viewPager2.setAdapter(this.adapter);
        if(this.comm.displayOption.equals("fact")){
            this.viewPager2.setCurrentItem(1);
        }else{
            this.viewPager2.setCurrentItem(1);
        }

        setCommunityHeader(this.comm);

        String langPref = "info_communityHeader";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        Boolean alreadyLaunched = prefs.getBoolean(langPref, false);

        if (!alreadyLaunched) {
            InfoDialogFragment frag = new InfoDialogFragment(this);
            frag.type = InfoDialogType.communityHeader;
            frag.show();
        }
    }

    public void setCommunityHeader(final Community community) {
        TextView title_tv = findViewById(R.id.comm_activity_title);
        TextView description_tv = findViewById(R.id.comm_activity_description);
        ImageView image_iv = findViewById(R.id.comm_activity_picture);
        View backgroundView = findViewById(R.id.comm_background_view);
        ImageButton newPostButton = findViewById(R.id.community_new_post_button);
        ImageButton linkFeedButton = findViewById(R.id.link_comm_in_feed);
        TabLayout tabLayout = findViewById(R.id.community_feed_tab_layout);
        if(this.comm.displayOption.equals("fact")){
            new TabLayoutMediator(tabLayout, this.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    switch(position){
                        case 0:
                            tab.setText(getResources().getString(R.string.topics));
                            return;
                        case 1:
                            tab.setText(getResources().getString(R.string.discussion));
                            return;
                        case 2:
                            tab.setText(getResources().getString(R.string.feed));
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
                            tab.setText(getResources().getString(R.string.topics));
                            return;
                        case 1:
                            tab.setText(getResources().getString(R.string.feed));
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
        followerCountLabel.setText(community.followerCount+"");
        postCountLabel.setText(community.postCount+"");

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
                    followButton.setText(getResources().getString(R.string.unfollow));
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

        linkFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(auth.getCurrentUser() == null){
                    Toast.makeText(mContext,getResources().getString(R.string.community_viewpager_activity_login), Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText titleBox = new EditText(mContext);
                    titleBox.setHint(getResources().getString(R.string.community_viewpager_activity_title_hint));
                    layout.addView(titleBox);

                    final EditText descriptionBox = new EditText(mContext);
                    descriptionBox.setHint(getResources().getString(R.string.community_viewpager_activity_description_hint));
                    layout.addView(descriptionBox); // Another add method

                    dialog.setView(layout);
                    dialog.setTitle(getResources().getString(R.string.community_viewpager_activity_post_community_title));
                    dialog.setMessage(getResources().getString(R.string.community_viewpager_activity_post_community_message));
                    dialog.setPositiveButton(getResources().getString(R.string.community_viewpager_activity_post), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String description = descriptionBox.getText().toString();
                            String title = titleBox.getText().toString();
                            if (!title.equals("")) {
                                helper.linkCommunityInFeed(title, description, comm, new BooleanCallback() {
                                    @Override
                                    public void onCallback(Boolean bool) {
                                        if (bool) {
                                            Toast.makeText(mContext, getResources().getString(R.string.community_viewpager_activity_succ), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(mContext, getResources().getString(R.string.community_viewpager_activity_fail), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(mContext,getResources().getString(R.string.community_viewpager_activity_title), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.setNegativeButton(getResources().getString(R.string.community_viewpager_activity_abort), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    public void followCommunityTapped(final Community community) {
        if (currentUser != null) {
            DocumentReference topicRef = db.collection("Users").document(currentUser.getUid()).collection("topics").document(community.topicID);
            Configuration conf = MainActivity.configContext.getResources().getConfiguration();
            final Locale locale = conf.locale;
            HashMap<String,Object> dataMap = new HashMap<>();
            dataMap.put("createDate", Timestamp.now());
            dataMap.put("language",locale.getLanguage());
            topicRef.set(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        updateFollowerCount(community,true);
                        followButton.setText(getResources().getString(R.string.unfollow));
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

    public void getRecents(){
        String langPref = "recents";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
    }

    public void setRecents(){
        String langPref = "recents";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.commit();
    }
}

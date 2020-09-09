package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.CustomViewHolder;
import com.imagine.myapplication.R;

import java.util.HashMap;

public class Community_Feed_Header_Viewholder extends CustomViewHolder {
    private static final String TAG = "Community_Feed_Header_V";
    Context mContext;
    Button followButton;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // works as an header for the community_topic feed
    // shows the communityPicture and description

    public Community_Feed_Header_Viewholder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final Community community){
        TextView title_tv = itemView.findViewById(R.id.comm_activity_title);
        TextView description_tv = itemView.findViewById(R.id.comm_activity_description);
        ImageView image_iv = itemView.findViewById(R.id.comm_activity_picture);
        View backgroundView = itemView.findViewById(R.id.comm_background_view);
        ImageButton newPostButton = itemView.findViewById(R.id.community_new_post_button);
        final Button followButton = itemView.findViewById(R.id.community_follow_button);
        this.followButton = followButton;
        final TextView followerCountLabel = itemView.findViewById(R.id.comm_header_follower_label);
        final TextView postCountLabel = itemView.findViewById(R.id.comm_header_post_count_label);
        backgroundView.setClipToOutline(true);
        image_iv.setClipToOutline(true);
        title_tv.setText(community.name);
        description_tv.setText(community.description);

        if(community.imageURL == null || community.imageURL.equals("")){
            Glide.with(itemView).load(R.drawable.placeholder_picture).into(image_iv);
        }else{
            Glide.with(itemView).load(community.imageURL).into(image_iv);
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

                Intent intent = new Intent(itemView.getContext(),Community_New_Post.class);
                Gson gson = new Gson();
                String commString = gson.toJson(community);
                intent.putExtra("Comm",commString);
                itemView.getContext().startActivity(intent);
                
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
                                    System.out.println("Community follow list updated! "+TAG);
                                } else if(task.isCanceled()){
                                    System.out.println("Community follow list update failed! "+TAG);
                                }
                            }
                        });
            } else {
                ref.update("follower", FieldValue.arrayRemove(currentUser.getUid()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    System.out.println("Community follow list updated! "+TAG);
                                } else if(task.isCanceled()){
                                    System.out.println("Community follow list failed! "+TAG);
                                }
                            }
                        });
            }

        }
    }

}

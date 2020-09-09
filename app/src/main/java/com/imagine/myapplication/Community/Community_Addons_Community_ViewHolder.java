package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class Community_Addons_Community_ViewHolder extends Community_Addons_ViewHolder {

    public Community_Addons_Community_ViewHolder(@NonNull View itemView, Context context) {
        super(itemView, context);
    }

    public void bind(final Addon addon){
        this.addon = addon;
        this.recyclerView = itemView.findViewById(R.id.community_addon_recyclerView);
        fetchCommunity(addon.linkedFactID);
        TextView title = itemView.findViewById(R.id.community_addon_title);
        TextView description = itemView.findViewById(R.id.community_addon_description);

        title.setText(addon.headerTitle);
        description.setText(addon.description);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(addon.title);
                System.out.println(addon.refs);
            }
        });
    }

    public void fetchCommunity(String commID){
        db.collection("Facts").document(commID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot docSnap = task.getResult();
                            String name = docSnap.getString("name");
                            String imageURL = docSnap.getString("imageURL");
                            String topicID = docSnap.getId();
                            String description = docSnap.getString("description");
                            comm = new Community(name,imageURL,topicID,description);
                            setHeader();
                            fetchCommunityPosts();
                        }else if(task.isCanceled()){
                            System.out.println("!!");
                        }
                    }
                });
    }

    public void setHeader(){
        ImageView image = itemView.findViewById(R.id.community_addon_community_image);
        if(this.comm.imageURL != null && !this.comm.equals("")){
            Glide.with(itemView).load(this.comm.imageURL).into(image);
        }else{
            Glide.with(itemView).load(R.drawable.fact_stamp).into(image);
        }
        TextView title = itemView.findViewById(R.id.community_addOn_community_title);
        TextView description = itemView.findViewById(R.id.community_addOn_community_description);
        final TextView followerCountLabel = itemView.findViewById(R.id.addOn_community_followerCount_label);
        final TextView postCountLabel = itemView.findViewById(R.id.addOn_community_postCount_label);
        title.setText(comm.name);
        description.setText(comm.description);

        comm.getPostCount(new IntegerCallback() {
            @Override
            public void onCallback(int count) {
                postCountLabel.setText("Beiträge: "+count);
            }
        });
        comm.getFollowerCount(new IntegerCallback() {
            @Override
            public void onCallback(int count) {
                followerCountLabel.setText("Follower: "+count);
            }
        });
    }

    public void fetchCommunityPosts(){
        Post_Helper helper = new Post_Helper();
        helper.getPostsForCommunityFeed(this.comm.topicID, new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                System.out.println("!");
                for(Post post : values){
                    communityPosts.add(post);
                }
                initRecyclerView();
            }
        });
    }

    @Override
    public void initRecyclerView() {
        Community_Items_Adapter adapter = new Community_Items_Adapter(communityPosts, itemView.getContext());
        this.recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    }
}

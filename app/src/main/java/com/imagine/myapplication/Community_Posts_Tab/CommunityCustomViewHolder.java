package com.imagine.myapplication.Community_Posts_Tab;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.LinkedCommunityCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.DefaultPost;

public class CommunityCustomViewHolder extends RecyclerView.ViewHolder {

    public Communities_Helper comm_helper = new Communities_Helper();
    public Post_Helper post_helper = new Post_Helper();
    public Community community;

    public CommunityCustomViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(DefaultPost post){
        TextView defaultTextVIew = itemView.findViewById(R.id.community_default_textView);
        defaultTextVIew.setText(post.documentID);
    }

    public void setLinkedFact(final String commID){
        comm_helper.fetchLinkedCommunity(commID, new LinkedCommunityCallback() {
            @Override
            public void onCallback(Community comm) {
                community = comm;
            }
        });
    }
}

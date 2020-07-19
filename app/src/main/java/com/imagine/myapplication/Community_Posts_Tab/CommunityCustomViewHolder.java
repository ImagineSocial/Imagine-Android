package com.imagine.myapplication.Community_Posts_Tab;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.DefaultPost;

public class CommunityCustomViewHolder extends RecyclerView.ViewHolder {
    public CommunityCustomViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(DefaultPost post){
        TextView defaultTextVIew = itemView.findViewById(R.id.community_default_textView);
        defaultTextVIew.setText(post.documentID);
    }
}

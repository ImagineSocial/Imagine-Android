package com.imagine.myapplication.Community;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

public class Communities_Sub_Header extends Community_ViewHolder {

    public Communities_Sub_Header(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Community comm) {
        TextView header = itemView.findViewById(R.id.communities_sub_header);
        switch(comm.type){
            case "topicsHeader":
                header.setText(R.string.communities_sub_header_trending);
                break;
            case "factsHeader":
                header.setText(R.string.communities_sub_header_current_discussions);
                break;
            case "ownHeader":
                header.setText(R.string.communities_sub_header_followed_communities);
                break;
        }
    }
}
package com.imagine.myapplication.Community;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

public class Communities_Header extends Community_ViewHolder {
    public Communities_Header(@NonNull View itemView, Communities_Fragment fragment) {
        super(itemView,fragment);
    }

    @Override
    public void bind(Community comm) {
        TextView header = itemView.findViewById(R.id.communities_header);
        header.setText("Communities");
    }
}

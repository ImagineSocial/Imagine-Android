package com.imagine.myapplication.Community;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.gson.internal.$Gson$Preconditions;
import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

public class Community_Footer_Viewholder extends Community_ViewHolder {
    public Community_Footer_Viewholder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Community comm) {
        Button addButton = itemView.findViewById(R.id.footer_addButton);
        Button allButton = itemView.findViewById(R.id.footer_showAllButton);

        if(comm.topicID.equals("topicsFooter")){
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),Community_New_Community_Activity.class);
                    intent.putExtra("type","topic");
                    itemView.getContext().startActivity(intent);
                }
            });
            allButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),Community_All_Communities_Activity.class);
                    intent.putExtra("type","topic");
                    itemView.getContext().startActivity(intent);
                }
            });
        }else{
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),Community_New_Community_Activity.class);
                    intent.putExtra("type","fact");
                    itemView.getContext().startActivity(intent);
                }
            });
            allButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),Community_All_Communities_Activity.class);
                    intent.putExtra("type","fact");
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}

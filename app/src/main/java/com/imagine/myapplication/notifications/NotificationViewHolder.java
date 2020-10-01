package com.imagine.myapplication.notifications;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void bind(Notification not){
        TextView title = itemView.findViewById(R.id.notification_title_textView);
        TextView body = itemView.findViewById(R.id.notification_body_titleView);

        body.setText(not.title);

        switch (not.type) {
            case "upvote":
                title.setText("Dein Bild wurde "+not.count+ " mal geliked:");
                break;
            case "comment":
                title.setText("Dein Beitrag wurde " + not.count+" mal kommentiert:");
                body.setText(not.comment);
                break;
            case "friend":
                title.setText("Du hast eine Freundschaftsanfrage:");
                body.setText(not.friendRequestName+" möchte mit dir befreundet sein");
                break;
        }
    }
}

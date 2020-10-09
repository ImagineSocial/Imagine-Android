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
                title.setText(itemView.getResources().getString(R.string.notification_viewholder_post)+not.count+ itemView.getResources().getString(R.string.notification_viewholder_upvote));
                break;
            case "comment":
                title.setText(itemView.getResources().getString(R.string.notification_viewholder_post) + not.count+itemView.getResources().getString(R.string.notification_viewholder_comment));
                body.setText(not.comment);
                break;
            case "friend":
                title.setText(itemView.getResources().getString(R.string.notification_viewholder_friend_title));
                body.setText(not.friendRequestName+itemView.getResources().getString(R.string.notification_viewholder_friend_body));
                break;
        }
    }
}

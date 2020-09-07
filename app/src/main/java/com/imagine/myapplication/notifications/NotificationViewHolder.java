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
        title.setText(not.title);
        body.setText(not.message);
    }
}

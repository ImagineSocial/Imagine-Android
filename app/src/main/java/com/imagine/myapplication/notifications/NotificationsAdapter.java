package com.imagine.myapplication.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

    public ArrayList<Notification> nots;
    public Context mContext;

    public NotificationsAdapter(ArrayList<Notification> nots, Context mContext) {
        this.nots = nots;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.notification_cell,parent,false);
        NotificationViewHolder holder = new NotificationViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(nots.get(position));
    }

    @Override
    public int getItemCount() {
        return nots.size();
    }
}

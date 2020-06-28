package com.imagine.myapplication.user_classes;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imagine.myapplication.Feed.viewholder_classes.CustomViewHolder;
import com.imagine.myapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Feed_Header_Viewholder extends CustomViewHolder {

    Context mContext;

    public User_Feed_Header_Viewholder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(User user) {
        CircleImageView profileImageView = itemView.findViewById(R.id.userHeaderProfileImageView);
        TextView nameLabel = itemView.findViewById(R.id.userHeaderNameTextView);
        TextView statusTextLabel = itemView.findViewById(R.id.userHeaderStatusTextView);
        CircleImageView changeProfilePicture = itemView.findViewById(R.id.userHeaderChangeProfilePicture);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.getUid() == user.userID) {
                changeProfilePicture.setVisibility(View.VISIBLE);
                changeProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO Change Profile Picture
                    }
                });
            }
        }

        nameLabel.setText(user.name);

        if (user.imageURL != "") {
            Glide.with(itemView).load(user.imageURL).into(profileImageView);
        }
        if (user.statusQuote != "") {
            statusTextLabel.setText(user.statusQuote);
        } //else { setDefaultQuote and edit it the way you want if you are THE user

    }
}

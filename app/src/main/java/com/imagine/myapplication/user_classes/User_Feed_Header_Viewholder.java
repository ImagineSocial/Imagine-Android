package com.imagine.myapplication.user_classes;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imagine.myapplication.Community.IntegerCallback;
import com.imagine.myapplication.Feed.viewholder_classes.CustomViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.MainActivity;
import com.imagine.myapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Feed_Header_Viewholder extends CustomViewHolder {

    private static final String TAG = "User_Feed_Header_Viewholder";
    public Context mContext;
    public UserActivity activity;
    public final int GALLERY = 1;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public User_Feed_Header_Viewholder(@NonNull View itemView, UserActivity activity) {
        super(itemView);
        this.mContext = itemView.getContext();
        this.activity = activity;
    }

    public void bind(User user) {
        ImageView profileImageView = itemView.findViewById(R.id.userHeaderProfileImageView);
        TextView nameLabel = itemView.findViewById(R.id.userHeaderNameTextView);
        TextView statusTextLabel = itemView.findViewById(R.id.userHeaderStatusTextView);
        CircleImageView changeProfilePicture = itemView.findViewById(R.id.changeProfilePicture);
        CircleImageView changeProfilePictureBackground = itemView.findViewById(R.id.changeProfilePictureBackground);
        final TextView postCountLabel = itemView.findViewById(R.id.user_header_post_count_label);
        profileImageView.setClipToOutline(true);

        user.getPostCount(new IntegerCallback() {
            @Override
            public void onCallback(int count) {
                postCountLabel.setText(count+"");
            }
        });

        FirebaseUser currentUser = auth.getCurrentUser();
        activity.setHeader(this);
        Button logout_button = itemView.findViewById(R.id.user_header_logout_button);

        if (currentUser != null) {
            if (currentUser.getUid().equals(user.userID)) {
                logout_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        MainActivity.resetFragmentsAfterLogin();
                        activity.finish();


                    }
                });
                logout_button.setVisibility(View.VISIBLE);

                changeProfilePicture.setVisibility(View.VISIBLE);
                changeProfilePictureBackground.setVisibility(View.VISIBLE);
                changeProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(intent,GALLERY);
                    }
                });
            }
        }


        nameLabel.setText(user.name);

        if (user.imageURL.equals("")){
            Glide.with(itemView).load(R.drawable.default_user).into(profileImageView);
        }else{
            Glide.with(itemView).load(user.imageURL).into(profileImageView);
        }
        if (user.statusQuote.equals("")) {
            statusTextLabel.setText("");
        }else {
            statusTextLabel.setText(user.statusQuote);
        }
    }

    public void reloadPicture(){
        //reloads the users profilePicture in the header
        // when the picture is changed
        FirebaseUser user = auth.getCurrentUser();
        String imageURL = user.getPhotoUrl().toString();
        CircleImageView profileImageView = itemView.findViewById(R.id.userHeaderProfileImageView);
        if (!imageURL.equals("")) {
            Glide.with(itemView).load(imageURL).into(profileImageView);
        }
    }
}

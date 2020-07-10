package com.imagine.myapplication.Post_Fragment_Classes;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imagine.myapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThoughtPostFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_thought,container,false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        CircleImageView imageView = view.findViewById(R.id.profile_picture_imageView);
        TextView nameTextView = view.findViewById(R.id.name_textView);
        Uri url = user.getPhotoUrl();

        String name = user.getDisplayName();
        if (user != null) {
            Glide.with(view).load(url).into(imageView);
            nameTextView.setText(name);
        }
        return view;
    }
}

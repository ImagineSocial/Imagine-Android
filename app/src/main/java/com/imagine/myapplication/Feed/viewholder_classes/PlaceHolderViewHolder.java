package com.imagine.myapplication.Feed.viewholder_classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;

public class PlaceHolderViewHolder extends CustomViewHolder {
    public PlaceHolderViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(){
        ImageView image = itemView.findViewById(R.id.placeholder_imageview);
        TextView description = itemView.findViewById(R.id.placeholder_description_label);
        Glide.with(itemView).load(R.drawable.placeholder_picture).into(image);
        description.setText("Hier gibt es leider noch nichts zu sehen.");
    }
}

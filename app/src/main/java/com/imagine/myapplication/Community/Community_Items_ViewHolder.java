package com.imagine.myapplication.Community;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.PicturePost;
import com.imagine.myapplication.post_classes.Post;

public class Community_Items_ViewHolder extends RecyclerView.ViewHolder {
    public PicturePost post;
    public Community comm;

    public Community_Items_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Object object){
        if(object instanceof PicturePost){
            this.post = (PicturePost) object;
            TextView title = itemView.findViewById(R.id.community_item_title);
            TextView description = itemView.findViewById(R.id.community_item_description);
            ImageView image = itemView.findViewById(R.id.community_item_image);
            title.setText(this.post.title);
            description.setText(this.post.description);
            Glide.with(itemView).load(this.post.imageURL).into(image);
        }else if(object instanceof Community){

        }
    }
}

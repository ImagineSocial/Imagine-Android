package com.imagine.myapplication.Community;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;

public class Community_Addons_PreView_ViewHolder extends RecyclerView.ViewHolder {
    public Community_Addons_PreView_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(PreView preView){
        ImageView image = itemView.findViewById(R.id.new_addOn_viewholder_previewImageView);
        TextView text = itemView.findViewById(R.id.new_addOn_viewholder_titleLabel);
        switch (preView.type){
            case "one":
                Glide.with(itemView).load(R.drawable.add_on_horizontal_scroll_example).into(image);
                break;
            case "two":
                Glide.with(itemView).load(R.drawable.add_on_qanda_example).into(image);
                break;
            case "three":
                Glide.with(itemView).load(R.drawable.add_on_single_topic_example).into(image);
                break;
            default:
                Glide.with(itemView).load(R.drawable.add_on_horizontal_scroll_example).into(image);
                break;
        }
    }
}

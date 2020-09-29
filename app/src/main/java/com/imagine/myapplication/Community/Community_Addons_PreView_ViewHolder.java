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
        TextView title = itemView.findViewById(R.id.new_addOn_viewholder_titleLabel);
        TextView description = itemView.findViewById(R.id.new_addOn_viewholder_description_label);
        switch (preView.type){
            case "one":
                Glide.with(itemView).load(R.drawable.add_on_horizontal_scroll_example).into(image);
                title.setText(R.string.addOn_collection_header);
                description.setText(R.string.addOn_collection_description);
                break;
            case "two":
                Glide.with(itemView).load(R.drawable.add_on_qanda_example).into(image);
                title.setText(R.string.addOn_QandA_header);
                description.setText(R.string.addOn_QandA_description);
                break;
            case "three":
                Glide.with(itemView).load(R.drawable.add_on_single_topic_example).into(image);
                title.setText(R.string.addOn_singleTopic_header);
                description.setText(R.string.addOn_singleTopic_description);
                break;
            default:
                Glide.with(itemView).load(R.drawable.add_on_horizontal_scroll_example).into(image);
                break;
        }
    }
}

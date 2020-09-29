package com.imagine.myapplication.Community;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Addon_Feed_Activity extends AppCompatActivity {

    public static Addon addon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addon_feed_activity);

        ImageView headerImageView = findViewById(R.id.addon_feed_header_image);
        TextView titleLabel = findViewById(R.id.addon_feed_header_title);
        TextView descriptionLabel = findViewById(R.id.addon_feed_header_description);

        titleLabel.setText(addon.title);
        descriptionLabel.setText(addon.description);

        headerImageView.setClipToOutline(true);
        if (addon.imageURL != null && !addon.imageURL.equals("")) {
            Glide.with(this).load(addon.imageURL).into(headerImageView);
        } else {
            ConstraintLayout.LayoutParams imageViewParams = (ConstraintLayout.LayoutParams) headerImageView.getLayoutParams();
            imageViewParams.height = 0;
            headerImageView.setLayoutParams(imageViewParams);
        }

        this.initRecyclerView();
    }

    public void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.addon_feed_recyclerview);
        FeedAdapter adapter = new FeedAdapter(addon.items,this);
        adapter.loadHeader = false;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Addon_Feed_Activity.addon = null;
    }
}

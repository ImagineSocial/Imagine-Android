package com.imagine.myapplication.ImagineCommunity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;


public class InfoDialogFragment extends AlertDialog {

    public InfoDialogType type = InfoDialogType.intro;
    int currentItem = 1;

    int[] introViews = {R.drawable.intro_first, R.drawable.intro_second, R.drawable.intro_third, R.drawable.intro_fourth, R.drawable.intro_fifth, R.drawable.intro_sixth};
    int infoView = R.drawable.info_view_new_post;

    public InfoDialogFragment(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_dialog_fragment);

        Button backButton = findViewById(R.id.info_dialog_back_button);
        final Button nextButton = findViewById(R.id.info_dialog_next_button);
        final CarouselView carouselView = findViewById(R.id.info_dialog_carouselView);

        if (type == InfoDialogType.intro) {
            carouselView.setPageCount(introViews.length);
        } else {
            backButton.setVisibility(View.INVISIBLE);
            nextButton.setText(getContext().getResources().getString(R.string.done));
            carouselView.setPageCount(1);

            switch (type) {
                case communityHeader:
                    infoView = R.drawable.info_view_community_header;
                    break;
                case likes:
                    infoView = R.drawable.info_view_likes;
                    break;
            }
        }
        carouselView.setPageTransformInterval(600);

        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if (type == InfoDialogType.intro) {
                    Glide.with(getContext()).load(introViews[position]).into(imageView);
                } else {
                    Glide.with(getContext()).load(infoView).into(imageView);
                }
            }
        });
        carouselView.setClipToOutline(true);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getContext().getSharedPreferences("CommonPrefs",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (type == InfoDialogType.intro) {
                    if (currentItem != introViews.length) {
                        carouselView.setCurrentItem(currentItem);
                        currentItem = currentItem + 1;
                        if (currentItem == introViews.length) {
                            nextButton.setText(getContext().getResources().getString(R.string.done));
                        }
                    } else {
                        editor.putBoolean("info_intro", true);
                        editor.apply();

                        dismiss();
                    }
                } else {
                    switch (type) {
                        case likes:
                            editor.putBoolean("info_likes", true);
                        case communityHeader:
                            editor.putBoolean("info_communityHeader", true);
                        case newPost:
                            editor.putBoolean("info_newPost", true);
                    }
                    editor.apply();

                    dismiss();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem != 0) {
                    carouselView.setCurrentItem(currentItem);
                    currentItem = currentItem - 1;
                }
            }
        });
    }
}

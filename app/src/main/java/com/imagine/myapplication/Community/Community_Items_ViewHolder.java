package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.PostActivitys.PicturePostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.LinkPost;
import com.imagine.myapplication.post_classes.MultiPicturePost;
import com.imagine.myapplication.post_classes.PicturePost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.post_classes.ThoughtPost;
import com.imagine.myapplication.post_classes.YouTubePost;
import com.imagine.myapplication.user_classes.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Community_Items_ViewHolder extends RecyclerView.ViewHolder {
    public Post post;
    public Community comm;
    public Context mContext;
    public Post_Helper helper = new Post_Helper();
    public TextView title;
    public ImageView image;


    public Community_Items_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Object object){
        this.mContext = itemView.getContext();
        if (object instanceof PicturePost) {
            PicturePost post = (PicturePost) object;
            this.bindPicturePost(post);
            hideSmallBackgroundView();
        } else if (object instanceof Community) {
            Community community = (Community) object;
            bindCommunity(community);
        } else if (object instanceof LinkPost) {
            LinkPost post = (LinkPost) object;
            bindLinkPost(post);
            setSmallBackgroundView();
        } else if (object instanceof GIFPost) {
            GIFPost post = (GIFPost) object;
            bindGIFPost(post);
            setSmallBackgroundView();
        } else if (object instanceof MultiPicturePost) {
            MultiPicturePost post = (MultiPicturePost) object;
            bindMultiPicturePost(post);
            setSmallBackgroundView();
        } else if (object instanceof ThoughtPost) {
            TextView title = itemView.findViewById(R.id.addon_post_item_title);
            title.getLayoutParams().height = RecyclerView.LayoutParams.MATCH_PARENT;
            ThoughtPost post = (ThoughtPost) object;
            bindThoughtPost(post);
            hideSmallBackgroundView();
        } else if (object instanceof YouTubePost) {
            YouTubePost post = (YouTubePost) object;
            bindYouTubePost(post);
            setSmallBackgroundView();
        }
    }

    public void hideSmallBackgroundView() {
        ConstraintLayout backgroundView = itemView.findViewById(R.id.addon_item_small_backgroundview);
        ImageView imageView = itemView.findViewById(R.id.addon_item_small_imageview);
        backgroundView.setVisibility(View.INVISIBLE);
        imageView.setImageDrawable(null);
    }

    public void setSmallBackgroundView() {
        ConstraintLayout backgroundView = itemView.findViewById(R.id.addon_item_small_backgroundview);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(30);   //View is 30x30

        shape.setColor(ContextCompat.getColor(mContext, R.color.grey));
        backgroundView.setBackground(shape);
        backgroundView.setClipToOutline(true);
        backgroundView.setVisibility(View.VISIBLE);
        backgroundView.setAlpha(0.85f);
    }

    public void bindCommunity(final Community community) {
        TextView titleLabel = itemView.findViewById(R.id.addon_community_item_title_label);
        TextView descriptionLabel = itemView.findViewById(R.id.addon_community_item_description_label);
        TextView followerCountLabel = itemView.findViewById(R.id.addon_community_item_followercount_label);
        TextView postCountLabel = itemView.findViewById(R.id.addon_community_item_postcount_label);
        ImageView addonImageView = itemView.findViewById(R.id.addon_community_item_image);

        titleLabel.setText(community.name);
        descriptionLabel.setText(community.description);
        postCountLabel.setText("Beiträge: "+community.postCount);
        followerCountLabel.setText("Follower: "+community.followerCount);

        if(community.imageURL != null && !community.equals("")){
            Glide.with(itemView).load(community.imageURL).into(addonImageView);
        }else{
            Glide.with(itemView).load(R.drawable.default_image).into(addonImageView);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Community_ViewPager_Activity.class);
                Gson gson = new Gson();
                String jsonComm = gson.toJson(community);
                intent.putExtra("comm", jsonComm);
                mContext.startActivity(intent);
            }
        });
    }

    public void bindPicturePost(final PicturePost post){
        this.title = itemView.findViewById(R.id.addon_post_item_title);
        this.image = itemView.findViewById(R.id.addon_post_item_imageView);
        image.setClipToOutline(true);
        title.setText(post.title);
        Glide.with(itemView).load(post.imageURL).into(image);

        if(post.user == null){
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                }
            });
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost(post);
            }
        });
    }

    public void bindLinkPost(final LinkPost post) {
        this.title = itemView.findViewById(R.id.addon_post_item_title);
        this.image = itemView.findViewById(R.id.addon_post_item_imageView);
        ImageView imageView= itemView.findViewById(R.id.addon_item_small_imageview);
        image.setClipToOutline(true);
        title.setText(post.title);
        if (!post.linkImageURL.equals("")) {
            Glide.with(itemView).load(post.linkImageURL).into(image);
        } else {
            Glide.with(itemView).load(R.drawable.link_preview_image).into(image);
        }
        Glide.with(itemView).load(R.drawable.internet_globe).into(imageView);
        if(post.user == null){
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                }
            });
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost(post);
            }
        });
    }

    public void bindYouTubePost(final YouTubePost post) {
        this.title = itemView.findViewById(R.id.addon_post_item_title);
        this.image = itemView.findViewById(R.id.addon_post_item_imageView);
        ImageView imageView= itemView.findViewById(R.id.addon_item_small_imageview);
        image.setClipToOutline(true);
        title.setText(post.title);

        if (!post.link.equals("")) {
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
            Pattern compilesPattern = Pattern.compile(pattern);
            Matcher matcher = compilesPattern.matcher(post.link);
            if(matcher.find()){
                String result = matcher.group();
                String previewLink = "https://img.youtube.com/vi/"+result+"/sddefault.jpg";
                Glide.with(itemView).load(previewLink).into(imageView);
                System.out.println("## Das ist die Preview: "+previewLink);
            }
        } else {
            Glide.with(itemView).load(R.drawable.link_preview_image).into(imageView);
        }
        Glide.with(itemView).load(R.drawable.internet_globe).into(imageView);
        if(post.user == null){
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                }
            });
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost(post);
            }
        });
    }

    public void bindMultiPicturePost(final MultiPicturePost post) {
        this.title = itemView.findViewById(R.id.addon_post_item_title);
        this.image = itemView.findViewById(R.id.addon_post_item_imageView);
        ImageView imageView= itemView.findViewById(R.id.addon_item_small_imageview);
        image.setClipToOutline(true);
        title.setText(post.title);
        Glide.with(itemView).load(R.drawable.multipicture_icon).into(imageView);

        if (post.imageURLs.length != 0) {
            String imageURL = post.imageURLs[0];
            Glide.with(itemView).load(imageURL).into(image);
        }
        if(post.user == null){
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                }
            });
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost(post);
            }
        });
    }

    public void bindGIFPost(final GIFPost post) {
        this.title = itemView.findViewById(R.id.addon_post_item_title);
        this.image = itemView.findViewById(R.id.addon_post_item_imageView);
        ImageView imageView= itemView.findViewById(R.id.addon_item_small_imageview);
        image.setClipToOutline(true);
        title.setText(post.title);
        Glide.with(itemView).load(R.drawable.gif_icon).into(imageView);

        if (!post.link.equals("")) {
            Glide.with(itemView).load(post.link).into(image);
        }

        if(post.user == null){
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                }
            });
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost(post);
            }
        });
    }

    public void bindThoughtPost(final ThoughtPost post) {
        this.title = itemView.findViewById(R.id.addon_post_item_title);
        title.setText(post.title);
        title.setMaxLines(10);
        title.setTextSize(16);
        if(post.user == null){
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                }
            });
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost(post);
            }
        });
    }

    public void goToPost(Post post) {
        Gson gson = new Gson();
        String jsonObj = gson.toJson(post);
        Intent intent = new Intent(itemView.getContext(), PicturePostActivity.class);
        intent.putExtra("post",jsonObj);
        mContext.startActivity(intent);
    }

}

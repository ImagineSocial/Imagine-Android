package com.imagine.myapplication.Community_Posts_Tab;

import android.content.Context;
import android.graphics.Picture;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.imagine.myapplication.Community.Community_ViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.CustomViewHolder;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.DefaultPost;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.MultiPicturePost;
import com.imagine.myapplication.post_classes.PicturePost;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class Community_Posts_Adapter extends RecyclerView.Adapter<CommunityCustomViewHolder> {
    private static final String TAG = "Community_Posts_Adapter";
    ArrayList<Post> postArrayList = new ArrayList<>();
    public Context mContext;

    public Community_Posts_Adapter(ArrayList<Post> postArrayList, Context context) {
        this.postArrayList = postArrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CommunityCustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        switch(viewType){
            case R.layout.community_gif:
                view = inflater.inflate(R.layout.community_gif,parent,false);
                int spacing = 2;
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                int size = ((parent.getWidth() / 3) - spacing);
                layoutParams.height = size;
                layoutParams.width = size;
                view.setLayoutParams(layoutParams);
                return new CommunityGIFViewHolder(view);
            case R.layout.community_picture:
                view = inflater.inflate(R.layout.community_picture, parent, false);
                int spacing1 = 2;
                ViewGroup.LayoutParams layoutParams1 = view.getLayoutParams();
                int size1 = ((parent.getWidth() / 3) - spacing1);
                layoutParams1.height = size1;
                layoutParams1.width = size1;
                view.setLayoutParams(layoutParams1);
                return new CommunityPictureViewHolder(view);
            case R.layout.community_multipicture:
                view = inflater.inflate(R.layout.community_multipicture,parent,false);
                int spacing2 = 2;
                ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
                int size2 = ((parent.getWidth() / 3) - spacing2);
                layoutParams2.height = size2;
                layoutParams2.width = size2;
                view.setLayoutParams(layoutParams2);
                return new CommunityMultiPictureViewHolder(view);
            default:
                view = inflater.inflate(R.layout.community_default,parent, false);
                return new CommunityCustomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityCustomViewHolder holder, int position) {
        Post post = postArrayList.get(position);
        if(holder instanceof CommunityGIFViewHolder){
            ((CommunityGIFViewHolder)holder).bind((GIFPost) post);
        }else if(holder instanceof CommunityPictureViewHolder){
            ((CommunityPictureViewHolder)holder).bind((PicturePost) post);
        } else if ( holder instanceof CommunityMultiPictureViewHolder){
            ((CommunityMultiPictureViewHolder)holder).bind((MultiPicturePost) post);
        } else {
            holder.bind((DefaultPost) post);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Post post = postArrayList.get(position);
        switch(post.type){
            case "GIF":
                return R.layout.community_gif;
            case "picture":
                return R.layout.community_picture;
            case "multiPicture":
                return R.layout.community_multipicture;
            default:
                System.out.println("Default Case! "+TAG);
                return R.layout.community_default;
        }
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
}

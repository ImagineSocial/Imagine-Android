package com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Feed.viewholder_classes.CommunityPostViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.HeaderViewHolder;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.CommunityPost;
import com.imagine.myapplication.post_classes.DefaultPost;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.LinkPost;
import com.imagine.myapplication.post_classes.MultiPicturePost;
import com.imagine.myapplication.post_classes.PicturePost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.post_classes.RepostPost;
import com.imagine.myapplication.post_classes.ThoughtPost;
import com.imagine.myapplication.post_classes.TranslationPost;
import com.imagine.myapplication.post_classes.YouTubePost;
import com.imagine.myapplication.Feed.viewholder_classes.CustomViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.DefaultViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.GIFViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.LinkViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.MultiPictureViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.PictureViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.RepostViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.ThoughtViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.TranslationViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.YouTubeViewHolder;

import java.util.ArrayList;

import javax.xml.XMLConstants;

public class FeedAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    private static final String TAG = "FeedAdapter";
    public ArrayList<Post> postList;
    public Context mContext;
    public HeaderViewHolder header;
    public Activity mainActivity;
    public boolean loadHeader = true;

    public FeedAdapter(ArrayList<Post> postList, Context mContext) {
        this.postList = postList;
        this.mContext = mContext;
    }

    public void addMorePosts(ArrayList<Post> posts){
        postList.addAll(posts);
        System.out.println("Posts geadded!");
    }

    public void refreshPosts(ArrayList<Post> posts){
        postList = posts;
    }
    @Override
    public int getItemViewType(int position) {
        if(position == 0 && loadHeader){
            return R.layout.post_top_header;
        }
        String type;
        if(loadHeader){
            type = postList.get(position-1).type;
        }else{
            type = postList.get(position).type;
        }
        switch(type){
            case "picture":
                return R.layout.post_picture;
            case "thought":
                return R.layout.post_thought;
            case "link":
                return R.layout.post_link;
            case "youTubeVideo":
                return R.layout.post_youtube;
            case "GIF":
                return R.layout.post_gif;
            case "multiPicture":
                return R.layout.post_multi_picture;
            case "translation":
                return R.layout.post_translation;
            case "repost":
                return R.layout.post_repost;
            case "comm":
                return R.layout.post_community;
            default:
                return R.layout.post_default;
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        switch(viewType){
            case R.layout.post_picture:
                view = inflater.inflate(R.layout.post_picture,parent,false);
                PictureViewHolder pictureViewHolder = new PictureViewHolder(view);
                pictureViewHolder.mainActivty = this.mainActivity;
                return pictureViewHolder;
            case R.layout.post_thought:
                view = inflater.inflate(R.layout.post_thought,parent,false);
                ThoughtViewHolder thoughtViewHolder = new ThoughtViewHolder(view);
                thoughtViewHolder.mainActivty = this.mainActivity;
                return thoughtViewHolder;
            case R.layout.post_link:
                view = inflater.inflate(R.layout.post_link,parent,false);
                LinkViewHolder linkViewHolder = new LinkViewHolder(view);
                linkViewHolder.mainActivty = this.mainActivity;
                return linkViewHolder;
            case R.layout.post_youtube:
                view = inflater.inflate(R.layout.post_youtube,parent,false);
                YouTubeViewHolder youTubeViewHolder = new YouTubeViewHolder(view);
                youTubeViewHolder.mainActivty = this.mainActivity;
                return youTubeViewHolder;
            case R.layout.post_gif:
                view = inflater.inflate(R.layout.post_gif,parent,false);
                GIFViewHolder gifViewHolder = new GIFViewHolder(view);
                gifViewHolder.mainActivty = this.mainActivity;
                return gifViewHolder;
            case R.layout.post_multi_picture:
                view = inflater.inflate(R.layout.post_multi_picture,parent,false);
                MultiPictureViewHolder multiPictureViewHolder = new MultiPictureViewHolder(view);
                multiPictureViewHolder.mainActivty = this.mainActivity;
                return multiPictureViewHolder;
            case R.layout.post_translation:
                view = inflater.inflate(R.layout.post_translation,parent,false);
                TranslationViewHolder translationViewHolder = new TranslationViewHolder(view);
                translationViewHolder.mainActivty = this.mainActivity;
                return translationViewHolder;
            case R.layout.post_repost:
                view = inflater.inflate(R.layout.post_repost,parent,false);
                RepostViewHolder repostViewHolder = new RepostViewHolder(view);
                repostViewHolder.mainActivty = this.mainActivity;
                return repostViewHolder;
            case R.layout.post_community:
                view = inflater.inflate(R.layout.post_community,parent,false);
                CommunityPostViewHolder communityPostViewHolder = new CommunityPostViewHolder(view);
                communityPostViewHolder.mainActivty = this.mainActivity;
                return communityPostViewHolder;
            case R.layout.post_top_header:
                if(this.header == null){
                    view = inflater.inflate(R.layout.post_top_header,parent,false);
                    this.header = new HeaderViewHolder(view);
                    return this.header;
                }else{
                    this.header.isInitialized = true;
                    return this.header;
                }
            default:
                view = inflater.inflate(R.layout.post_default,parent,false);
                return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            if(position == 0 && loadHeader){
                ((HeaderViewHolder) holder).bind();
                return;
            }

        Post post;
        if(loadHeader){
             post = postList.get(position-1);
        }
        else{
            post = postList.get(position);
        }
            if( holder instanceof PictureViewHolder){
                ((PictureViewHolder) holder).bind((PicturePost) post);
            }else{
            if(holder instanceof ThoughtViewHolder){
                ((ThoughtViewHolder) holder).bind((ThoughtPost) post);
            }
            else{
                if(holder instanceof YouTubeViewHolder){
                    ((YouTubeViewHolder) holder).bind((YouTubePost)post);
                }else{
                    if(holder instanceof LinkViewHolder){
                        ((LinkViewHolder) holder).bind((LinkPost) post);
                    }else{
                        if(holder instanceof GIFViewHolder){
                            ((GIFViewHolder) holder).bind((GIFPost)post);
                        }else {
                            if(holder instanceof TranslationViewHolder){
                                ((TranslationViewHolder)holder).bind((TranslationPost) post);
                            }else{
                                if(holder instanceof MultiPictureViewHolder){
                                    ((MultiPictureViewHolder) holder).bind((MultiPicturePost)post);
                                }else{
                                    if(holder instanceof RepostViewHolder){
                                        ((RepostViewHolder)holder).bind((RepostPost) post);
                                    }else if(holder instanceof CommunityPostViewHolder) {
                                        ((CommunityPostViewHolder)holder).bind((CommunityPost) post);
                                    }else{
                                        ((DefaultViewHolder)holder).bind((DefaultPost)post);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if(loadHeader) return postList.size()+1;
        else return postList.size();
    }
}

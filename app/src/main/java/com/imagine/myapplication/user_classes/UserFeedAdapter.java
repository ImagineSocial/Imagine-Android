package com.imagine.myapplication.user_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.imagine.myapplication.Feed.viewholder_classes.CustomViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.DefaultViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.GIFViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.Feed.viewholder_classes.LinkViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.MultiPictureViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.PictureViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.RepostViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.ThoughtViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.TranslationViewHolder;
import com.imagine.myapplication.Feed.viewholder_classes.YouTubeViewHolder;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.LinkPost;
import com.imagine.myapplication.post_classes.MultiPicturePost;
import com.imagine.myapplication.post_classes.PicturePost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.post_classes.RepostPost;
import com.imagine.myapplication.post_classes.ThoughtPost;
import com.imagine.myapplication.post_classes.TranslationPost;
import com.imagine.myapplication.post_classes.YouTubePost;

import java.util.ArrayList;

public class UserFeedAdapter extends FeedAdapter {

    public ArrayList<Post> postList;
    public Context mContext;
    public UserActivity activity;
    public User user;

    public UserFeedAdapter(ArrayList<Post> postList, Context mContext, User user,UserActivity activity) {
        super(postList, mContext);
        this.postList = postList;
        this.mContext = mContext;
        this.activity = activity;
        this.user= user;
    }

    @Override
    public void addMorePosts(ArrayList<Post> posts) {
        //Doesnt really add more like a setter
        this.postList = posts;
    }

    @Override
    public int getItemViewType(int position) {
        //Position 0 always retruns the header layout
        if(position == 0){
            return R.layout.user_header;
        }
        String type = postList.get(position-1).type;
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
            default:
                return R.layout.post_default;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //Checks what kind of subclass the holder is an instance of
        // calls the subclasses bind() method
        if(position ==0){
            ((User_Feed_Header_Viewholder) holder).bind(this.user);
            return;
        }
        Post post = postList.get(position-1);
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
        return postList.size()+1;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        switch(viewType){
            case R.layout.user_header:
                view = inflater.inflate(R.layout.user_header,parent,false);
                return new User_Feed_Header_Viewholder(view,this.activity);
            case R.layout.post_picture:
                view = inflater.inflate(R.layout.post_picture,parent,false);
                PictureViewHolder pictureViewHolder = new PictureViewHolder(view);
                pictureViewHolder.mainActivty = this.activity;
                return pictureViewHolder;
            case R.layout.post_thought:
                view = inflater.inflate(R.layout.post_thought,parent,false);
                ThoughtViewHolder thoughtViewHolder = new ThoughtViewHolder(view);
                thoughtViewHolder.mainActivty = this.activity;
                return thoughtViewHolder;
            case R.layout.post_link:
                view = inflater.inflate(R.layout.post_link,parent,false);
                LinkViewHolder linkViewHolder = new LinkViewHolder(view);
                linkViewHolder.mainActivty = this.activity;
                return linkViewHolder;
            case R.layout.post_youtube:
                view = inflater.inflate(R.layout.post_youtube,parent,false);
                YouTubeViewHolder youTubeViewHolder = new YouTubeViewHolder(view);
                youTubeViewHolder.mainActivty = this.activity;
                return youTubeViewHolder;
            case R.layout.post_gif:
                view = inflater.inflate(R.layout.post_gif,parent,false);
                GIFViewHolder gifViewHolder = new GIFViewHolder(view);
                gifViewHolder.mainActivty = this.activity;
                return gifViewHolder;
            case R.layout.post_multi_picture:
                view = inflater.inflate(R.layout.post_multi_picture,parent,false);
                MultiPictureViewHolder multiPictureViewHolder = new MultiPictureViewHolder(view);
                multiPictureViewHolder.mainActivty = this.activity;
                return multiPictureViewHolder;
            case R.layout.post_translation:
                view = inflater.inflate(R.layout.post_translation,parent,false);
                TranslationViewHolder translationViewHolder = new TranslationViewHolder(view);
                translationViewHolder.mainActivty = this.activity;
                return translationViewHolder;
            case R.layout.post_repost:
                view = inflater.inflate(R.layout.post_repost,parent,false);
                RepostViewHolder repostViewHolder = new RepostViewHolder(view);
                repostViewHolder.mainActivty = this.activity;
            default:
                view = inflater.inflate(R.layout.post_default,parent,false);
                return new DefaultViewHolder(view);
        }
    }
}

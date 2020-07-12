package com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;
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

public class FeedAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    private static final String TAG = "FeedAdapter";
    public ArrayList<Post> postList = new ArrayList<>();
    public Context mContext;

    public FeedAdapter(ArrayList<Post> postList, Context mContext) {
        this.postList = postList;
        this.mContext = mContext;
    }
    public void addMorePosts(ArrayList<Post> posts){
        postList = posts;
        System.out.println("Posts geadded!");
    }
    @Override
    public int getItemViewType(int position) {
        String type = postList.get(position).type;

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

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        switch(viewType){
            case R.layout.post_picture:
                view = inflater.inflate(R.layout.post_picture,parent,false);
                return new PictureViewHolder(view);
            case R.layout.post_thought:
                view = inflater.inflate(R.layout.post_thought,parent,false);
                return new ThoughtViewHolder(view);
            case R.layout.post_link:
                view = inflater.inflate(R.layout.post_link,parent,false);
                return new LinkViewHolder(view);
            case R.layout.post_youtube:
                view = inflater.inflate(R.layout.post_youtube,parent,false);
                return new YouTubeViewHolder(view);
            case R.layout.post_gif:
                view = inflater.inflate(R.layout.post_gif,parent,false);
                return new GIFViewHolder(view);
            case R.layout.post_multi_picture:
                view = inflater.inflate(R.layout.post_multi_picture,parent,false);
                return new MultiPictureViewHolder(view);
            case R.layout.post_translation:
                view = inflater.inflate(R.layout.post_translation,parent,false);
                return new TranslationViewHolder(view);
            case R.layout.post_repost:
                view = inflater.inflate(R.layout.post_repost,parent,false);
                return new RepostViewHolder(view);
            default:
                view = inflater.inflate(R.layout.post_default,parent,false);
                return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Post post = postList.get(position);
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
                                    }else {
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
        return postList.size();
    }
}

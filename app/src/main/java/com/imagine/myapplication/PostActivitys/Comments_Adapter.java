package com.imagine.myapplication.PostActivitys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Comment;
import com.imagine.myapplication.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Comments_Adapter extends RecyclerView.Adapter<CommentViewHolder> {
    private static final String TAG = "Comments_Adapter";
    public ArrayList<Comment> comments;
    public Context mContext;

    public Comments_Adapter(ArrayList<Comment> comments, Context mContext) {
        this.comments = comments;
        this.mContext = mContext;
    }

    public void getNewComments(ArrayList<Comment> comments){
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.comment_layout,parent,false);
        CommentViewHolder commVH = new CommentViewHolder(view);
        return commVH;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

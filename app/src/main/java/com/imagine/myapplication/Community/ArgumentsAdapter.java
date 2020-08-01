package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

import java.util.ArrayList;

public class ArgumentsAdapter extends RecyclerView.Adapter<ArgumentViewHolder> {
    ArrayList<Argument> args;
    Context mContext;

    public ArgumentsAdapter(ArrayList<Argument> args, Context context){
        this.args = args;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ArgumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater infalter = LayoutInflater.from(mContext);
        View view = infalter.inflate(R.layout.argument,parent,false);
        ArgumentViewHolder argumentViewHolder = new ArgumentViewHolder(view,mContext);
        return argumentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArgumentViewHolder holder, int position) {
        holder.bind(args.get(position));
    }

    @Override
    public int getItemCount() {
        return args.size();
    }
}

package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

import java.util.ArrayList;

public class ArgumentsAdapter extends RecyclerView.Adapter<ArgumentViewHolder> {
    ArrayList<Argument> args;
    Context mContext;
    CommunityFactsFragment frag;

    public ArgumentsAdapter(ArrayList<Argument> args, Context context){
        this.args = args;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ArgumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater infalter = LayoutInflater.from(mContext);
        View view;
        switch(viewType){
            case R.layout.argument:
                view = infalter.inflate(R.layout.argument,parent,false);
                ArgumentViewHolder argumentViewHolder = new ArgumentViewHolder(view,mContext);
                return argumentViewHolder;
            case R.layout.argument_footer:
                view = infalter.inflate(R.layout.argument_footer,parent,false);
                ArgumentViewHolderFooter footer = new ArgumentViewHolderFooter(view,mContext);
                footer.frag = this.frag;
                return footer;
            default:
                view = infalter.inflate(R.layout.argument,parent,false);
                return new ArgumentViewHolder(view,mContext);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ArgumentViewHolder holder, int position) {
        holder.bind(args.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        String type = args.get(position).type;
        switch(type){
            case "arg":
                return R.layout.argument;
            case "footer":
                return R.layout.argument_footer;
            default:
                return R.layout.argument;
        }
    }

    @Override
    public int getItemCount() {
        return args.size();
    }
}

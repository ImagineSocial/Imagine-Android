package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

public class ArgumentViewHolder extends RecyclerView.ViewHolder {
    public Context mContext;

    public ArgumentViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.mContext = context;
    }

    public void bind(final Argument arg){
        TextView title = itemView.findViewById(R.id.argument_title);
        TextView description = itemView.findViewById(R.id.argument_description);
        title.setText(arg.title);
        description.setText(arg.description);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(mContext,ArgumentActivity.class);
                intent.putExtra("title",arg.title);
                intent.putExtra("description", arg.description);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}

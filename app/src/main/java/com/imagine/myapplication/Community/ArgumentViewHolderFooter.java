package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.imagine.myapplication.R;

public class ArgumentViewHolderFooter extends ArgumentViewHolder {

    CommunityFactsFragment frag;

    public ArgumentViewHolderFooter(@NonNull View itemView, Context context) {
        super(itemView, context);
    }

    @Override
    public void bind(Argument arg) {
        Button adder = itemView.findViewById(R.id.add_argument_button);
        adder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag.showDialogFragment();
            }
        });
    }
}

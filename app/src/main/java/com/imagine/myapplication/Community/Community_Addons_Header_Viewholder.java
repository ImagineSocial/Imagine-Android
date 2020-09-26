package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.imagine.myapplication.R;

public class Community_Addons_Header_Viewholder extends Community_Addons_ViewHolder {
    public Community_Addons_Header_Viewholder(@NonNull View itemView, Context context) {
        super(itemView, context);
    }

    @Override
    public void bind(final Addon addon) {
        Button button = itemView.findViewById(R.id.add_addon_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(),Community_Addon_New_Addon_Activity.class);
                Gson gson = new Gson();
                String commString = gson.toJson(addon.community);
                intent.putExtra("comm",commString);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}

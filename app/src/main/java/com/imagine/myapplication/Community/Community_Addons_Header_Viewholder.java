package com.imagine.myapplication.Community;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class Community_Addons_Header_Viewholder extends Community_Addons_ViewHolder {
    public Community_Addons_Header_Viewholder(@NonNull View itemView, Context context) {
        super(itemView, context);
    }

    @Override
    public void bind(Addon addon) {
        System.out.println("!");
    }
}

package com.imagine.myapplication.Community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.imagine.myapplication.R;

public class Community_New_Community_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_new_activity);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        System.out.println("!");
    }
}

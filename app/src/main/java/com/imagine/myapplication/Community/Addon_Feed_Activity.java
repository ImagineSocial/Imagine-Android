package com.imagine.myapplication.Community;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imagine.myapplication.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Addon_Feed_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addon_feed_activity);
        Gson gson = new Gson();
        Intent intent = getIntent();
        String itemsString = intent.getStringExtra("items");
        ArrayList<Object> test = new ArrayList<Object>();
        test= gson.fromJson(itemsString,test.getClass());
    }
}

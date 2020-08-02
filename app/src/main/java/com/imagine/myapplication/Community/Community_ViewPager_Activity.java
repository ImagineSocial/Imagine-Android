package com.imagine.myapplication.Community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.imagine.myapplication.R;

import java.util.HashMap;

public class Community_ViewPager_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_test);
        Intent intent = getIntent();
        HashMap<String,String> args = new HashMap<>();
        args.put("name",intent.getStringExtra("name"));
        args.put("description",intent.getStringExtra("description"));
        args.put("imageURL",intent.getStringExtra("imageURL"));
        args.put("commID",intent.getStringExtra("commID"));
        args.put("displayOption",intent.getStringExtra("displayOption"));
        ViewPager2 viewPager2 = findViewById(R.id.containerViewPager);
        TestCollectionAdapter adapter = new TestCollectionAdapter(this,args);
        viewPager2.setAdapter(adapter);
        if(intent.getStringExtra("displayOption").equals("fact")){
            viewPager2.setCurrentItem(1);
        }else{
            viewPager2.setCurrentItem(0);
        }
    }
}

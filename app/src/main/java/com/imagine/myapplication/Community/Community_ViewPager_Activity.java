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
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

import java.util.HashMap;

public class Community_ViewPager_Activity extends AppCompatActivity {

    public String name;
    public String description;
    public String imageURL;
    public String commID;
    public String displayOption;
    public Communities_Fragment fragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_test);
        Intent intent = getIntent();
        this.name = intent.getStringExtra("name");
        this.description = intent.getStringExtra("description");
        this.imageURL = intent.getStringExtra("imageURL");
        this.commID = intent.getStringExtra("commID");
        this.displayOption = intent.getStringExtra("displayOption");

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
            viewPager2.setCurrentItem(0);
        }else{
            viewPager2.setCurrentItem(0);
        }
    }
}

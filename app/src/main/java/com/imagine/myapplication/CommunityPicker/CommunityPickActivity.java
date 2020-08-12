package com.imagine.myapplication.CommunityPicker;

import android.os.Bundle;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.CommunityCallback;
import com.imagine.myapplication.R;

import java.util.ArrayList;

public class CommunityPickActivity extends AppCompatActivity {

    public Communities_Helper helper = new Communities_Helper();
    ArrayList<Community> commList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // fetches the communities from the database
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_picker_activity);

        ViewPager2 viewPager2 = findViewById(R.id.community_picker_viewpager);
        TabLayout tabLayout = findViewById(R.id.community_picker_tab_layout);
        PickerCollectionAdapter adapter = new PickerCollectionAdapter(this,this);
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch(position){
                    case 0:
                        tab.setText("Topics");
                        return;
                    case 1:
                        tab.setText("Facts");
                        return;
                    case 2:
                        tab.setText("Deine Communitys");
                }
            }
        }).attach();
    }
}

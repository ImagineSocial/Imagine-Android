package com.imagine.myapplication.Community;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.imagine.myapplication.R;

public class ArgumentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_argument);
        TextView title_tv = findViewById(R.id.comm_activity_title);
        TextView description_tv = findViewById(R.id.comm_activity_description);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        title_tv.setText(title);
        description_tv.setText(description);
    }
}

package com.imagine.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;
import com.imagine.myapplication.nav_fragments.Feed_Fragment;
import com.imagine.myapplication.nav_fragments.New_Post_Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.imagine.myapplication.R.drawable.default_user;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Feed_Fragment()).commit();

        //In R.values.styles.xml habe ich eingestellt: parent="Theme.AppCompat.Light.NoActionBar">
        //Toolbar aus der XML Datei
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        //Get the image from toolbar XML
        View hView =  toolbar.getRootView();
        CircleImageView imageCircle = hView.findViewById(R.id.toolbarProfilePicture);
        Glide.with(this).load(default_user).into(imageCircle);
        imageCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("## Fucking Bullshit");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId()){
                        case R.id.nav_feed:
                            selectedFragment = new Feed_Fragment();
                            break;
                        case R.id.nav_new_post:
                            selectedFragment = new New_Post_Fragment();
                            break;
                        case R.id.nav_communities:
                            selectedFragment = new Communities_Fragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}

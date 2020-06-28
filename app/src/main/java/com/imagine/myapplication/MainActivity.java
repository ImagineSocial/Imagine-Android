package com.imagine.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;
import com.imagine.myapplication.nav_fragments.Feed_Fragment;
import com.imagine.myapplication.nav_fragments.New_Post_Fragment;
import com.imagine.myapplication.user_classes.UserActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.imagine.myapplication.R.drawable.default_user;

public class MainActivity extends AppCompatActivity{
    public Context mContext;
    public Button loginButton;
    public CircleImageView imageCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = this;
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
        this.imageCircle = hView.findViewById(R.id.toolbarProfilePicture);
        this.loginButton = hView.findViewById(R.id.toolbarLoginButton);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            loginButton.setVisibility(View.INVISIBLE);
            Uri userURI = user.getPhotoUrl();

            if (userURI != null) {
                Glide.with(this).load(userURI).into(imageCircle);
            } else {
                Glide.with(this).load(default_user).into(imageCircle);
            }
            imageCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else {
            imageCircle.setVisibility(View.INVISIBLE);
            loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            loginButton.setVisibility(View.INVISIBLE);
            Uri userURI = user.getPhotoUrl();

            if (userURI != null) {
                Glide.with(this).load(userURI).into(imageCircle);
            } else {
                Glide.with(this).load(default_user).into(imageCircle);
            }
            imageCircle.setVisibility(View.VISIBLE);
            imageCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,UserActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else{
            imageCircle.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
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

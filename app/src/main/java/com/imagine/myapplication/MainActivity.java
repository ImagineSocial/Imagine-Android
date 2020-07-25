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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;
import com.imagine.myapplication.nav_fragments.Community_Posts_Fragment;
import com.imagine.myapplication.nav_fragments.Feed_Fragment;
import com.imagine.myapplication.nav_fragments.Information_Fragment;
import com.imagine.myapplication.nav_fragments.New_Post_Fragment;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.imagine.myapplication.R.drawable.default_user;

public class MainActivity extends AppCompatActivity{
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Feed_Fragment feed_fragment;
    public Community_Posts_Fragment commPosts_fragment;
    public New_Post_Fragment newPosts_fragment;
    public Communities_Fragment comms_fragment;
    public Information_Fragment infos_fragment;
    public Context mContext;
    public Button loginButton;
    public CircleImageView imageCircle;
    public User userObj;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = this;
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        feed_fragment = new Feed_Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                feed_fragment).commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        //Reference to UserImage and LoginButton in Toolbar
        this.imageCircle = findViewById(R.id.toolbarProfilePicture);
        this.loginButton = findViewById(R.id.toolbarLoginButton);
    }

    @Override
    protected void onResume() {
        // Reloads the UserViews when resumed to main activity
        super.onResume();
        final FirebaseUser user = auth.getCurrentUser();
        if(user != null){
                this.getUser(user.getUid());
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
                            if(feed_fragment == null){
                                feed_fragment = new Feed_Fragment();
                            }
                            selectedFragment = feed_fragment;
                            break;
                        case R.id.nav_new_post:
                            if(newPosts_fragment == null){
                                newPosts_fragment = new New_Post_Fragment();
                            }
                            selectedFragment = newPosts_fragment;
                            break;
                        case R.id.nav_communities:
                            if(comms_fragment == null){
                                comms_fragment = new Communities_Fragment();
                            }
                            selectedFragment = comms_fragment;
                            break;
                        case R.id.nav_info:
                            if(infos_fragment == null){
                                infos_fragment = new Information_Fragment();
                            }
                            selectedFragment = infos_fragment;
                            break;
                        case R.id.nav_community_posts:
                            if(commPosts_fragment == null){
                                commPosts_fragment = new Community_Posts_Fragment();
                            }
                            selectedFragment = commPosts_fragment;
                            break;
                        default:
                            System.out.println("Default case! "+TAG);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    public void getUser(final String userID){
        //Fetches user information and calls setUpUserViews()
        if(userID != "" && userID !=null){
            DocumentReference userRef = db.collection("Users").document(userID);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try {
                        Map<String, Object> docData = documentSnapshot.getData();
                        String userName = (docData.get("name") != null)      // Condition
                                ? (String) docData.get("name")              // IF-True
                                : (String) "";                              // ELSE

                        String userSurname = (docData.get("surname") != null)
                                ? (String) docData.get("surname")
                                : (String) "";
                        String userImageURL = (docData.get("profilePictureURL") != null)
                                ? (String) docData.get("profilePictureURL")
                                : (String) "";
                        String userUserUID = userID;
                        String userStatusQuote = (docData.get("statusText") != null)
                                ? (String) docData.get("statusText")
                                : (String) "";
                        List<String> userBlocked = (docData.get("blocked") != null)
                                ? (List<String>) docData.get("blocked")
                                : (List<String>) null;
                        User user = new User(userName, userSurname, userUserUID);
                        user.setImageURL(userImageURL);
                        user.setStatusQuote(userStatusQuote);
                        user.setBlocked(userBlocked);
                        setUpUserViews(user);
                    }catch(NullPointerException e){
                        System.out.println("Error in UserFetch! "+TAG+" "+ e.getStackTrace());
                    }
                }
            });
        }
    }

    public void setUpUserViews(User user){
        // Sets up the User Views (UserImage & LoginButton)
        this.userObj = user;
        this.imageCircle.setVisibility(View.VISIBLE);
        this.loginButton.setVisibility(View.INVISIBLE);
        if (user.imageURL.equals("")) {
            Glide.with(this).load(default_user).into(imageCircle);
        } else {
            Glide.with(this).load(user.imageURL).into(imageCircle);
        }
        imageCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String userString = gson.toJson(userObj);
                Intent intent = new Intent(mContext,UserActivity.class);
                intent.putExtra("user", userString);
                mContext.startActivity(intent);
            }
        });
    }
}

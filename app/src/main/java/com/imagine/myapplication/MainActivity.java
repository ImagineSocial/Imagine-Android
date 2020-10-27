package com.imagine.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.ImagineCommunity.InfoDialogFragment;
import com.imagine.myapplication.ImagineCommunity.InfoDialogType;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;
import com.imagine.myapplication.nav_fragments.Community_Posts_Fragment;
import com.imagine.myapplication.nav_fragments.Feed_Fragment;
import com.imagine.myapplication.nav_fragments.Imagine_Community_Fragment;
import com.imagine.myapplication.nav_fragments.New_Post_Fragment;
import com.imagine.myapplication.notifications.Notification;
import com.imagine.myapplication.notifications.NotificationCallback;
import com.imagine.myapplication.notifications.NotificationsAdapter;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.imagine.myapplication.R.drawable.default_user;

public class MainActivity extends AppCompatActivity{

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public DrawerLayout drawer;
    public RecyclerView noti_recyclerView;
    public NotificationsAdapter adapter;
    public Post_Helper helper;
    public static Feed_Fragment feed_fragment;
    public static Community_Posts_Fragment commPosts_fragment;
    public static New_Post_Fragment newPosts_fragment;
    public static Communities_Fragment comms_fragment;
    public static Imagine_Community_Fragment infos_fragment;
    public Activity mainActivity;
    public Context mContext;
    public Button loginButton;
    public CircleImageView imageCircle;
    public User userObj;
    public View header;
    public Gson gson = new Gson();
    public static boolean languageChange = false;
    public TextView smallNotificationLabel;
    public Button deleteButton;
    public static Context configContext;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new Post_Helper(this);
        loadLocale();
        showIntro();
        this.mContext = this;
        mainActivity = this;
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        feed_fragment = new Feed_Fragment();
        feed_fragment.mainActivity = this.mainActivity;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                feed_fragment).commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        this.drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this.navViewListener);
        this.header = navigationView.getHeaderView(0);
        //Reference to UserImage and LoginButton in Toolbar
        this.imageCircle = findViewById(R.id.toolbarProfilePicture);
        this.loginButton = findViewById(R.id.toolbarLoginButton);
        helper.getNotifictations(new NotificationCallback() {
            @Override
            public void onCallback(ArrayList<Notification> notifications) {
                if(notifications != null){
                    if(noti_recyclerView != null){
                        adapter.nots = notifications;
                        adapter.notifyDataSetChanged();
                        setUpNotificationViews(notifications);
                    }else{
                        setUpNotifications(notifications);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        // Reloads the UserViews when resumed to main activity
        if(MainActivity.languageChange){
            this.resetAfterLanguageChange();
        }

        super.onResume();
        if(drawer != null){
            drawer.closeDrawer(GravityCompat.START);
        }
        if(feed_fragment == null){
            feed_fragment = new Feed_Fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    feed_fragment).commit();
            helper.getNotifictations(new NotificationCallback() {
                @Override
                public void onCallback(ArrayList<Notification> notifications) {
                    if(notifications != null){
                        setUpNotifications(notifications);
                    }
                }
            });
        }
        final FirebaseUser user = auth.getCurrentUser();
        if(user != null){
                this.getUser(user.getUid());

        } else{
            if(header != null){
                ImageView headerImage = header.findViewById(R.id.sideMenu_profilePicture);
                TextView name = header.findViewById(R.id.sideMenu_userName);
                name.setText(getResources().getString(R.string.main_activity_name));
                Glide.with(mContext).load(default_user).into(headerImage);
            }
            if(noti_recyclerView != null && adapter != null){
                adapter.resetNotifications();
                adapter.notifyDataSetChanged();
                helper.getNotifictations(new NotificationCallback() {
                    @Override
                    public void onCallback(ArrayList<Notification> notifications) {
                        if(notifications != null){
                            if(noti_recyclerView != null){
                                adapter.nots = notifications;
                                adapter.notifyDataSetChanged();
                            }else{
                                setUpNotifications(notifications);
                            }
                        }
                    }
                });
            }
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private NavigationView.OnNavigationItemSelectedListener navViewListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return false;
                }
            };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemID = item.getItemId();
                    System.out.println("!");
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
                        case R.id.imagine_community:
                            if(infos_fragment == null){
                                infos_fragment = new Imagine_Community_Fragment();
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

        public void showFeed() {
            Fragment selectedFragment = null;

            if (feed_fragment == null) {
                feed_fragment = new Feed_Fragment();
            }
            selectedFragment = feed_fragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            feed_fragment.refreshFeed(null);
        }

        public void setUpNotificationViews(ArrayList<Notification> nots){
            TextView smallNotificationLabel = findViewById(R.id.toolbar_smallNotificationLabel);
            Button deleteButton = header.findViewById(R.id.sideMenu_deleteNotificationsButton);
            if (nots.size() != 0) {
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(12);   //View is 12x12

                shape.setColor(Color.RED);
                smallNotificationLabel.setBackground(shape);
                smallNotificationLabel.setClipToOutline(true);
                smallNotificationLabel.setText(nots.size() + "");
                smallNotificationLabel.setVisibility(View.VISIBLE);


                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAllNotifications();
                    }
                });
            } else {
                smallNotificationLabel.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
            }
        }

        public void setUpNotifications(ArrayList<Notification> nots){
        if (header != null) {
            this.noti_recyclerView = header.findViewById(R.id.notifications_recyclerView);
            this.adapter = new NotificationsAdapter(nots, mContext);
            noti_recyclerView.setAdapter(adapter);
            noti_recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            TextView smallNotificationLabel = findViewById(R.id.toolbar_smallNotificationLabel);
            Button deleteButton = header.findViewById(R.id.sideMenu_deleteNotificationsButton);

            if (nots.size() != 0) {
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(12);   //View is 12x12

                shape.setColor(Color.RED);
                smallNotificationLabel.setBackground(shape);
                smallNotificationLabel.setClipToOutline(true);
                smallNotificationLabel.setText(nots.size() + "");
                smallNotificationLabel.setVisibility(View.VISIBLE);


                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAllNotifications();
                    }
                });

            } else {
                smallNotificationLabel.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
            }

        }
    }

    public void deleteAllNotifications() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            final CollectionReference notRef = db.collection("Users").document(user.getUid())
                    .collection("notifications");

            notRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DocumentReference ref = notRef.document(document.getId());
                        ref.delete().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Failed to remove Notification");
                            }
                        });
                    }
                }
            });
        }
    }

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

                        if (header != null) {
                            CircleImageView profilePicture = header.findViewById(R.id.sideMenu_profilePicture);
                            TextView nameLabel = header.findViewById(R.id.sideMenu_userName);

                            if (user.imageURL.equals("")) {
                                Glide.with(mContext).load(default_user).into(profilePicture);
                            } else {
                                Glide.with(mContext).load(user.imageURL).into(profilePicture);
                            }
                            nameLabel.setText(user.name);

                            profilePicture.setOnClickListener(new View.OnClickListener() {
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

                drawer.openDrawer(GravityCompat.START);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5){
            String name = data.getStringExtra("name");
            String imageURL = data.getStringExtra("imageURL");
            String commID = data.getStringExtra("commID");
            String postID = data.getStringExtra("postID");

            DocumentReference postRef = db.collection("Posts").document(postID);
            HashMap<String,Object> updateData = new HashMap<>();
            updateData.put("linkedFactID",commID);
            postRef.update(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            System.out.println("!");
                        }
                }
            });


            CollectionReference commRef = db.collection("Facts")
                    .document(commID).collection("posts");
            DocumentReference commPostRef = commRef.document(postID);
            HashMap<String,Object> map = new HashMap<>();
            map.put("createTime",new Timestamp(new Date()));
            commPostRef.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        System.out.println("Erfolg!");
                    } else if(task.isCanceled()){
                        System.out.println("Fail!");
                    }
                }
            });
        }
    }


    public void loadLocale(){
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    public void changeLang(String lang) {
        Locale myLocale;
        if (lang.equalsIgnoreCase("")) {
            Locale.getDefault().getDisplayLanguage();
            Locale locale = new Locale(Locale.getDefault().getDisplayLanguage());
            Configuration configuration = new Configuration(getResources().getConfiguration());
            configuration.setLocale(locale);
            MainActivity.configContext = createConfigurationContext(configuration);
            return;
        }
        myLocale = new Locale(lang);
        saveLocale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(myLocale);
        Locale.setDefault(myLocale);
        res.updateConfiguration(config, dm);
        MainActivity.configContext = createConfigurationContext(config);
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public void showIntro() {
        String langPref = "info_intro";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        boolean alreadyLaunched = prefs.getBoolean(langPref, false);

        if (!alreadyLaunched) {
            InfoDialogFragment frag = new InfoDialogFragment(this);
            frag.type = InfoDialogType.intro;
            frag.show();
        }
    }


    public static void resetFragmentsAfterLogin(){
        feed_fragment = null;
        commPosts_fragment = null;
        newPosts_fragment = null;
        comms_fragment = null;
        infos_fragment = null;
    }

    public void resetAfterLanguageChange(){
        MainActivity.languageChange = false;
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

}

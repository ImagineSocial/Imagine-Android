package com.imagine.myapplication.user_classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";
    public StorageReference storeRef = FirebaseStorage.getInstance().getReference();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public ArrayList<Post> posts = new ArrayList<>();
    public Post_Helper helper = new Post_Helper();
    public User_Feed_Header_Viewholder header;
    public RecyclerView recyclerView;
    public UserFeedAdapter adapter;
    public SwipeRefreshLayout swipe;
    public Context mContext;
    public User user;
    public final int GALLERY = 1;
    public Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        this.mContext = this;
        Gson gson = new Gson();
        Intent intent = getIntent();
        String userString = intent.getStringExtra("user");
        user = gson.fromJson(userString,User.class);
        swipe = findViewById(R.id.swipeUserFeed);
        swipe.setRefreshing(true);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                helper.getPostsForUserFeed(new FirebaseCallback() {
                    @Override
                    public void onCallback(ArrayList<Post> values) {
                        ArrayList<Post> sortedPosts = sortPostList(values);
                        posts = sortedPosts;
                        adapter.refreshPosts(posts);
                        swipe.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }
                },user.userID);
            }
        });
        helper.getPostsForUserFeed(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                ArrayList<Post> sortedPosts = sortPostList(values);
                posts = sortedPosts;
                swipe.setRefreshing(false);
                initRecyclerView();
            }
        },user.userID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        Button logout_button = findViewById(R.id.toolbar_logout_button);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.getUid().equals(user.userID)) {
                logout_button.setVisibility(View.VISIBLE);

                logout_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();

                        UserActivity.super.finish();
                    }
                });
            }
        }
    }

    public void initRecyclerView(){
        //initializes RecyclerView and adds
        //onScrollListener
        recyclerView = findViewById(R.id.user_recyclerView);
        adapter = new UserFeedAdapter(posts,mContext,user,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            boolean loading = true;
            int previousTotal =0;
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = lm.getChildCount();
                int totalItemCount = lm.getItemCount();
                int pastVisibleItems = lm.findFirstVisibleItemPosition();

                if(loading && totalItemCount > previousTotal){
                    loading = false;
                    previousTotal = totalItemCount;
                }
                if((totalItemCount-(pastVisibleItems+visibleItemCount))<=2&&!loading){
                    loading = true;
                    helper.getMorePostsForUserFeed(new FirebaseCallback() {
                        @Override
                        public void onCallback(ArrayList<Post> values) {
                            ArrayList<Post> sortedValues = sortPostList(values);
                            posts = sortedValues;
                            UserFeedAdapter adapter = (UserFeedAdapter) recyclerView.getAdapter();
                            adapter.addMorePosts(sortedValues);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public ArrayList<Post> sortPostList(ArrayList<Post> posts){
        //orders the posts by createTime attribute
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                if(o1.createTimestamp.getSeconds()>=
                        o2.createTimestamp.getSeconds()){
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        for(Post post : posts){
            System.out.println(post.createTimestamp.getNanoseconds());
        }
        return posts;

    }

    public void setHeader(User_Feed_Header_Viewholder header){
        //Links the header vierholder to the userActivity
        //neccessary for the on ActivityResult method
        this.header = header;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser user = auth.getCurrentUser();
        if(resultCode == Activity.RESULT_OK){
            if(user != null){
                if(user.getPhotoUrl() != null){
                    deletePhoto(user);
                }
            }

            if(requestCode == GALLERY){
                if(data != null){
                    final Uri contentURI = data.getData();
                    try{
                        if(Build.VERSION.SDK_INT <28){
                            bitmap = MediaStore.Images.Media.getBitmap(mContext
                                    .getContentResolver(),contentURI);
                            setPhoto();
                        } else{
                            ImageDecoder.Source source = ImageDecoder.createSource(mContext
                                    .getContentResolver(),contentURI);
                            bitmap = ImageDecoder.decodeBitmap(source);
                            setPhoto();
                        }
                    }catch (Exception e){
                        System.out.println(e.getStackTrace().toString());
                    }
                }
            }
        }

    }

    public void deletePhoto(FirebaseUser user){
        //deletes the existing profile picture from the database
        String imageName = user.getUid()+".profilePicture";
        StorageReference imageRef = storeRef.child("profilePictures").child(imageName+".png");
        imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("File Deleted! " +TAG);
                } else if(task.isCanceled()){
                    System.out.println("Error deleting File! " +TAG);
                }
            }
        });
    }

    public void setPhoto(){
        // uploads the new selected profilePicture to the Database
        final FirebaseUser user = auth.getCurrentUser();
        if( user != null){
            String imageName = user.getUid()+".profilePicture.png";
            final StorageReference imageRef = storeRef.child("profilePictures").child(imageName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            StorageTask uploadTask = imageRef.putBytes(data).addOnCompleteListener(
                    new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                System.out.println("Picture upload successfull! "+TAG);
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = uri.toString();
                                        changeDatabase(url);
                                        changeUsersAuthData(user,uri);
                                    }
                                });
                            } else if( task.isCanceled()){
                                System.out.println("Picture upload failed! "+TAG);
                            }
                        }
                    }
            );
        }
    }

    public void changeDatabase(String url){
        // changes the users profilePicture field
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            DocumentReference userRef = db.collection("Users")
                    .document(user.getUid());
            userRef.update("profilePictureURL",url)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                System.out.println("Userdocument updated! "+TAG);
                            } else if(task.isCanceled()){
                                System.out.println("Userdocument update failed! "+TAG);
                            }
                        }
                    });
        }
    }

    public void changeUsersAuthData(FirebaseUser user, Uri uri){
        // changes the users auth data
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        UserProfileChangeRequest profileUpdates = builder.setPhotoUri(uri).build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("User Auth update finished! "+TAG);
                    header.reloadPicture();
                } else if(task.isCanceled()){
                    System.out.println("User Auth update failed! "+TAG);
                }
            }
        });
    }
}

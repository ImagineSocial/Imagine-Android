package com.imagine.myapplication.Community;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;
import java.util.List;

public class Addon{

    public String OP; //
    public String description;//
    public long thanksCount; //
    public String title; //
    public String headerDescription; //
    public String headerIntro; //
    public String moreInformationLink; //
    public long popularity; //
    public boolean isHeader;
    public ArrayList<String> itemOrder;
    public String imageURL;
    public String linkedFactID;
    public String headerTitle;
    public String addonID;
    public Community community;
    public ArrayList<PostRef> refs;
    public ArrayList<Post> items;
    public Post_Helper helper = new Post_Helper();
    public CommunityAddonsFragment fragment;
}

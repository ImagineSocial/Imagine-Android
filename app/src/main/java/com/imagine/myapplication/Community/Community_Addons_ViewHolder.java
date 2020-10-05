package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.ItemCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;
import java.util.List;

public class Community_Addons_ViewHolder extends RecyclerView.ViewHolder {

    public RecyclerView recyclerView;
    public Addon addon;
    public Context mContext;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ArrayList<PostRef> refs;
    public ArrayList<Post> communityPosts = new ArrayList<>();
    public Post_Helper helper = new Post_Helper();
    public Community comm;

    public Community_Addons_ViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.mContext = context;
    }

    public void bind(final Addon boundAddon){
        if (this.addon != null) {
            if (this.addon.items != null) {
                this.addon.items = null;
                this.addon = null;
            }
        }
        this.addon = boundAddon;
        TextView title = itemView.findViewById(R.id.community_addon_title);
        TextView description = itemView.findViewById(R.id.community_addon_description);
        ImageView headerImageView = itemView.findViewById(R.id.addOn_title_imageView);
        this.recyclerView = itemView.findViewById(R.id.community_addon_recyclerView);
        title.setText(boundAddon.title);
        description.setText(boundAddon.description);
        if (boundAddon.imageURL != null && !boundAddon.imageURL.equals("")) {
            Glide.with(itemView).load(boundAddon.imageURL).into(headerImageView);
        }
        this.fetchItemList();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Addon_Feed_Activity.class);
                Addon_Feed_Activity.addon = addon;
                mContext.startActivity(intent);
            }
        });
    }

    public void fetchItemList(){
        this.refs = new ArrayList<>();
        CollectionReference ref = db.collection("Facts").document(addon.community.topicID)
                .collection("addOns").document(addon.addonID).collection("items");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docList = result.getDocuments();
                    for(DocumentSnapshot docSnap : docList){
                        PostRef postRef = new PostRef();
                        postRef.postID = docSnap.getId();
                        postRef.OP = (docSnap.getString("OP") != null)
                                ? docSnap.getString("OP")
                                : "";
                        postRef.type = (docSnap.getString("type") != null)
                                ? docSnap.getString("type")
                                : "";
                        refs.add(postRef);
                    }
                    fetchThePosts();
                } else if(task.isCanceled()){
                    System.out.println("ItemList fetch failed!");
                }
            }
        });
    }

    public void fetchThePosts(){
        this.helper.fetchAddonItems(this.refs, new ItemCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                addon.items = values;
                initRecyclerView();
            }
        });
    }

    public void initRecyclerView() {
        Community_Items_Adapter adapter = new Community_Items_Adapter(addon.items, itemView.getContext());
        this.recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    }
}

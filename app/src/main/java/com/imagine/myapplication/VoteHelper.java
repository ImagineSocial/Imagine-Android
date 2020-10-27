package com.imagine.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.post_classes.Post;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class VoteHelper {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Context mContext;

    public VoteHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void updateVotes(String type, Post post){
        DocumentReference ref;
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        Locale locale = conf.locale;
        if (post.isTopicPost) {
            switch(locale.getLanguage()){
                case "de":
                    ref = db.collection("TopicPosts").document(post.documentID);
                    break;
                case "en":
                    ref = db.collection("Data").document("en").collection("topicPosts")
                        .document(post.documentID);
                    break;
                default:
                    ref = db.collection("Data").document("en").collection("topicPosts")
                            .document(post.documentID);
                    break;
            }
        } else {
            switch(locale.getLanguage()){
                case "de":
                    ref = db.collection("Posts").document(post.documentID);
                    break;
                case "en":
                    ref = db.collection("Data").document("en").collection("posts")
                            .document(post.documentID);
                    break;
                default:
                    ref = db.collection("Data").document("en").collection("posts")
                            .document(post.documentID);
                    break;
            }
        }

        notifyUser(type, post);

        switch(type){
            case "thanks":
                ref.update("thanksCount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("ThanksVote successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("ThanksVote Update failed! Reason: "+e.getMessage());
                    }
                });
                post.thanksCount++;
                break;
            case "wow":
                ref.update("wowCount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("WowVote successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("WowVote Update failed! Reason: "+e.getMessage());
                    }
                });
                post.wowCount++;
                break;
            case "ha":
                ref.update("haCount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("HaVote successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("HaVote Update failed! Reason: "+e.getMessage());
                    }
                });
                post.haCount++;
                break;
            case "nice":
                ref.update("niceCount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("NiceVote successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("NiceVote Update failed! Reason: "+e.getMessage());
                    }
                });
                post.niceCount++;
                break;
            default:
                System.out.println("Invalid UpVoteType!");

        }
    }

    public void notifyUser(String button, Post post) {
        if (!post.originalPoster.equals("") && !post.originalPoster.equals("anonym")) {
            Configuration conf = MainActivity.configContext.getResources().getConfiguration();
            Locale locale = conf.locale;
            DocumentReference ref = db.collection("Users").document(post.originalPoster)
                    .collection("notifications").document();
            HashMap<String,Object> data = new HashMap<>();
            data.put("type","upvote");
            data.put("button", button);
            data.put("postID", post.documentID);
            data.put("title", post.title);
            data.put("language",locale.getLanguage());
            data.put("isTopicPost",post.isTopicPost);
            ref.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        System.out.println("successfully added notification");
                    }else{
                        System.out.println("Error when added notification");
                    }
                }
            });
        }
    }
}

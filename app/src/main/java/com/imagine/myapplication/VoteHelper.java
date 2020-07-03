package com.imagine.myapplication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.post_classes.Post;

public class VoteHelper {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void updateVotes(String type, Post post){
        DocumentReference ref;
        if (post.isTopicPost) {
            ref = db.collection("TopicPosts").document(post.documentID);
        } else {
            ref = db.collection("Posts").document(post.documentID);
        }

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
}

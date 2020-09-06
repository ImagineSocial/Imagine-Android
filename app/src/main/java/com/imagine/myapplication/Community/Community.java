package com.imagine.myapplication.Community;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class Community {
    public String name;
    public String imageURL;
    public String topicID;
    public String description;
    public String displayOption;
    public String type;
    public Boolean isBeingFollowed = false;
    public long popularity;

    public Community(String name, String imageURL, String topicID, String description){
        this.name = name;
        this.imageURL = imageURL;
        this.topicID = topicID;
        this.description = description;
    }

    public void getFollowerCount(final IntegerCallback callback) {
        if (topicID != "") {
            DocumentReference ref = FirebaseFirestore.getInstance().collection("Facts").document(topicID);
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> docData = documentSnapshot.getData();
                    List<String> follower = (docData.get("follower") != null)
                            ? (List<String>) docData.get("follower")
                            : (List<String>) null;

                    int count = follower.size();
                    callback.onCallback(count);
                }
            });
        }
    }

    public void checkIfCommunityIsFollowed(final BooleanCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            if (topicID != "") {
                DocumentReference ref = db.collection("Users").document(currentUser.getUid()).collection("topics").document(topicID);

                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot result = task.getResult();
                        if (result.exists()) {
                            callback.onCallback(true);
                        } else {
                            callback.onCallback(false);
                        }
                    }
                });
            }
        } else {
            callback.onCallback(false);
        }
    }

    public void getPostCount(final IntegerCallback callback) {
        if (topicID != "") {
            Query ref = FirebaseFirestore.getInstance().collection("Facts").document(topicID).collection("posts");
            ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int count = queryDocumentSnapshots.size();
                    callback.onCallback(count);
                }
            });
        }
    }
}

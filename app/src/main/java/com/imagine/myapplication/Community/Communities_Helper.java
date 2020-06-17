package com.imagine.myapplication.Community;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.imagine.myapplication.CommunityCallback;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Communities_Helper {

    ArrayList<Community> commList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getCommunities(final CommunityCallback callback){
        Query commQuery = db.collection("Facts").orderBy("popularity", Query.Direction.DESCENDING)
                .limit(20);
        commQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                   QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap);
                    }
                    callback.onCallback(commList);
                }else{
                    System.out.println("Community Fetch FAILED!");

                }
            }
        });
    }

    public void addCommunity(DocumentSnapshot docSnap){
        String name = docSnap.getString("name");
        String topicID = docSnap.getId();
        String imageURL = docSnap.getString("imageURL");
        String description = docSnap.getString("description");

        Community comm = new Community(name,imageURL,topicID,description);
        commList.add(comm);
    }
}

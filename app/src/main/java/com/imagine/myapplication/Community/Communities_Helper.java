package com.imagine.myapplication.Community;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private static final String TAG = "Communities_Helper";
    ArrayList<Community> commList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentSnapshot lastSnap = null;

    public void getCommunities(final CommunityCallback callback){
        // fetches the initial communites from the "Facts" collection
        // ordered by popularity
        Query commQuery = db.collection("Facts").orderBy("popularity", Query.Direction.DESCENDING).limit(20);
        commQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                   QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    if(docMap.size() >0){
                        lastSnap = docMap.get(docMap.size()-1);
                    }
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap);
                    }
                    callback.onCallback(commList);
                }else{
                    System.out.println("community fetch failed! "+TAG);
                }
            }
        });
    }

    public void getMoreCommunities(final CommunityCallback callback){
        // fetches more communities from the "Facts" collection when the
        // onScrollListener is triggered
        if(lastSnap == null){
            return;
        }
        Query commQuery = db.collection("Facts")
                .orderBy("popularity", Query.Direction.DESCENDING)
                .startAfter(lastSnap)
                .limit(20);
        commQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    if(docMap.size() >0){
                        lastSnap = docMap.get(docMap.size()-1);
                    }
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap);
                    }
                    callback.onCallback(commList);
                } else if(task.isCanceled()){
                    System.out.println("getmore communitys fetch failed! "+TAG);
                }
            }
        });

    }

    public void addCommunity(DocumentSnapshot docSnap){
        // creates a Community from the docSNap and adds it to the commList
        String name = docSnap.getString("name");
        String topicID = docSnap.getId();
        String imageURL = docSnap.getString("imageURL");
        String description = docSnap.getString("description");
        Community comm = new Community(name,imageURL,topicID,description);
        commList.add(comm);
    }
}

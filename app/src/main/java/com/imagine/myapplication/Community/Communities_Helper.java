package com.imagine.myapplication.Community;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.imagine.myapplication.ArgumentsCallback;
import com.imagine.myapplication.CommunityCallback;
import com.imagine.myapplication.LinkedCommunityCallback;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Communities_Helper {
    private static final String TAG = "Communities_Helper";
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static ArrayList<Community> commCache = new ArrayList<>();
    public ArrayList<Community> topicsList = new ArrayList<>();
    public ArrayList<Community> factsList = new ArrayList<>();
    public DocumentSnapshot lastTopic = null;
    public DocumentSnapshot lastFact = null;

    public void getCommunities(final CommunityCallback callback, String userID){
        final ArrayList<Community> topics = new ArrayList<>();        // Topic-Communities Array
        final ArrayList<Community> facts = new ArrayList<>();         // Facts Communites Array
        final ArrayList<Community> ownComms = new ArrayList<>();      // Own Communites Array

        final boolean[] topicFinished = {false};
        final boolean[] factsFinished = {false};
        final boolean[] ownCommsFinished = {false};

        // Putting the Headers on TOp of the Lists
        Community commHeader = new Community("commHeader","commHeader","commHeader","commHeader");
        commHeader.type = "commHeader";
        topics.add(commHeader);
        Community recentsHeader = new Community("recents","recents","recents","recents");
        recentsHeader.type = "recentHeader";
        topics.add(recentsHeader);
        Community topicsHeader = new Community("header","header","header","header");
        topicsHeader.type = "topicsHeader";
        topics.add(topicsHeader);
        Community factsHeader = new Community("header","header","header","header");
        factsHeader.type = "factsHeader";
        facts.add(factsHeader);
        if(!userID.equals("")){
            Community ownCommsHeader = new Community("header","header","header","header");
            ownCommsHeader.type = "ownHeader";
            ownComms.add(ownCommsHeader);
        }

        // Fetching the Communities
        Query topicQuery = db.collection("Facts")
                .whereEqualTo("displayOption","topic")
                .orderBy("popularity", Query.Direction.DESCENDING)
                .limit(8);
        topicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                   QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap,topics,"topic");
                    }
                    if(factsFinished[0] && ownCommsFinished[0]){
                        addFooter(topics,facts,ownComms,callback);
                    }else{
                        topicFinished[0] = true;
                    }
                }else{
                    System.out.println("community_topic fetch failed! "+TAG);
                }
            }
        });
        //Fetching the Topics
        Query factsQuery = db.collection("Facts")
                .whereEqualTo("displayOption","fact")
                .orderBy("popularity", Query.Direction.DESCENDING)
                .limit(8);
        factsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap,facts,"fact");
                    }
                    if(topicFinished[0] && ownCommsFinished[0]){
                        addFooter(topics,facts,ownComms,callback);
                    }else{
                        factsFinished[0] = true;
                    }
                }else{
                    System.out.println("community_topic fetch failed! "+TAG);
                }
            }
        });
        // Fetching the Own Communites
        if(!userID.equals("")){
            Query ownCommsQuery = db.collection("Users").document(userID)
                    .collection("topics");
            ownCommsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot result = task.getResult();
                        List<DocumentSnapshot> docMap = result.getDocuments();
                        final int size = docMap.size();
                        final int[] count = {0};
                        for(DocumentSnapshot docSnap : docMap){
                            DocumentReference docRef = db.collection("Facts").document(docSnap.getId());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        addCommunity(documentSnapshot,ownComms,"ownComms");
                                        count[0]++;
                                        if(count[0] == size){
                                            if(topicFinished[0] && factsFinished[0]){
                                                addFooter(topics,facts,ownComms,callback);
                                            }else{
                                                ownCommsFinished[0] = true;
                                            }
                                        }
                                    }else{
                                        count[0] = count[0]+1;
                                        if(count[0] == size){
                                            if(topicFinished[0] && factsFinished[0]){
                                                addFooter(topics,facts,ownComms,callback);
                                            }else{
                                                ownCommsFinished[0] = true;
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        //callback.onCallback(commList);
                    }else{
                        System.out.println("community_topic fetch failed! "+TAG);
                    }
                }
            });
        }else{
            ownCommsFinished[0] = true;
            if(topicFinished[0] && factsFinished[0]){
                addFooter(topics,facts,ownComms,callback);
            }
        }


    }

    public void addFooter(ArrayList<Community> topics,ArrayList<Community> facts,
                          ArrayList<Community> ownComms,CommunityCallback callback){
        Community topicsFooter = new Community("footer","footer","topicsFooter","footer");
        topicsFooter.type = "footer";
        topics.add(topicsFooter);
        Community factsFooter = new Community("footer","footer","factsFooter","footer");
        factsFooter.type = "footer";
        facts.add(factsFooter);
        ArrayList<Community> finishedList = topics;
        finishedList.addAll(facts);
        finishedList.addAll(ownComms);
        callback.onCallback(finishedList);
    }

    public void getMoreTopics(final CommunityCallback callback){
        // fetches more communities from the "Facts" collection when the
        // onScrollListener is triggered
        if(lastTopic == null){
            return;
        }
        Query commQuery = db.collection("Facts")
                .orderBy("popularity", Query.Direction.DESCENDING)
                .whereEqualTo("displayOption","topic")
                .startAfter(lastTopic)
                .limit(20);
        commQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    if(docMap.size() >0){
                        lastTopic = docMap.get(docMap.size()-1);
                    }
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap,topicsList,"topic");
                    }
                    callback.onCallback(topicsList);
                } else if(task.isCanceled()){
                    System.out.println("getmore communitys fetch failed! "+TAG);
                }
            }
        });

    }

    public void fetchLinkedCommunity(final String linkedCommID, final LinkedCommunityCallback callback){
        if(linkedCommID != null && !linkedCommID.equals("")){
            Community comm = getCommFromCache(linkedCommID);
            if(comm == null){
                DocumentReference commRef = db.collection("Facts").document(linkedCommID);
                commRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> docData = documentSnapshot.getData();
                        final String communityName = (docData.get("name") != null)      // Condition
                                ? (String) docData.get("name")              // IF-True
                                : (String) "";                              // ELSE
                        final String communityImageURL = (docData.get("imageURL") != null)
                                ? (String) docData.get("imageURL")
                                : (String) "";
                        final String communityID = linkedCommID;
                        final String description = (docData.get("description") != null)
                                ? (String) docData.get("description")
                                : (String) "";
                        final String displayOption =(String) docData.get("displayOption");
                        Community comm = new Community(communityName,communityImageURL,communityID,description);
                        comm.displayOption = displayOption;
                        callback.onCallback(comm);
                        if(commCache.size() <= 20){
                            commCache.add(comm);
                        }else{
                            commCache.remove(19);
                            commCache.add(comm);
                        }
                    }
                });
            }else {
                callback.onCallback(comm);
            }
        }else{
            return;
        }
    }

    public Community getCommFromCache(String commID){
        for(Community comm : commCache){
            if(comm.topicID.equals(commID)){
                return comm;
            }
        }
        return null;
    }

    public void getMoreFacts(final CommunityCallback callback){
        // fetches more communities from the "Facts" collection when the
        // onScrollListener is triggered
        if(lastFact == null){
            return;
        }
        Query commQuery = db.collection("Facts")
                .orderBy("popularity", Query.Direction.DESCENDING)
                .whereEqualTo("displayOption","fact")
                .startAfter(lastFact)
                .limit(20);
        commQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    if(docMap.size() >0){
                        lastFact = docMap.get(docMap.size()-1);
                    }
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap,factsList,"fact");
                    }
                    callback.onCallback(factsList);
                } else if(task.isCanceled()){
                    System.out.println("getmore communitys fetch failed! "+TAG);
                }
            }
        });

    }

    public void getTopics(final CommunityCallback callback){
        // fetches  communities from the "Facts" collection when the
        // onScrollListener is triggered
        Query topicQuery = db.collection("Facts")
                .whereEqualTo("displayOption","topic")
                .orderBy("popularity", Query.Direction.DESCENDING)
                .limit(20);
        topicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    if(docMap.size() >0){
                        lastTopic = docMap.get(docMap.size()-1);
                    }
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap,topicsList,"topic");
                    }
                    callback.onCallback(topicsList);
                }else{
                    System.out.println("community_topic fetch failed! "+TAG);
                }
            }
        });
    }



    public void getFacts(final CommunityCallback callback){
        // fetches  communities from the "Facts" collection when the
        // onScrollListener is triggered
        Query topicQuery = db.collection("Facts")
                .whereEqualTo("displayOption","fact")
                .orderBy("popularity", Query.Direction.DESCENDING)
                .limit(20);
        topicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    if(docMap.size() >0){
                        lastFact = docMap.get(docMap.size()-1);
                    }
                    for(DocumentSnapshot docSnap : docMap){
                        addCommunity(docSnap,factsList,"fact");
                    }
                    callback.onCallback(factsList);
                }else{
                    System.out.println("community_topic fetch failed! "+TAG);
                }
            }
        });
    }

    public void getOwnCommunities(final CommunityCallback callback, String userID){
        // Fetching the Own Communites
        final ArrayList<Community> ownComms = new ArrayList<>();
        Query ownCommsQuery = db.collection("Users").document(userID)
                .collection("topics");
        ownCommsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> docMap = result.getDocuments();
                    final int size = docMap.size();
                    final int[] count = {0};
                    for(DocumentSnapshot docSnap : docMap){
                        DocumentReference docRef = db.collection("Facts").document(docSnap.getId());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    addCommunity(documentSnapshot,ownComms,"ownComms");
                                    count[0]++;
                                    if(count[0] == size){
                                        callback.onCallback(ownComms);
                                    }
                                }else{
                                    count[0]++;
                                    System.out.println("Fetch Failed"+TAG);
                                }
                            }
                        });
                    }
                }else{
                    System.out.println("community_topic fetch failed! "+TAG);
                }
            }
        });
    }

    public void getProArguments(String commID,final ArgumentsCallback callback){
        final ArrayList<Argument> pros = new ArrayList<>();
        Query proQuery = db.collection("Facts").document(commID).collection("arguments")
                .whereEqualTo("proOrContra","pro");
        proQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot docSnap : queryDocumentSnapshots){
                    Argument argument = new Argument();
                    argument.description = docSnap.getString("description");
                    //argument.downVotes = docSnap.getLong("downVotes");
                    argument.OP = docSnap.getString("OP");
                    argument.proOrCon = docSnap.getString("proOrContra");
                    argument.title = docSnap.getString("title");
                    //argument.upVotes = docSnap.getLong("upVotes");
                    pros.add(argument);
                }
                callback.onCallback(pros);
            }
        });
    }

    public void getContraArguments(String commID,final ArgumentsCallback callback){
        final ArrayList<Argument> cons = new ArrayList<>();
        Query proQuery = db.collection("Facts").document(commID).collection("arguments")
                .whereEqualTo("proOrContra","contra");
        proQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot docSnap : queryDocumentSnapshots){
                    Argument argument = new Argument();
                    argument.description = docSnap.getString("description");
                    //argument.downVotes = docSnap.getLong("downVotes");
                    argument.OP = docSnap.getString("OP");
                    argument.proOrCon = docSnap.getString("proOrContra");
                    argument.title = docSnap.getString("title");
                    //argument.upVotes = docSnap.getLong("upVotes");
                    cons.add(argument);
                }
                callback.onCallback(cons);
            }
        });
    }

    public void addCommunity(DocumentSnapshot docSnap, ArrayList<Community> list,String type){
        // creates a Community from the docSNap and adds it to the commList
        String name = docSnap.getString("name");
        String topicID = docSnap.getId();
        String imageURL = docSnap.getString("imageURL");
        String description = docSnap.getString("description");
        String displayOption = docSnap.getString("displayOption");
        Community comm = new Community(name,imageURL,topicID,description);
        comm.displayOption = displayOption;
        comm.popularity = (long) docSnap.getLong("popularity");
        comm.type = type;
        list.add(comm);
    }
}

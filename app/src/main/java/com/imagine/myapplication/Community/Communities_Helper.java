package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

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
import com.imagine.myapplication.MainActivity;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Communities_Helper {
    private static final String TAG = "Communities_Helper";
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static ArrayList<Community> commCache = new ArrayList<>();
    public ArrayList<Community> topicsList = new ArrayList<>();
    public ArrayList<Community> factsList = new ArrayList<>();
    public DocumentSnapshot lastTopic = null;
    public DocumentSnapshot lastFact = null;
    public Context mContext;

    public Communities_Helper(Context mContext) {
        this.mContext = mContext;
    }

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
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        final Locale locale = conf.locale;
        Query topicQuery;
        switch(locale.getLanguage()){
            case "de":
                topicQuery = db.collection("Facts")
                        .whereEqualTo("displayOption","topic")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .limit(8);
                break;
            case "en":
                topicQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .whereEqualTo("displayOption","topic")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .limit(8);
                break;
            default:
                topicQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .whereEqualTo("displayOption","topic")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .limit(8);
                break;
        }
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

        Query factsQuery;
        switch(locale.getLanguage()){
            case "de":
                factsQuery = db.collection("Facts")
                        .whereEqualTo("displayOption","fact")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .limit(8);
                break;
            case "en":
                factsQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .whereEqualTo("displayOption","fact")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .limit(8);
                break;
            default:
                factsQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .whereEqualTo("displayOption","fact")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .limit(8);
                break;
        }
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
                            DocumentReference docRef;
                            String commLang = docSnap.getString("language");
                            String selectLang = locale.getLanguage();
                            if(commLang == null){
                                if(selectLang.equals("de")){
                                    docRef = db.collection("Facts").document(docSnap.getId());
                                }
                                else{
                                    count[0]++;
                                    if(count[0] == size){
                                        if(topicFinished[0] && factsFinished[0]){
                                            addFooter(topics,facts,ownComms,callback);
                                        }else{
                                            ownCommsFinished[0] = true;
                                        }
                                    }
                                    continue;
                                }
                            }else{
                                if(locale.getLanguage().equals("en")){
                                    docRef = db.collection("Data").document("en")
                                            .collection("topics").document(docSnap.getId());
                                }else{
                                    count[0]++;
                                    if(count[0] == size){
                                        if(topicFinished[0] && factsFinished[0]){
                                            addFooter(topics,facts,ownComms,callback);
                                        }else{
                                            ownCommsFinished[0] = true;
                                        }
                                    }
                                    continue;
                                }
                            }
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
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        final Locale locale = conf.locale;
        Query commQuery;
        switch(locale.getLanguage()){
            case "de":
                commQuery = db.collection("Facts")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","topic")
                        .startAfter(lastTopic)
                        .limit(20);
                break;
            case "en":
                commQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","topic")
                        .startAfter(lastTopic)
                        .limit(20);
                break;
            default:
                commQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","topic")
                        .startAfter(lastTopic)
                        .limit(20);
                break;
        }
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
                Configuration conf = MainActivity.configContext.getResources().getConfiguration();
                final Locale locale = conf.locale;
                DocumentReference commRef;
                switch(locale.getLanguage()){
                    case "de":
                        commRef = db.collection("Facts").document(linkedCommID);
                        break;
                    case "en":
                        commRef = db.collection("Data").document("en").collection("topics")
                        .document(linkedCommID);
                        break;
                    default:
                        commRef = db.collection("Data").document("en").collection("topics")
                                .document(linkedCommID);
                        break;

                }
                commRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> docData = documentSnapshot.getData();
                        if (docData != null) {
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
                            final long postCount = (docData.get("postCount") != null)
                                    ? (long) docData.get("postCount")
                                    : (long) 0;
                            List<String> follower = (docData.get("follower") != null)
                                    ? (List<String>) docData.get("follower")
                                    : (List<String>) new ArrayList();
                            int followerCount = follower.size();

                            final String displayOption = (String) docData.get("displayOption");
                            Community comm = new Community(communityName, communityImageURL, communityID, description);
                            comm.displayOption = displayOption;
                            comm.followerCount = followerCount;
                            comm.postCount = (int) postCount;
                            callback.onCallback(comm);
                            if (commCache.size() <= 20) {
                                commCache.add(comm);
                            } else {
                                commCache.remove(19);
                                commCache.add(comm);
                            }
                        } else {
                            System.out.println("##no document for community because Matz was too busy watching granny hentai");
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
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        final Locale locale = conf.locale;
        Query commQuery;
        switch(locale.getLanguage()){
            case "de":
                commQuery = db.collection("Facts")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","fact")
                        .startAfter(lastTopic)
                        .limit(20);
                break;
            case "en":
                commQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","fact")
                        .startAfter(lastTopic)
                        .limit(20);
                break;
            default:
                commQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","fact")
                        .startAfter(lastTopic)
                        .limit(20);
                break;
        }
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
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        final Locale locale = conf.locale;
        Query commQuery;
        switch(locale.getLanguage()){
            case "de":
                commQuery = db.collection("Facts")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","topic")
                        .limit(20);
                break;
            case "en":
                commQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","topic")
                        .limit(20);
                break;
            default:
                commQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","topic")
                        .limit(20);
                break;
        }
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
                }else{
                    System.out.println("community_topic fetch failed! "+TAG);
                }
            }
        });
    }



    public void getFacts(final CommunityCallback callback){
        // fetches  communities from the "Facts" collection when the
        // onScrollListener is triggered
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        final Locale locale = conf.locale;
        Query commQuery;
        switch(locale.getLanguage()){
            case "de":
                commQuery = db.collection("Facts")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","fact")
                        .limit(20);
                break;
            case "en":
                commQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","fact")
                        .limit(20);
                break;
            default:
                commQuery = db.collection("Data").document("en")
                        .collection("topics")
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .whereEqualTo("displayOption","fact")
                        .limit(20);
                break;
        }
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
                        DocumentReference docRef;
                        String commLang = docSnap.getString("language");
                        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
                        final Locale locale = conf.locale;
                        if(commLang == null){
                            if(locale.getLanguage().equals("de")){
                                docRef = db.collection("Facts").document(docSnap.getId());
                            }else{
                                count[0]++;
                                if(count[0] == size){
                                    callback.onCallback(ownComms);
                                }
                                continue;
                            }
                        }else{
                            docRef = db.collection("Data").document("en")
                                    .collection("topics").document(docSnap.getId());
                        }
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
                                    if(count[0] == size){
                                        callback.onCallback(ownComms);
                                    }
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
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        final Locale locale = conf.locale;
        Query proQuery;
        switch(locale.getLanguage()){
            case "de":
                proQuery = db.collection("Facts").document(commID).collection("arguments")
                        .whereEqualTo("proOrContra","pro");
                break;
            case "en":
                proQuery = db.collection("Data").document("en").collection("topics")
                        .document(commID).collection("arguments")
                        .whereEqualTo("proOrContra","pro");
                break;
            default:
                proQuery = db.collection("Data").document("en").collection("topics")
                        .document(commID).collection("arguments")
                        .whereEqualTo("proOrContra","pro");
                break;
        }
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
                    argument.type = "arg";
                    argument.proOrCon = "pro";
                    //argument.upVotes = docSnap.getLong("upVotes");
                    pros.add(argument);
                }
                addArgumentsFooter(callback,pros,"pro");
            }
        });
    }

    public void getContraArguments(String commID,final ArgumentsCallback callback){
        final ArrayList<Argument> cons = new ArrayList<>();
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        final Locale locale = conf.locale;
        Query proQuery;
        switch(locale.getLanguage()){
            case "de":
                proQuery = db.collection("Facts").document(commID).collection("arguments")
                        .whereEqualTo("proOrContra","contra");
                break;
            case "en":
                proQuery = db.collection("Data").document("en").collection("topics")
                        .document(commID).collection("arguments")
                        .whereEqualTo("proOrContra","contra");
                break;
            default:
                proQuery = db.collection("Data").document("en").collection("topics")
                        .document(commID).collection("arguments")
                        .whereEqualTo("proOrContra","contra");
                break;
        }
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
                    argument.type = "arg";
                    argument.proOrCon = "contra";
                    //argument.upVotes = docSnap.getLong("upVotes");
                    cons.add(argument);
                }
                addArgumentsFooter(callback,cons,"contra");
            }
        });
    }

    public void addArgumentsFooter(ArgumentsCallback callback, ArrayList<Argument> args, String proOrCon){
        Argument arg = new Argument();
        if(proOrCon.equals("pro"))arg.proOrCon = "pro";
        else arg.proOrCon = "contra";
        arg.type = "footer";
        args.add(arg);
        callback.onCallback(args);
    }

    public void addCommunity(DocumentSnapshot docSnap, ArrayList<Community> list,String type){
        // creates a Community from the docSNap and adds it to the commList
        String name = docSnap.getString("name");
        String topicID = docSnap.getId();
        String imageURL = docSnap.getString("imageURL");
        String description = docSnap.getString("description");
        String displayOption = docSnap.getString("displayOption");
        Double postCount = docSnap.getDouble("postCount");
        List<String> follower = (List<String>) docSnap.get("follower");
        Community comm = new Community(name,imageURL,topicID,description);
        comm.displayOption = displayOption;
        Double popularity = docSnap.getDouble("popularity");
        if (popularity != null) {
            comm.popularity = (long) popularity.longValue();
        }
        comm.type = type;
        if (postCount!= null) {
            comm.postCount = postCount.intValue();
        }
        if (follower != null) {
            comm.followerCount = follower.size();
        }
        list.add(comm);
    }
}

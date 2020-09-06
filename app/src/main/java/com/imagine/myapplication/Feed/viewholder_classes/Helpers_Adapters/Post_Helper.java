package com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.imagine.myapplication.Comment;
import com.imagine.myapplication.CommentsCallback;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.PostRef;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.ItemCallback;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.post_classes.DefaultPost;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.LinkPost;
import com.imagine.myapplication.post_classes.MultiPicturePost;
import com.imagine.myapplication.post_classes.PicturePost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.post_classes.RepostPost;
import com.imagine.myapplication.post_classes.ThoughtPost;
import com.imagine.myapplication.post_classes.TranslationPost;
import com.imagine.myapplication.post_classes.YouTubePost;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Post_Helper {

    private static final String TAG = "Post_Helper";
    public DocumentSnapshot lastSnap;
    public Timestamp lastSnapTimeSaver;
    public Timestamp lastSnapTime;
    public ArrayList<Post> postList = new ArrayList<Post>();
    public ArrayList<Post> commPostList = new ArrayList<>();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public StorageReference storage = FirebaseStorage.getInstance().getReference();
    public ArrayList<Object> items = new ArrayList<>();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public boolean firstFetch = false;
    public boolean moreFetch = false;
    public String commID;
    public String userID;
    public int count;
    public int size;

    public  void getPostsForMainFeed( final FirebaseCallback callback){
        // fetches the initial posts for the main feed
        firstFetch = true;
        Query postsRef = db.collection("Posts").orderBy("createTime",Query.Direction.DESCENDING).limit(15);
        postsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(queryDocumentSnapshots.getDocuments().size() >=1){
                        lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        lastSnapTime = lastSnap.getTimestamp("createTime");
                    }
                    for(QueryDocumentSnapshot docSnap : queryDocumentSnapshots){
                        switch((String)docSnap.get("type")){
                            case "thought":
                                addThoughtPost(docSnap,false,false, false);
                                break;
                            case "youTubeVideo":
                                addYouTubePost(docSnap,false,false, false);
                                break;
                            case "link":
                                addLinkPost(docSnap,false,false, false);
                                break;
                            case "GIF":
                                addGIFPost(docSnap,false,false, false);
                                break;
                            case "picture":
                                addPicturePost(docSnap,false,false, false);
                                break;
                            case "multiPicture":
                                addMultiPicturePost(docSnap,false,false, false);
                                break;
                            case "translation":
                                addTranslationPost(docSnap,false,false, false);
                                break;
                            case "repost":
                                addRepostPost(docSnap,false,false, false);
                                break;
                            default:
                                addDefaulPost(docSnap,false,false, false);
                                break;
                        }
                    }
                    Log.d(TAG,"From Post_Helper"+postList.toString());
                    if(auth.getCurrentUser() != null){
                        getFollowedCommunities(callback);
                    }else{
                        mergeAndSortPostLists(callback);
                    }
                }
            }
        });
    }

    public void getFollowedCommunities(final FirebaseCallback callback){
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            callback.onCallback(postList);
        }else{
            String userID = user.getUid();
            CollectionReference factsColl = db.collection("Facts");
            Query factsQuery = factsColl.whereArrayContains("follower",userID);
            factsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot result = task.getResult();
                        if(result == null){
                            callback.onCallback(postList);
                        }else{
                            List<DocumentSnapshot> documentSnapshots = result.getDocuments();
                            getCommunityIDsFromFollowedCommunities(callback,documentSnapshots);
                        }
                    }else if(task.isCanceled()){
                        callback.onCallback(postList);
                    }
                }
            });
        }
    }

    public void getCommunityIDsFromFollowedCommunities(final FirebaseCallback callback, List<DocumentSnapshot> documentSnapshots){
        ArrayList<String> commIDs = new ArrayList<>();
        if(documentSnapshots.size() == 0){
            callback.onCallback(postList);
        }else{
            for(DocumentSnapshot docSnap: documentSnapshots){
                String communityID = docSnap.getId();
                commIDs.add(communityID);
            }
            getPostsFromCommunityIDs(callback,commIDs);
        }
    }

    public void getPostsFromCommunityIDs(final FirebaseCallback callback, ArrayList<String> commIDs){
        CollectionReference topicRef = db.collection("TopicPosts");
        final int size = commIDs.size();
        final int [] count = {0};
        for(String id: commIDs){
            Query postsQuery;
            if(lastSnapTimeSaver == null || firstFetch){
                postsQuery = topicRef.whereEqualTo("linkedFactID",id)
                        .whereGreaterThan("createTime",lastSnapTime);
            }else{
                postsQuery = topicRef.whereEqualTo("linkedFactID",id)
                        .whereGreaterThan("createTime",lastSnapTime)
                        .whereLessThan("createTime",lastSnapTimeSaver);
            }
            postsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isComplete()){
                        QuerySnapshot result = task.getResult();
                        if(result != null){
                            List<DocumentSnapshot> postList = result.getDocuments();
                            for(DocumentSnapshot docSnap : postList){
                                switch((String)docSnap.get("type")){
                                    case "thought":
                                        addThoughtPost(docSnap,false,false,true);
                                        break;
                                    case "youTubeVideo":
                                        addYouTubePost(docSnap,false,false,true);
                                        break;
                                    case "link":
                                        addLinkPost(docSnap,false,false,true);
                                        break;
                                    case "GIF":
                                        addGIFPost(docSnap,false,false,true);
                                        break;
                                    case "picture":
                                        addPicturePost(docSnap,false,false,true);
                                        break;
                                    case "multiPicture":
                                        addMultiPicturePost(docSnap,false,false,true);
                                        break;
                                    case "translation":
                                        addTranslationPost(docSnap,false,false,true);
                                        break;
                                    case "repost":
                                        addRepostPost(docSnap,false,false,true);
                                        break;
                                    default:
                                        addDefaulPost(docSnap,false,false,true);
                                        break;
                                }
                            }
                            count[0] = count[0]+1;
                            if(count[0] == size){
                                mergeAndSortPostLists(callback);
                            }
                        }
                    }else{
                        System.out.println("!");
                    }
                }
            });
        }
    }

    public void mergeAndSortPostLists(FirebaseCallback callback){
        ArrayList<Post> finalList = postList;
        finalList.addAll(commPostList);
        postList = new ArrayList<>();
        commPostList = new ArrayList<>();
        callback.onCallback(finalList);
        firstFetch = false;
        moreFetch = false;
    }

    public void getMorePostsForFeed(final FirebaseCallback callback){
        // called when onScrollListener triggers and fetches more post for the main feed
        // whole postLists is returned
        moreFetch = true;
        lastSnapTimeSaver = lastSnapTime;
        Query postsRef = db.collection("Posts").orderBy("createTime",Query.Direction.DESCENDING).startAfter(lastSnap).limit(15);
        postsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(queryDocumentSnapshots.getDocuments().size() >=1){
                        lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        lastSnapTime = lastSnap.getTimestamp("createTime");
                    }
                    System.out.println(queryDocumentSnapshots.size());
                    for(QueryDocumentSnapshot docSnap : queryDocumentSnapshots){
                        switch((String)docSnap.get("type")){
                            case "thought":
                                addThoughtPost(docSnap,false,false,false);
                                break;
                            case "youTubeVideo":
                                addYouTubePost(docSnap,false,false,false);
                                break;
                            case "link":
                                addLinkPost(docSnap,false,false,false);
                                break;
                            case "GIF":
                                addGIFPost(docSnap,false,false,false);
                                break;
                            case "picture":
                                addPicturePost(docSnap,false,false,false);
                                break;
                            case "multiPicture":
                                addMultiPicturePost(docSnap,false,false,false);
                                break;
                            case "translation":
                                addTranslationPost(docSnap,false,false,false);
                                break;
                            case "repost":
                                addRepostPost(docSnap,false,false,false);
                                break;
                            default:
                                addDefaulPost(docSnap,false,false,false);
                                break;
                        }
                    }
                    getFollowedCommunities(callback);
                    System.out.println("FERTIG");
                }
            }
        });
    }

    public  void getPostsForCommunityFeed(String commID,final FirebaseCallback callback){
        // fetches the initial posts for the communityfeed
        if(firstFetch){
            postList = new ArrayList<>();
        }else{
            firstFetch = true;
        }
        this.commID = commID;
        Query postsColl = db.collection("Facts").document(commID).collection("posts")
                .orderBy("createTime", Query.Direction.DESCENDING)
                .limit(20);
        postsColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                count = 0;
                size = queryDocumentSnapshots.size();
                if(queryDocumentSnapshots.getDocuments().size() >=1){
                    lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                }
                if(count == 0 && size == 0){
                    callback.onCallback(postList);
                }
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    String postID = queryDocumentSnapshot.getId();
                    String typeOne = queryDocumentSnapshot.getString("type") == null ?
                            "feedPost"
                            : queryDocumentSnapshot.getString("type") ;
                    final boolean isTopicPost = queryDocumentSnapshot.getBoolean("isTopicPost") != null;
                    DocumentReference docRef = typeOne.equals("topicPost") ?
                            db.collection("TopicPosts").document(postID) :
                            db.collection("Posts").document(postID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                String id = documentSnapshot.getId();
                                String type = documentSnapshot.getString("type");
                                if(type == null ){
                                    count++;
                                    return;
                                }
                                System.out.println(id+"   "+type);
                                switch(documentSnapshot.getString("type")){
                                    case "thought":
                                        addThoughtPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "youTubeVideo":
                                        addYouTubePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "link":
                                        addLinkPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "GIF":
                                        addGIFPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "picture":
                                        addPicturePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "multiPicture":
                                        addMultiPicturePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "translation":
                                        addTranslationPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "repost":
                                        addRepostPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    default:
                                        addDefaulPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                }
                                count++;
                                if(count == size)callback.onCallback(postList);
                            }else if(task.isCanceled()){
                                count++;
                                if(count == size)callback.onCallback(postList);
                            }
                        }
                    });
                }
            }
        });
    }

    public void getMorePostsForCommunityFeed(final FirebaseCallback callback){
        // fetches more post for the community_topic feed when the onscrollListener is
        // triggered returns the whole postsList
        if(lastSnap == null || this.commID == null){
            return;
        }
        Query postsColl = db.collection("Facts").document(commID).collection("posts")
                .orderBy("createTime", Query.Direction.DESCENDING)
                .startAfter(lastSnap)
                .limit(20);
        postsColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                count = 0;
                size = queryDocumentSnapshots.size();
                if(size ==0) {
                    return;
                }
                if(queryDocumentSnapshots.getDocuments().size() >=1){
                    lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                }
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    String postID = queryDocumentSnapshot.getId();
                    String typeOne = queryDocumentSnapshot.getString("type") == null ?
                            "feedPost"
                            : queryDocumentSnapshot.getString("type") ;
                    final boolean isTopicPost = queryDocumentSnapshot.getBoolean("isTopicPost") != null;
                    DocumentReference docRef = typeOne.equals("topicPost") ?
                            db.collection("TopicPosts").document(postID) :
                            db.collection("Posts").document(postID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                String id = documentSnapshot.getId();
                                String type = documentSnapshot.getString("type");
                                if(type == null ){
                                    count++;
                                    return;
                                }
                                System.out.println(id+"   "+type);
                                switch(documentSnapshot.getString("type")){
                                    case "thought":
                                        addThoughtPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "youTubeVideo":
                                        addYouTubePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "link":
                                        addLinkPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "GIF":
                                        addGIFPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "picture":
                                        addPicturePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "multiPicture":
                                        addMultiPicturePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "translation":
                                        addTranslationPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "repost":
                                        addRepostPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    default:
                                        addDefaulPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                }
                                count++;
                                if(count == size){
                                    callback.onCallback(postList);
                                };
                            }else if(task.isCanceled()){
                                count++;
                                if(count == size){
                                    callback.onCallback(postList);
                                };
                            }
                        }
                    });
                }
            }
        });
    }

    public  void getPostsForUserFeed( final FirebaseCallback callback,String userID){
        // fetches the initial posts for the userfeed
        if(firstFetch){
            postList = new ArrayList<>();
        }else{
            firstFetch = true;
        }
        this.userID = userID;
        Query postsColl = db.collection("Users").document(userID).collection("posts")
                .orderBy("createTime", Query.Direction.DESCENDING)
                .limit(20);
        postsColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                count = 0;
                size = queryDocumentSnapshots.size();
                if(count == 0 && size == 0){
                    callback.onCallback(postList);
                }
                if(queryDocumentSnapshots.getDocuments().size() >=1){
                    lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                }
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    String postID = queryDocumentSnapshot.getId();
                    String typeOne = queryDocumentSnapshot.getBoolean("isTopicPost") == null ?
                            "feedPost"
                            : "topicPost" ;
                    final boolean isTopicPost = queryDocumentSnapshot.getBoolean("isTopicPost") != null;
                    DocumentReference docRef = typeOne.equals("topicPost") ?
                            db.collection("TopicPosts").document(postID) :
                            db.collection("Posts").document(postID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                String id = documentSnapshot.getId();
                                String type = documentSnapshot.getString("type");
                                if(type == null ){
                                    count++;
                                    return;
                                }
                                System.out.println(id+"   "+type);
                                switch(documentSnapshot.getString("type")){
                                    case "thought":
                                        addThoughtPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "youTubeVideo":
                                        addYouTubePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "link":
                                        addLinkPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "GIF":
                                        addGIFPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "picture":
                                        addPicturePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "multiPicture":
                                        addMultiPicturePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "translation":
                                        addTranslationPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "repost":
                                        addRepostPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    default:
                                        addDefaulPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                }
                                count++;
                                if(count == size){
                                    callback.onCallback(postList);
                                };
                            }else if(task.isCanceled()){
                                count++;
                                if(count == size){
                                    callback.onCallback(postList);
                                };
                            }
                        }
                    });
                }
            }
        });
    }

    public void getMorePostsForUserFeed(final FirebaseCallback callback){
        // fetches more post for the user feed when the onscrolllistener is triggered
        // returns the whole postList
        if(lastSnap == null || this.userID == null){
            return;
        }
        Query postsColl = db.collection("Users").document(userID).collection("posts")
                .orderBy("createTime", Query.Direction.DESCENDING)
                .startAfter(lastSnap)
                .limit(20);
        postsColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                count = 0;
                size = queryDocumentSnapshots.size();
                if(size == 0) {
                    return;
                }
                if(queryDocumentSnapshots.getDocuments().size() >=1){
                    lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                }
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    String postID = queryDocumentSnapshot.getId();
                    String typeOne = queryDocumentSnapshot.getBoolean("isTopicPost") == null ?
                            "feedPost"
                            : "topicPost" ;
                    final boolean isTopicPost = queryDocumentSnapshot.getBoolean("isTopicPost") != null;
                    DocumentReference docRef = typeOne.equals("topicPost") ?
                            db.collection("TopicPosts").document(postID) :
                            db.collection("Posts").document(postID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                String id = documentSnapshot.getId();
                                String type = documentSnapshot.getString("type");
                                if(type == null ){
                                    count++;
                                    return;
                                }
                                System.out.println(id+"   "+type);
                                switch(documentSnapshot.getString("type")){
                                    case "thought":
                                        addThoughtPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "youTubeVideo":
                                        addYouTubePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "link":
                                        addLinkPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "GIF":
                                        addGIFPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "picture":
                                        addPicturePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "multiPicture":
                                        addMultiPicturePost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "translation":
                                        addTranslationPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    case "repost":
                                        addRepostPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                    default:
                                        addDefaulPost(documentSnapshot,isTopicPost,false,false);
                                        break;
                                }
                                count++;
                                if(count == size){
                                    callback.onCallback(postList);
                                };
                            }else if(task.isCanceled()){
                                count++;
                                if(count == size){
                                    callback.onCallback(postList);
                                };
                            }
                        }
                    });
                }
            }
        });
    }

    public void getComments(String postID, final CommentsCallback callback){
        // fetches the comments for the postActivitys
        final ArrayList<Comment> commArray = new ArrayList<>();
        Query commRef = db.collection("Comments").document(postID)
                .collection("threads").orderBy("sentAt", Query.Direction.ASCENDING);
        commRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> docsList = querySnapshot.getDocuments();
                    for(DocumentSnapshot docSnap: docsList){
                        final Comment comm = new Comment();
                        comm.body = docSnap.getString("body");
                        //comm.id = docSnap.getLong("is");
                        Timestamp sentAt = docSnap.getTimestamp("sentAt");
                        comm.sentAt = sentAt;
                        comm.userID = docSnap.getString("userID");
                        Long timeNow = new Date().getTime();
                        String dateString = convertLongDateToAgoString(sentAt.toDate(),timeNow);
                        comm.sentAtString = dateString;
                        commArray.add(comm);
                        DocumentReference userRef = db.collection("User").document(comm.userID);
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot userSnap = task.getResult();
                                    String name = userSnap.getString("name");
                                    String userID = userSnap.getId();
                                    String surname = userSnap.getString("surname");
                                    User user = new User(name,surname,userID);
                                    user.imageURL = userSnap.getString("imageURL");
                                    comm.setUser(user);
                            }else if(task.isCanceled()){
                                    System.out.println("User Fetchfailed!");
                                }
                        }
                        });
                    }
                    callback.onCallback(commArray);
                }else if(task.isCanceled()){
                    System.out.println("Task Failed!");
                }
            }
        });
    }

    public void getCommunityPosts(final FirebaseCallback callback){
        // Fetches all the GIF, MultiPicture and PicturePost from the
        // CommunityPosts Collection
        ArrayList<String> stringArray = new ArrayList<>();
        stringArray.add("GIF");
        stringArray.add("multiPicture");
        stringArray.add("picture");
        Query postsQuery = db.collection("TopicPosts").whereIn("type",stringArray)
                .orderBy("createTime", Query.Direction.DESCENDING)
                .limit(50);
        postsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snap = task.getResult();
                    List<DocumentSnapshot> snaps = snap.getDocuments();
                    int size = snaps.size();
                    for(DocumentSnapshot docSnap : snaps){
                       String type = docSnap.getString("type");
                       switch(type){
                           case "picture":
                               addPicturePost(docSnap,true,false,false);
                               break;
                           case "multiPicture":
                               addMultiPicturePost(docSnap,true,false,false);
                               break;
                           case "GIF":
                               addGIFPost(docSnap,true,false,false);
                               break;
                           default:
                               addDefaulPost(docSnap,true,false,false);
                               break;
                       }
                    }
                    callback.onCallback(postList);
                }else if(task.isCanceled()){
                    System.out.println("Default Case getCommunityPosts");
                }
            }
        });
    }

    public void fetchAddonItems(ArrayList<PostRef> postRefs, final ItemCallback callback){
        for(PostRef ref : postRefs){
            final int size = postRefs.size();
            final int[] count = {0};
            switch(ref.type){
                case "fact":
                    DocumentReference communityRef = db.collection("Facts").document(ref.postID);
                    communityRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot docSnap = task.getResult();
                                addCommunityToItemList(docSnap);
                                if(items.size() == size){
                                    callback.onCallback(items);
                                };
                            } else if(task.isCanceled()){
                                System.out.println("!");
                            }
                        }
                    });
                    break;
                case "post":
                    DocumentReference postsRef = db.collection("Posts").document(ref.postID);
                    postsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot docSnap = task.getResult();
                                try{
                                    String type = docSnap.getString("type");
                                    switch(type){
                                        case "thought":
                                            addThoughtPost(docSnap,true,true,false);
                                            break;
                                        case "youTubeVideo":
                                            addYouTubePost(docSnap,true,true,false);
                                            break;
                                        case "link":
                                            addLinkPost(docSnap,true,true,false);
                                            break;
                                        case "GIF":
                                            addGIFPost(docSnap,true,true,false);
                                            break;
                                        case "picture":
                                            addPicturePost(docSnap,true,true,false);
                                            break;
                                        case "multiPicture":
                                            addMultiPicturePost(docSnap,true,true,false);
                                            break;
                                        case "translation":
                                            addTranslationPost(docSnap,true,true,false);
                                            break;
                                        case "repost":
                                            addRepostPost(docSnap,true,true,false);
                                            break;
                                        default:
                                            addDefaulPost(docSnap,true,true,false);
                                            break;
                                    }
                                }catch(Exception e){
                                    addDefaulPost(docSnap,true,true,false);
                                }

                                if(items.size() == size){
                                    callback.onCallback(items);
                                }
                            }else if(task.isCanceled()){
                                System.out.println("Fetch failed!  618!");
                                count[0]++;
                                if(count[0] == size){
                                    callback.onCallback(items);
                                }
                            }
                        }
                    });
                    break;
                case "topicPost":
                    DocumentReference topicPostsRef = db.collection("TopicPosts").document(ref.postID);
                    topicPostsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot docSnap = task.getResult();
                                try{
                                    String type = docSnap.getString("type");
                                    switch(type){
                                        case "thought":
                                            addThoughtPost(docSnap,true,true, false);
                                            break;
                                        case "youTubeVideo":
                                            addYouTubePost(docSnap,true,true ,false);
                                            break;
                                        case "link":
                                            addLinkPost(docSnap,true,true,false);
                                            break;
                                        case "GIF":
                                            addGIFPost(docSnap,true,true,false);
                                            break;
                                        case "picture":
                                            addPicturePost(docSnap,true,true,false);
                                            break;
                                        case "multiPicture":
                                            addMultiPicturePost(docSnap,true,true,false);
                                            break;
                                        case "translation":
                                            addTranslationPost(docSnap,true,true,false);
                                            break;
                                        case "repost":
                                            addRepostPost(docSnap,true,true,false);
                                            break;
                                        default:
                                            addDefaulPost(docSnap,true,true,false);
                                            break;
                                    }
                                }catch(Exception e){
                                    addDefaulPost(docSnap,true,true,false);
                                }
                                if(items.size() == size){
                                    callback.onCallback(items);
                                }
                            }else if(task.isCanceled()){
                                System.out.println("Fetch failed!   640");
                            }
                        }
                    });
                    break;
            }
        }
    }

    public void getUser(final String userID,final UserCallback callback){
        // fetches the user again if it hasnt been set yet
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(!userID .equals("") && userID !=null){
            DocumentReference userRef = db.collection("Users").document(userID);
            System.out.println(userID+" "+TAG);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try {
                        Map<String, Object> docData = documentSnapshot.getData();
                        String userName = (docData.get("name") != null)      // Condition
                                ? (String) docData.get("name")              // IF-True
                                : (String) "";                              // ELSE
                        String userSurname = (docData.get("surname") != null)
                                ? (String) docData.get("surname")
                                : (String) "";
                        String userImageURL = (docData.get("profilePictureURL") != null)
                                ? (String) docData.get("profilePictureURL")
                                : (String) "";
                        String userUserUID = userID;
                        String userStatusQuote = (docData.get("statusText") != null)
                                ? (String) docData.get("statusText")
                                : (String) "";
                        List<String> userBlocked = (docData.get("blocked") != null)
                                ? (List<String>) docData.get("blocked")
                                : (List<String>) null;
                        User user = new User(userName, userSurname, userUserUID);
                        user.setImageURL(userImageURL);
                        user.setStatusQuote(userStatusQuote);
                        user.setBlocked(userBlocked);
                        callback.onCallback(user);
                    }catch(NullPointerException e){
                        System.out.println(documentSnapshot.getId()+" "+TAG);
                    }
                }
            });
        }
    }

    public void addCommunityToItemList(DocumentSnapshot docSnap){
        // creates a Community from the docSNap and adds it to the commList
        String name = docSnap.getString("name");
        String topicID = docSnap.getId();
        String imageURL = docSnap.getString("imageURL");
        String description = docSnap.getString("description");
        String displayOption = docSnap.getString("displayOption");
        Community comm = new Community(name,imageURL,topicID,description);
        comm.displayOption = displayOption;
        comm.popularity = (long) docSnap.getLong("popularity");
        items.add(comm);
    }


    public void addThoughtPost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        // creates a ThoughtPost from a docSNap and adds it to the postslist
        try{
            // Attribute die alle haben
            String thought_docID = docSnap.getId();
            String thought_title = (String) docSnap.get("title");
            String thought_description = (String) docSnap.get("description");
            String thought_report = (String) docSnap.get("report");
            String thought_originalPoster = (String) docSnap.get("originalPoster");
            long thought_thanksCount = (long) docSnap.getLong("thanksCount");
            long thought_wowCount = (long) docSnap.getLong("wowCount");
            long thought_haCount = (long) docSnap.getLong("haCount");
            long thought_niceCount = (long) docSnap.getLong("niceCount");
            String thought_type = (String)docSnap.get("type");
            Timestamp thought_createTime = (Timestamp) docSnap.getTimestamp("createTime");
            Long timeNow = new Date().getTime();
            String dateString = convertLongDateToAgoString(thought_createTime.toDate(),timeNow);
            //ThoughtPost erstellen
            System.out.println(dateString);
            ThoughtPost thoughtPost = new ThoughtPost(thought_title,thought_docID,thought_description,
                    thought_report,dateString,thought_createTime,thought_originalPoster,thought_thanksCount,
                    thought_wowCount,thought_haCount,thought_niceCount,thought_type);
            //Optinale Attribute setzen
            ArrayList<String> thought_tagsArray = (ArrayList<String>) docSnap.get("tags");
            if(thought_tagsArray!=null){
                String[] thought_tags = new String[thought_tagsArray.size()];
                thought_tags = thought_tagsArray.toArray(thought_tags);
                thoughtPost.setTags(thought_tags);
            }
            thoughtPost.setLinkedFactId((String) docSnap.get("linkedFactID"));
            thoughtPost.isTopicPost = isTopicPost;
            // Zur Liste hinzufügen
            if(fromCommunities){
                    commPostList.add(thoughtPost);
            }else{
                if(isAddonItem){
                    items.add(thoughtPost);
                }else{
                    postList.add(thoughtPost);
                }
            }
        }catch(Exception e){
            if(isAddonItem){
                addDefaulPost(docSnap,isTopicPost,true, fromCommunities);
            }else{
                addDefaulPost(docSnap,isTopicPost,false, fromCommunities);
            }
        }
    }

    public void addYouTubePost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        // creates a youTubePost from s docSnap and adds it to the postList
        try{
            // Attribute die alle haben
            String youtube_docID = docSnap.getId();
            String youtube_title = (String) docSnap.get("title");
            String youtube_description = (String) docSnap.get("description");
            String youtube_report = (String) docSnap.get("report");
            String youtube_originalPoster = (String) docSnap.get("originalPoster");
            long youtube_thanksCount = (long) docSnap.getLong("thanksCount");
            long youtube_wowCount = (long) docSnap.getLong("wowCount");
            long youtube_haCount = (long) docSnap.getLong("haCount");
            long youtube_niceCount = (long) docSnap.getLong("niceCount");
            String youtube_type = (String)docSnap.get("type");
            Timestamp youtube_createTime = (Timestamp) docSnap.getTimestamp("createTime");
            Long timeNow = new Date().getTime();
            String dateString = convertLongDateToAgoString(youtube_createTime.toDate(),timeNow);
            //YouTubePost Attribute
            String youtube_link = (String) docSnap.get("link");
            //YouTubePost erstellen
            YouTubePost youTubePost = new YouTubePost(youtube_title,youtube_docID,youtube_description,
                    youtube_report, dateString,youtube_createTime,youtube_originalPoster,youtube_thanksCount,
                    youtube_wowCount,youtube_haCount, youtube_niceCount,youtube_type,youtube_link);
            //Optionale Attribute setzen
            ArrayList<String> youtube_tagsArray = (ArrayList<String>) docSnap.get("tags");
            if(youtube_tagsArray!=null){
                String[] youtube_tags = new String[youtube_tagsArray.size()];
                youtube_tags = youtube_tagsArray.toArray(youtube_tags);
                youTubePost.setTags(youtube_tags);
            }
            youTubePost.setLinkedFactId((String) docSnap.get("linkedFactID"));
            youTubePost.isTopicPost = isTopicPost;
            // Zur Liste hinzufügen
            if(fromCommunities){
                commPostList.add(youTubePost);
            }else{
                if(isAddonItem){
                    items.add(youTubePost);
                }else{
                    postList.add(youTubePost);
                }
            }
        }catch(Exception e){
            if(isAddonItem){
                addDefaulPost(docSnap,isTopicPost,true, fromCommunities);
            }else{
                addDefaulPost(docSnap,isTopicPost,false, fromCommunities);
            }
        }
    }

    public void addLinkPost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        // creates a LinkPost from a docSNap and adds it to the postList
        try{
            // Attribute die alle haben
            String link_docID = docSnap.getId();
            String link_title = (String) docSnap.get("title");
            String link_description = (String) docSnap.get("description");
            String link_report = (String) docSnap.get("report");
            String link_originalPoster = (String) docSnap.get("originalPoster");
            long link_thanksCount = (long) docSnap.getLong("thanksCount");
            long link_wowCount = (long) docSnap.getLong("wowCount");
            long link_haCount = (long) docSnap.getLong("haCount");
            long link_niceCount = (long) docSnap.getLong("niceCount");
            String link_type = (String)docSnap.get("type");
            Timestamp link_createTime = (Timestamp) docSnap.getTimestamp("createTime");
            Long timeNow = new Date().getTime();
            String dateString = convertLongDateToAgoString(link_createTime.toDate(),timeNow);
            //LinkPost Attribute
            String link_link = (String) docSnap.get("link");
            //LinkPost erstellen
            LinkPost linkPost = new LinkPost(link_title,link_docID,link_description,
                    link_report,dateString,link_createTime,link_originalPoster,link_thanksCount,
                    link_wowCount,link_haCount,link_niceCount,link_type,link_link);
            //Optinale Attribute setzen
            ArrayList<String> link_tagsArray = (ArrayList<String>) docSnap.get("tags");
            if(link_tagsArray !=null){
                String[] link_tags = new String[link_tagsArray.size()];
                link_tags = link_tagsArray.toArray(link_tags);
                linkPost.setTags(link_tags);
            }
            linkPost.setLinkedFactId((String)docSnap.get("linkedFactID"));
            linkPost.isTopicPost = isTopicPost;
            //Zur Liste hinzufügen
            if(fromCommunities){
                commPostList.add(linkPost);
            }else{
                if(isAddonItem){
                    items.add(linkPost);
                }else{
                    postList.add(linkPost);
                }
            }
        } catch (Exception e){
            if(isAddonItem){
                addDefaulPost(docSnap,isTopicPost,true, fromCommunities);
            }else{
                addDefaulPost(docSnap,isTopicPost,false, fromCommunities);
            }
        }
    }

    public void addGIFPost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        // creates a GIFPost from a docSnap and adds it to the post list
        try{
            // Attribute die alle haben
            String gif_docID = docSnap.getId();
            String gif_title = (String) docSnap.get("title");
            String gif_description = (String) docSnap.get("description");
            String gif_report = (String) docSnap.get("report");
            String gif_originalPoster = (String) docSnap.get("originalPoster");
            long gif_thanksCount = (long) docSnap.getLong("thanksCount");
            long gif_wowCount = (long) docSnap.getLong("wowCount");
            long gif_haCount = (long) docSnap.getLong("haCount");
            long gif_niceCount = (long) docSnap.getLong("niceCount");
            String gif_type = (String)docSnap.get("type");
            Timestamp gif_createTime = (Timestamp) docSnap.getTimestamp("createTime");
            Long timeNow = new Date().getTime();
            String dateString = convertLongDateToAgoString(gif_createTime.toDate(),timeNow);
            //GIFPost Attribute
            String gif_link = (String) docSnap.get("link");
            //GIFPost erstellen
            GIFPost GIFPost = new GIFPost(gif_title,gif_docID,gif_description,
                    gif_report,dateString,gif_createTime,gif_originalPoster,gif_thanksCount,
                    gif_wowCount,gif_haCount,gif_niceCount,gif_type,gif_link);
            //Optionale Attribute setzen
            ArrayList<String> gif_tagsArray = (ArrayList<String>) docSnap.get("tags");
            if(gif_tagsArray!=null){
                String[] gif_tags = new String[gif_tagsArray.size()];
                gif_tags = gif_tagsArray.toArray(gif_tags);
                GIFPost.setTags(gif_tags);
            }
            GIFPost.setLinkedFactId((String)docSnap.get("linkedFactID"));
            GIFPost.isTopicPost = isTopicPost;
            //Zur Liste hinzufügen
            if(fromCommunities){
                commPostList.add(GIFPost);
            }else{
                if(isAddonItem){
                    items.add(GIFPost);
                }else{
                    postList.add(GIFPost);
                }
            }
        }catch (Exception e){
            if(isAddonItem){
                addDefaulPost(docSnap,isTopicPost,true, fromCommunities);
            }else{
                addDefaulPost(docSnap,isTopicPost,false, fromCommunities);
            }
        }
    }

    public void addPicturePost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        // creates a PicturePost from a docSnap and adds it to the postList
        try{
            // Attribute die alle haben
            String picture_docID = docSnap.getId();
            String picture_title = (String) docSnap.get("title");
            String picture_description = (String) docSnap.get("description");
            String picture_report = (String) docSnap.get("report");
            String picture_originalPoster = (String) docSnap.get("originalPoster");
            System.out.println(picture_docID);
            long picture_thanksCount = (long) docSnap.getLong("thanksCount");
            long picture_wowCount = (long) docSnap.getLong("wowCount");
            long picture_haCount = (long) docSnap.getLong("haCount");
            long picture_niceCount = (long) docSnap.getLong("niceCount");
            String picture_type = (String)docSnap.get("type");
            String picture_link = (String) docSnap.get("link");
            Timestamp picture_createTime = (Timestamp) docSnap.getTimestamp("createTime");
            Long timeNow = new Date().getTime();
            String dateString = convertLongDateToAgoString(picture_createTime.toDate(),timeNow);
            // PicturePost Attribute
            long picture_imageHeight = (long) docSnap.getLong("imageHeight");
            long picture_imageWidth = (long) docSnap.getLong("imageWidth");
            String picture_imageURL = (String) docSnap.get("imageURL");
            //PicturePost erstellen
            PicturePost picturePost = new PicturePost(picture_title,picture_docID,picture_description,
                    picture_report,dateString,picture_createTime,picture_originalPoster,picture_thanksCount,
                    picture_wowCount,picture_haCount,picture_niceCount,picture_type,picture_imageHeight,
                    picture_imageWidth,picture_imageURL);
            //Optinale Attribute setzen
            ArrayList<String> picture_tagsArray = (ArrayList<String>) docSnap.get("tags");
            if(picture_tagsArray!=null){
                String[] picture_tags = new String[picture_tagsArray.size()];
                picture_tags = picture_tagsArray.toArray(picture_tags);
                picturePost.setTags(picture_tags);
            }
            picturePost.setLinkedFactId((String)docSnap.get("linkedFactID"));
            picturePost.isTopicPost = isTopicPost;
            // Zur Liste hinzufügen
            if(fromCommunities){
                commPostList.add(picturePost);
            }else{
                if(isAddonItem){
                    items.add(picturePost);
                }else{
                    postList.add(picturePost);
                }
            }
        }catch (Exception e){
            if(isAddonItem){
                addDefaulPost(docSnap,isTopicPost,true, fromCommunities);
            }else{
                addDefaulPost(docSnap,isTopicPost,false, fromCommunities);
            }
        }
    }
    public void addMultiPicturePost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        // creates a muliPicturePost from a docSnap and adds it to the postList
        try{
            // Attribute die alle haben
            String mpicture_docID = docSnap.getId();
            String mpicture_title = (String) docSnap.get("title");
            String mpicture_description = (String) docSnap.get("description");
            String mpicture_report = (String) docSnap.get("report");
            String mpicture_originalPoster = (String) docSnap.get("originalPoster");
            long mpicture_thanksCount = (long) docSnap.getLong("thanksCount");
            long mpicture_wowCount = (long) docSnap.getLong("wowCount");
            long mpicture_haCount = (long) docSnap.getLong("haCount");
            long mpicture_niceCount = (long) docSnap.getLong("niceCount");
            String mpicture_type = (String)docSnap.get("type");
            String mpicture_link = (String) docSnap.get("link");
            Timestamp mpicture_createTime = (Timestamp) docSnap.getTimestamp("createTime");
            Long timeNow = new Date().getTime();
            String dateString = convertLongDateToAgoString(mpicture_createTime.toDate(),timeNow);
            // MultiPicturePost Attribute
            long mpicture_imageHeight = (long) docSnap.getLong("imageHeight");
            long mpicture_imageWidth = (long) docSnap.getLong("imageWidth");
            String mpicture_imageURL = (String) docSnap.get("imageURL");
            ArrayList<String> mpicture_imageURLS_array = (ArrayList<String>) docSnap.get("imageURLs");
            String[] mpicture_imageURLS = new String[mpicture_imageURLS_array.size()];
            mpicture_imageURLS = mpicture_imageURLS_array.toArray(mpicture_imageURLS);
            // MultiPicturePost erstellen
            MultiPicturePost mPicturePost = new MultiPicturePost(mpicture_title,mpicture_docID,mpicture_description,
                    mpicture_report,dateString,mpicture_createTime,mpicture_originalPoster,mpicture_thanksCount,
                    mpicture_wowCount,mpicture_haCount,mpicture_niceCount,mpicture_type,
                    mpicture_imageHeight,mpicture_imageWidth,mpicture_imageURL,mpicture_imageURLS);
            //Optionale Attribute setzen
            ArrayList<String> mpicture_tagsArray = (ArrayList<String>) docSnap.get("tags");
            if(mpicture_tagsArray!=null){
                String[] mpicture_tags = new String[mpicture_tagsArray.size()];
                mpicture_tags = mpicture_tagsArray.toArray(mpicture_tags);
                mPicturePost.setTags(mpicture_tags);
            }
            mPicturePost.setLinkedFactId((String)docSnap.get("linkedFactID"));
            mPicturePost.isTopicPost = isTopicPost;
            //Zur Liste hinzufügen
            if(fromCommunities){
                commPostList.add(mPicturePost);
            }else{
                if(isAddonItem){
                    items.add(mPicturePost);
                }else{
                    postList.add(mPicturePost);
                }
            }
        }catch(Exception e){
            if(isAddonItem){
                addDefaulPost(docSnap,isTopicPost,true, fromCommunities);
            }else{
                addDefaulPost(docSnap,isTopicPost,false, fromCommunities);
            }
        }
    }
    public void addTranslationPost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        //creates a translationpost from a docSnap and adds it to the postList
        try{
            // Attribute die alle haben
            String translation_docID = docSnap.getId();
            String translation_title = (String) docSnap.get("title");
            String translation_description = (String) docSnap.get("description");
            String translation_report = (String) docSnap.get("report");
            String translation_originalPoster = (String) docSnap.get("originalPoster");
            long translation_thanksCount = (long) docSnap.getLong("thanksCount");
            long translation_wowCount = (long) docSnap.getLong("wowCount");
            long translation_haCount = (long) docSnap.getLong("haCount");
            long translation_niceCount = (long) docSnap.getLong("niceCount");
            String translation_type = (String)docSnap.get("type");
            Timestamp translation_createTime = (Timestamp) docSnap.getTimestamp("createTime");
            Long timeNow = new Date().getTime();
            String dateString = convertLongDateToAgoString(translation_createTime.toDate(),timeNow);
            //TranslationPost Attribute
            String translation_OGpostDocumentID = (String) docSnap.get("OGpostDocumentID");
            //TranslationPost erstellen
            TranslationPost translationPost = new TranslationPost(translation_title,translation_docID,translation_description,
                    translation_report,dateString,translation_createTime,translation_originalPoster,translation_thanksCount,
                    translation_wowCount,translation_haCount,translation_niceCount,translation_type,
                    translation_OGpostDocumentID);
            // Optionale Attribute setzen!
            ArrayList<String> translation_tagsArray = (ArrayList<String>) docSnap.get("tags");
            if(translation_tagsArray!=null){
                String[] mpicture_tags = new String[translation_tagsArray.size()];
                mpicture_tags = translation_tagsArray.toArray(mpicture_tags);
                translationPost.setTags(mpicture_tags);
            }
            translationPost.setLinkedFactId((String)docSnap.get("linkedFactID"));
            if(fromCommunities){
                commPostList.add(translationPost);
            }else{
                if(isAddonItem){
                    items.add(translationPost);
                }else{
                    postList.add(translationPost);
                }
            }
            translationPost.isTopicPost = isTopicPost;
        } catch(Exception e){
            if(isAddonItem){
                addDefaulPost(docSnap,isTopicPost,true, fromCommunities);
            }else{
                addDefaulPost(docSnap,isTopicPost,false, fromCommunities);
            }
        }
    }

    public void addRepostPost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        // create a Repostpost from a docSnap and adds it to the postList
        try{
            // Attribute die alle haben
            String repost_docID = docSnap.getId();
            String repost_title = (String) docSnap.get("title");
            String repost_description = (String) docSnap.get("description");
            String repost_report = (String) docSnap.get("report");
            String repost_originalPoster = (String) docSnap.get("originalPoster");
            long repost_thanksCount = (long) docSnap.getLong("thanksCount");
            long repost_wowCount = (long) docSnap.getLong("wowCount");
            long repost_haCount = (long) docSnap.getLong("haCount");
            long repost_niceCount = (long) docSnap.getLong("niceCount");
            String repost_type = (String)docSnap.get("type");
            Timestamp repost_createTime = (Timestamp) docSnap.getTimestamp("createTime");
            Long timeNow = new Date().getTime();
            String dateString = convertLongDateToAgoString(repost_createTime.toDate(),timeNow);
            //RepostPost Attribute
            String repost_OGpostDocumentID = (String) docSnap.get("OGpostDocumentID");
            //RepostPost erstellen
            RepostPost repostPost = new RepostPost(repost_title,repost_docID,repost_description,
                    repost_report,dateString,repost_createTime,repost_originalPoster,repost_thanksCount,
                    repost_wowCount,repost_haCount,repost_niceCount,repost_type,
                    repost_OGpostDocumentID);
            if(fromCommunities){
                commPostList.add(repostPost);
            }else{
                if(isAddonItem){
                    items.add(repostPost);
                }else{
                    postList.add(repostPost);
                }
            }
            // Optinale Attribute setzen!
            ArrayList<String> repost_tagsArray = (ArrayList<String>) docSnap.get("tags");
            if(repost_tagsArray!=null){
                String[] repost_tags = new String[repost_tagsArray.size()];
                repost_tags = repost_tagsArray.toArray(repost_tags);
                repostPost.setTags(repost_tags);
            }
            repostPost.setLinkedFactId((String)docSnap.get("linkedFactID"));
            repostPost.isTopicPost = isTopicPost;
        } catch(Exception e){
            if(isAddonItem){
                addDefaulPost(docSnap,isTopicPost,true, fromCommunities);
            }else{
                addDefaulPost(docSnap,isTopicPost,false, fromCommunities);
            }
        }
    }

    public void addDefaulPost(DocumentSnapshot docSnap,boolean isTopicPost,boolean isAddonItem, boolean fromCommunities){
        // Create a DefaultPost from a docSnap when the type of the post isnt known
        // Or some required files are missing within the post and adds it to the postList
        // Attribute die alle haben
        String default_docID = docSnap.getId();
        String default_title = "Default";
        String default_description = "Default";
        String default_report = "Default";
        String default_originalPoster = "Default";
        long default_thanksCount = 0L;
        long default_wowCount = 0L;
        long default_haCount = 0L;
        long default_niceCount = 0L;
        String default_type = "Default";
        Timestamp default_createTime = new Timestamp(new Date());
        Long timeNow = new Date().getTime();
        String dateString = convertLongDateToAgoString(default_createTime.toDate(),timeNow);
        //ThoughtPost erstellen
        DefaultPost defaultPost = new DefaultPost(default_title,default_docID,default_description,
                default_report,dateString,default_createTime,default_originalPoster,default_thanksCount,
                default_wowCount,default_haCount,default_niceCount,default_type);
        // Zur Liste hinzufügen
        if(fromCommunities){
            commPostList.add(defaultPost);
        }else{
            if(isAddonItem){
                items.add(defaultPost);
            }else{
                postList.add(defaultPost);
            }
        }
    }

    public void removePost(Post post){
        DocumentReference postRef;
        if(post.isTopicPost){
            postRef = db.collection("TopicPosts").document(post.documentID);
        }else{
            postRef = db.collection("Posts").document(post.documentID);
        }
        postRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("!");
                }
            }
        });
        if(post.linkedFactId == null || post.linkedFactId.equals("")){
            postRef = db.collection("Posts").document(post.documentID);
            postRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("Löschen erfolgreich!");
                }
            });
        }else{
            DocumentReference commRef = db.collection("Facts").document(post.linkedFactId)
                    .collection("posts").document(post.documentID);
            commRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("Löschen erfolgreich!");
                }
            });
        }


        if(post instanceof PicturePost){

                String pathString = post.documentID+".png";
                StorageReference pictureRef = storage.child("postPictures").child(pathString);
                pictureRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("Löschen erfolgreich!");
                    }
                });

            }
        if (post instanceof MultiPicturePost){

                int index = 0;
                for(String imageURL: ((MultiPicturePost) post).imageURLs){
                    String pathString = post.documentID+"-"+index+".png";
                    StorageReference pictureRef = storage.child("postPictures").child(pathString);
                    pictureRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            System.out.println("Löschen erfolgreich!");
                        }
                    });
                }
            }

    }


    public static String convertLongDateToAgoString (Date createdDate, Long timeNow){
        // converts the date to a string
        Long createdDateLong = createdDate.getTime();
        Long timeElapsed = timeNow - createdDateLong;
        // For logging in Android for testing purposes
        /*
        Date dateCreatedFriendly = new Date(createdDate);
        Log.d("MicroR", "dateCreatedFriendly: " + dateCreatedFriendly.toString());
        Log.d("MicroR", "timeNow: " + timeNow.toString());
        Log.d("MicroR", "timeElapsed: " + timeElapsed.toString());*/

        // Lengths of respective time durations in Long format.
        Long oneMin = 60000L;
        Long oneHour = 3600000L;
        Long oneDay = 86400000L;
        Long oneWeek = 604800000L;
        String finalString = "0sec";
        String unit;
        if (timeElapsed < oneMin){
            // Convert milliseconds to seconds.
            double seconds = (double) ((timeElapsed / 1000));
            // Round up
            seconds = Math.round(seconds);
            // Generate the friendly unit of the ago time
            if (seconds == 1) {
                unit = "Sekunde";
            } else {
                unit = "Sekunden";
            }
            finalString = String.format("Vor %.0f ", seconds) + unit;
        } else if (timeElapsed < oneHour) {
            double minutes = (double) ((timeElapsed / 1000) / 60);
            minutes = Math.round(minutes);
            if (minutes == 1) {
                unit = "Minute";
            } else {
                unit = "Minuten";
            }
            finalString = String.format("Vor %.0f ", minutes) + unit;
        } else if (timeElapsed < oneDay) {
            double hours   = (double) ((timeElapsed / 1000) / 60 / 60);
            hours = Math.round(hours);
            if (hours == 1) {
                unit = "Stunde";
            } else {
                unit = "Stunden";
            }
            finalString = String.format("Vor %.0f ", hours) + unit;
        } else if (timeElapsed < oneWeek) {
            double days   = (double) ((timeElapsed / 1000) / 60 / 60 / 24);
            days = Math.round(days);
            if (days == 1) {
                finalString = "Gestern";
            } else {
                finalString = String.format("Vor %.0f ", days) + "Tagen";
            }

        } else if (timeElapsed > oneWeek) {
            double weeks = (double) ((timeElapsed / 1000) / 60 / 60 / 24 / 7);
            weeks = Math.round(weeks);
            if (weeks == 1) {
                unit = "Woche";
            } else {
                unit = "Wochen";
            }
            finalString = String.format("Vor %.0f ", weeks) + unit;
        }
        return finalString;
    }
}

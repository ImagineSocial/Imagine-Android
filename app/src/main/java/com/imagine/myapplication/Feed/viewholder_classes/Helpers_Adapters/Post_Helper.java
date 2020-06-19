package com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.imagine.myapplication.FirebaseCallback;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterInputStream;

public class Post_Helper {

    private static final String TAG = "Post_Helper";
    public DocumentSnapshot lastSnap;
    public ArrayList<Post> postList = new ArrayList<Post>();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String commID;

    public  void getPostsForMainFeed( final FirebaseCallback callback){
        Query postsRef = db.collection("Posts").orderBy("createTime",Query.Direction.DESCENDING).limit(20);
        postsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    for(QueryDocumentSnapshot docSnap : queryDocumentSnapshots){
                        System.out.println("aa");
                        switch((String)docSnap.get("type")){
                            case "thought":
                                addThoughtPost(docSnap);
                                break;
                            case "youTubeVideo":
                                addYouTubePost(docSnap);
                                break;
                            case "link":
                                addLinkPost(docSnap);
                                break;
                            case "GIF":
                                addGIFPost(docSnap);
                                break;
                            case "picture":
                                addPicturePost(docSnap);
                                break;
                            case "multiPicture":
                                addMultiPicturePost(docSnap);
                                break;
                            case "translation":
                                addTranslationPost(docSnap);
                                break;
                            case "repost":
                                addRepostPost(docSnap);
                                break;
                            default:
                                addDefaulPost(docSnap);
                                break;
                        }
                    }
                    Log.d(TAG,"From Post_Helper"+postList.toString());
                    callback.onCallback(postList);
                }
            }
        });
    }
    public void getMorePostsForFeed(final FirebaseCallback callback){
        Query postsRef = db.collection("Posts").orderBy("createTime",Query.Direction.DESCENDING).startAfter(lastSnap).limit(20);
        postsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    System.out.println(queryDocumentSnapshots.size());
                    for(QueryDocumentSnapshot docSnap : queryDocumentSnapshots){
                        System.out.println("aa");
                        switch((String)docSnap.get("type")){
                            case "thought":
                                addThoughtPost(docSnap);
                                break;
                            case "youTubeVideo":
                                addYouTubePost(docSnap);
                                break;
                            case "link":
                                addLinkPost(docSnap);
                                break;
                            case "GIF":
                                addGIFPost(docSnap);
                                break;
                            case "picture":
                                addPicturePost(docSnap);
                                break;
                            case "multiPicture":
                                addMultiPicturePost(docSnap);
                                break;
                            case "translation":
                                addTranslationPost(docSnap);
                                break;
                            case "repost":
                                addRepostPost(docSnap);
                                break;
                            default:
                                addDefaulPost(docSnap);
                                break;
                        }
                    }
                    callback.onCallback(postList);
                    System.out.println("FERTIG");
                }
            }
        });
    }

    public  void getPostsForCommunityFeed(String commID,final FirebaseCallback callback){
        this.commID = commID;
        Query postsColl = db.collection("Facts").document(commID).collection("posts").limit(20);
        postsColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    String postID = queryDocumentSnapshot.getId();
                    String type = queryDocumentSnapshot.getString("type") == null ?
                            "feedPost"
                            : queryDocumentSnapshot.getString("type") ;

                    DocumentReference docRef = type.equals("topicPost") ?
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
                                    return;
                                }
                                System.out.println(id+"   "+type);
                                switch(documentSnapshot.getString("type")){
                                    case "thought":
                                        addThoughtPost(documentSnapshot);
                                        break;
                                    case "youTubeVideo":
                                        addYouTubePost(documentSnapshot);
                                        break;
                                    case "link":
                                        addLinkPost(documentSnapshot);
                                        break;
                                    case "GIF":
                                        addGIFPost(documentSnapshot);
                                        break;
                                    case "picture":
                                        addPicturePost(documentSnapshot);
                                        break;
                                    case "multiPicture":
                                        addMultiPicturePost(documentSnapshot);
                                        break;
                                    case "translation":
                                        addTranslationPost(documentSnapshot);
                                        break;
                                    case "repost":
                                        addRepostPost(documentSnapshot);
                                        break;
                                    default:
                                        addDefaulPost(documentSnapshot);
                                        break;
                                }

                                    callback.onCallback(postList);


                            }else if(task.isCanceled()){

                            }
                        }
                    });
                }



            }
        });
    }

    public void getMorePostsForCommunityFeed(final FirebaseCallback callback){
        if(lastSnap == null){
            return;
        }
        Query postsRef = db.collection("Posts");
        Task<QuerySnapshot> task1 = postsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    lastSnap = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    System.out.println(queryDocumentSnapshots.size());
                    for(QueryDocumentSnapshot docSnap : queryDocumentSnapshots){
                        System.out.println("aa");
                        switch((String)docSnap.get("type")){
                            case "thought":
                                addThoughtPost(docSnap);
                                break;
                            case "youTubeVideo":
                                addYouTubePost(docSnap);
                                break;
                            case "link":
                                addLinkPost(docSnap);
                                break;
                            case "GIF":
                                addGIFPost(docSnap);
                                break;
                            case "picture":
                                addPicturePost(docSnap);
                                break;
                            case "multiPicture":
                                addMultiPicturePost(docSnap);
                                break;
                            case "translation":
                                addTranslationPost(docSnap);
                                break;
                            case "repost":
                                addRepostPost(docSnap);
                                break;
                            default:
                                addDefaulPost(docSnap);
                                break;
                        }
                    }
                    callback.onCallback(postList);
                    System.out.println("FERTIG");
                }
            }
        });
    }

    public void addThoughtPost(DocumentSnapshot docSnap){
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
        //ThoughtPost erstellen
        ThoughtPost thoughtPost = new ThoughtPost(thought_title,thought_docID,thought_description,
                thought_report,thought_createTime,thought_originalPoster,thought_thanksCount,
                thought_wowCount,thought_haCount,thought_niceCount,thought_type);
        //Optinale Attribute setzen
        ArrayList<String> thought_tagsArray = (ArrayList<String>) docSnap.get("tags");
        if(thought_tagsArray!=null){
            String[] thought_tags = new String[thought_tagsArray.size()];
            thought_tags = thought_tagsArray.toArray(thought_tags);
            thoughtPost.setTags(thought_tags);
        }
        thoughtPost.setLinkedFactId((String) docSnap.get("linkedFactID"));
        // Zur Liste hinzufügen
        postList.add(thoughtPost);

    }
    public void addYouTubePost(DocumentSnapshot docSnap){
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
        //YouTubePost Attribute
        String youtube_link = (String) docSnap.get("link");
        //YouTubePost erstellen
        YouTubePost youTubePost = new YouTubePost(youtube_title,youtube_docID,youtube_description,
                youtube_report, youtube_createTime,youtube_originalPoster,youtube_thanksCount,
                youtube_wowCount,youtube_haCount, youtube_niceCount,youtube_type,youtube_link);
        //Optionale Attribute setzen
        ArrayList<String> youtube_tagsArray = (ArrayList<String>) docSnap.get("tags");
        if(youtube_tagsArray!=null){
            String[] youtube_tags = new String[youtube_tagsArray.size()];
            youtube_tags = youtube_tagsArray.toArray(youtube_tags);
            youTubePost.setTags(youtube_tags);
        }
        youTubePost.setLinkedFactId((String) docSnap.get("linkedFactID"));
        // Zur Liste hinzufügen
        postList.add(youTubePost);
    }
    public void addLinkPost(DocumentSnapshot docSnap){
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
        //LinkPost Attribute
        String link_link = (String) docSnap.get("link");
        //LinkPost erstellen
        LinkPost linkPost = new LinkPost(link_title,link_docID,link_description,
                link_report,link_createTime,link_originalPoster,link_thanksCount,
                link_wowCount,link_haCount,link_niceCount,link_type,link_link);
        //Optinale Attribute setzen
        ArrayList<String> link_tagsArray = (ArrayList<String>) docSnap.get("tags");
        if(link_tagsArray !=null){
            String[] link_tags = new String[link_tagsArray.size()];
            link_tags = link_tagsArray.toArray(link_tags);
            linkPost.setTags(link_tags);
        }
        linkPost.setLinkedFactId((String)docSnap.get("linkedFactID"));
        //Zur Liste hinzufügen
        postList.add(linkPost);
    }
    public void addGIFPost(DocumentSnapshot docSnap){
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
        //GIFPost Attribute
        String gif_link = (String) docSnap.get("link");
        //GIFPost erstellen
        GIFPost GIFPost = new GIFPost(gif_title,gif_docID,gif_description,
                gif_report,gif_createTime,gif_originalPoster,gif_thanksCount,
                gif_wowCount,gif_haCount,gif_niceCount,gif_type,gif_link);
        //Optionale Attribute setzen
        ArrayList<String> gif_tagsArray = (ArrayList<String>) docSnap.get("tags");
        if(gif_tagsArray!=null){
            String[] gif_tags = new String[gif_tagsArray.size()];
            gif_tags = gif_tagsArray.toArray(gif_tags);
            GIFPost.setTags(gif_tags);
        }
        GIFPost.setLinkedFactId((String)docSnap.get("linkedFactID"));
        //Zur Liste hinzufügen
        postList.add(GIFPost);
    }
    public void addPicturePost(DocumentSnapshot docSnap){
        //Map<String,Object> dataMap = docSnap.getData();
        //String type = (String) dataMap.get("type");
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
        // PicturePost Attribute
        long picture_imageHeight = (long) docSnap.getLong("imageHeight");
        long picture_imageWidth = (long) docSnap.getLong("imageWidth");
        String picture_imageURL = (String) docSnap.get("imageURL");
        //PicturePost erstellen
        PicturePost picturePost = new PicturePost(picture_title,picture_docID,picture_description,
                picture_report,picture_createTime,picture_originalPoster,picture_thanksCount,
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
        // Zur Liste hinzufügen
        postList.add(picturePost);
    }
    public void addMultiPicturePost(DocumentSnapshot docSnap){
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
        // MultiPicturePost Attribute
        long mpicture_imageHeight = (long) docSnap.getLong("imageHeight");
        long mpicture_imageWidth = (long) docSnap.getLong("imageWidth");
        String mpicture_imageURL = (String) docSnap.get("imageURL");
        ArrayList<String> mpicture_imageURLS_array = (ArrayList<String>) docSnap.get("imageURLs");
        String[] mpicture_imageURLS = new String[mpicture_imageURLS_array.size()];
        mpicture_imageURLS = mpicture_imageURLS_array.toArray(mpicture_imageURLS);
        // MultiPicturePost erstellen
        MultiPicturePost mPicturePost = new MultiPicturePost(mpicture_title,mpicture_docID,mpicture_description,
                mpicture_report,mpicture_createTime,mpicture_originalPoster,mpicture_thanksCount,
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
        //Zur Liste hinzufügen
        postList.add(mPicturePost);

    }
    public void addTranslationPost(DocumentSnapshot docSnap){
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
        //TranslationPost Attribute
        String translation_OGpostDocumentID = (String) docSnap.get("OGpostDocumentID");
        //TranslationPost erstellen
        TranslationPost translationPost = new TranslationPost(translation_title,translation_docID,translation_description,
                translation_report,translation_createTime,translation_originalPoster,translation_thanksCount,
                translation_wowCount,translation_haCount,translation_niceCount,translation_type,
                translation_OGpostDocumentID);
        postList.add(translationPost);
        // Optionale Attribute setzen!
        ArrayList<String> translation_tagsArray = (ArrayList<String>) docSnap.get("tags");
        if(translation_tagsArray!=null){
            String[] mpicture_tags = new String[translation_tagsArray.size()];
            mpicture_tags = translation_tagsArray.toArray(mpicture_tags);
            translationPost.setTags(mpicture_tags);
        }
        translationPost.setLinkedFactId((String)docSnap.get("linkedFactID"));
    }
    public void addRepostPost(DocumentSnapshot docSnap){
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
        //RepostPost Attribute
        String repost_OGpostDocumentID = (String) docSnap.get("OGpostDocumentID");
        //RepostPost erstellen
        RepostPost repostPost = new RepostPost(repost_title,repost_docID,repost_description,
                repost_report,repost_createTime,repost_originalPoster,repost_thanksCount,
                repost_wowCount,repost_haCount,repost_niceCount,repost_type,
                repost_OGpostDocumentID);
        postList.add(repostPost);
        // Optinale Attribute setzen!
        ArrayList<String> repost_tagsArray = (ArrayList<String>) docSnap.get("tags");
        if(repost_tagsArray!=null){
            String[] repost_tags = new String[repost_tagsArray.size()];
            repost_tags = repost_tagsArray.toArray(repost_tags);
            repostPost.setTags(repost_tags);
        }
        repostPost.setLinkedFactId((String)docSnap.get("linkedFactID"));
    }
    public void addDefaulPost(DocumentSnapshot docSnap){
        // Attribute die alle haben
        String default_docID = docSnap.getId();
        String default_title = (String) docSnap.get("title");
        String default_description = (String) docSnap.get("description");
        String default_report = (String) docSnap.get("report");
        String default_originalPoster = (String) docSnap.get("originalPoster");
        long default_thanksCount = (long) docSnap.getLong("thanksCount");
        long default_wowCount = (long) docSnap.getLong("wowCount");
        long default_haCount = (long) docSnap.getLong("haCount");
        long default_niceCount = (long) docSnap.getLong("niceCount");
        String default_type = (String)docSnap.get("type");
        Timestamp default_createTime = (Timestamp) docSnap.getTimestamp("createTime");
        //ThoughtPost erstellen
        DefaultPost defaultPost = new DefaultPost(default_title,default_docID,default_description,
                default_report,default_createTime,default_originalPoster,default_thanksCount,
                default_wowCount,default_haCount,default_niceCount,default_type);
        //Optinale Attribute setzen
        ArrayList<String> default_tagsArray = (ArrayList<String>) docSnap.get("tags");
        if(default_tagsArray!=null){
            String[] default_tags = new String[default_tagsArray.size()];
            default_tags = default_tagsArray.toArray(default_tags);
            defaultPost.setTags(default_tags);
        }
        defaultPost.setLinkedFactId((String) docSnap.get("linkedFactID"));
        // Zur Liste hinzufügen
        postList.add(defaultPost);
    }
}

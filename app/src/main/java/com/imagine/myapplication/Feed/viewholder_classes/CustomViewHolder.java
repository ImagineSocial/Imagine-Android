package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.Community.Community_Activity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.VoteHelper;
import com.imagine.myapplication.post_classes.Post;

import java.util.List;
import java.util.Map;


public abstract class CustomViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "CustomViewHolder";
    public View mItemView;
    public Context mContext;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mItemView = itemView;
        mContext = itemView.getContext();
    }

    public void init(final Post post){
        // sets up the onClickListeners for the buttons
        final VoteHelper vote = new VoteHelper();
        ImageButton thanksButton = mItemView.findViewById(R.id.thanks_button);
        ImageButton wowButton = mItemView.findViewById(R.id.wow_button);
        ImageButton haButton = mItemView.findViewById(R.id.ha_button);
        ImageButton niceButton = mItemView.findViewById(R.id.nice_button);
        thanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote.updateVotes("thanks",post);
                upDateButtonUI("thanks",post);
            }
        });
        wowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote.updateVotes("wow",post);
                upDateButtonUI("wow",post);
            }
        });
        haButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote.updateVotes("ha",post);
                upDateButtonUI("ha",post);
            }
        });
        niceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote.updateVotes("nice",post);
                upDateButtonUI("nice",post);
            }
        });
    }

    public void upDateButtonUI(String type, Post post){
        // changes the buttonUI when a button is clicked
        switch(type){
            case "thanks":
                ImageButton thanksBUtton = mItemView.findViewById(R.id.thanks_button);
                TextView thanksCounttv = mItemView.findViewById(R.id.thanks_count_textView);
                thanksBUtton.setEnabled(false);
                thanksBUtton.setVisibility(View.INVISIBLE);
                thanksCounttv.setText(String.valueOf(post.thanksCount++));
                break;
            case "wow":
                ImageButton wowButton = mItemView.findViewById(R.id.wow_button);
                TextView wowCounttv = mItemView.findViewById(R.id.wow_count_textView);
                wowButton.setEnabled(false);
                wowButton.setVisibility(View.INVISIBLE);
                wowCounttv.setText(String.valueOf(post.wowCount++));
                break;
            case "ha":
                ImageButton haButton = mItemView.findViewById(R.id.ha_button);
                TextView haCounttv = mItemView.findViewById(R.id.ha_count_textView);
                haButton.setEnabled(false);
                haButton.setVisibility(View.INVISIBLE);
                haCounttv.setText(String.valueOf(post.haCount++));
                break;
            case "nice":
                ImageButton niceButton = mItemView.findViewById(R.id.nice_button);
                TextView niceCounttv = mItemView.findViewById(R.id.nice_count_textView);
                niceButton.setEnabled(false);
                niceButton.setVisibility(View.INVISIBLE);
                niceCounttv.setText(String.valueOf(post.niceCount++));
                break;
            default:
                Log.d(TAG,"Invalid type String!");
                break;
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

    public void setLinkedFact(final String linkedTopicID){
        // sets the linkedfactID and fetches communitydata to display
        final ImageView linkedTopicImageView = itemView.findViewById(R.id.topicImageView);
        if (linkedTopicID != "" && linkedTopicID != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Facts").document(linkedTopicID);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try {
                        Map<String, Object> docData = documentSnapshot.getData();
                        final String communityName = (docData.get("name") != null)      // Condition
                                ? (String) docData.get("name")              // IF-True
                                : (String) "";                              // ELSE
                        final String communityImageURL = (docData.get("imageURL") != null)
                                ? (String) docData.get("imageURL")
                                : (String) "";
                        final String communityID = linkedTopicID;
                        final String description = (docData.get("description") != null)
                                ? (String) docData.get("description")
                                : (String) "";
                        if (communityImageURL != "") {
                            Glide.with(itemView).load(communityImageURL).into(linkedTopicImageView);
                        } else {
                            Glide.with(itemView).load(R.drawable.fact_stamp).into(linkedTopicImageView);
                        }
                        linkedTopicImageView.setClipToOutline(true);

                        linkedTopicImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, Community_Activity.class);
                                intent.putExtra("name", communityName);
                                intent.putExtra("description",description);
                                intent.putExtra("imageURL", communityImageURL);
                                intent.putExtra("commID", communityID);
                                mContext.startActivity(intent);
                            }
                        });
                    }catch(NullPointerException e){
                        System.out.println(documentSnapshot.getId()+" "+TAG);
                    }
                }
            });
        } else {
            linkedTopicImageView.setImageBitmap(null);
        }
    }

    public void removePost(){
        //TODO
        Toast.makeText(itemView.getContext(),"Methode einfügen! TODO",Toast.LENGTH_SHORT).show();
    }

    public void linkCommunity(){
        //TODO
        Toast.makeText(itemView.getContext(),"Methode einfügen! TODO",Toast.LENGTH_SHORT).show();
    }

    public String getType(){
        return "custom";
    }
}

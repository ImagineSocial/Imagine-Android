package com.imagine.myapplication.Feed.viewholder_classes;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.R;
import com.imagine.myapplication.User;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.VoteHelper;
import com.imagine.myapplication.post_classes.Post;

import java.util.Date;
import java.util.List;
import java.util.Map;


public abstract class CustomViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "CustomViewHolder";
    public View mItemView;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mItemView = itemView;
    }

    public void init(final Post post){
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
        switch(type){
            case "thanks":
                ImageButton thanksBUtton = mItemView.findViewById(R.id.thanks_button);
                thanksBUtton.setBackground(null);
//                thanksBUtton.setText(post.thanksCount+"");
                break;
            case "wow":
                ImageButton wowButton = mItemView.findViewById(R.id.wow_button);
                wowButton.setBackground(null);
//                wowButton.setText(post.wowCount+"");
                break;
            case "ha":
                ImageButton haButton = mItemView.findViewById(R.id.ha_button);
                haButton.setBackground(null);
//                haButton.setText(post.haCount+"");
                break;
            case "nice":
                ImageButton niceButton = mItemView.findViewById(R.id.nice_button);
                niceButton.setBackground(null);
//                niceButton.setText(post.niceCount+"");
                break;
            default:
                Log.d(TAG,"Invalid type String!");
                break;
        }
    }

    public void getUser(final String userID,final UserCallback callback){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(userID != "" && userID !=null){
            DocumentReference userRef = db.collection("Users").document(userID);
            System.out.println(userID+" ###############################################SWAG");
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
                                ? (String) docData.get("statustext")
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
                        System.out.println(documentSnapshot.getId()+"HEHEHEHEHEHEHEHEH!!!!");
                    }
                }
            });
        }
    }
    public String getType(){
        return "custom";
    }
    public String dateToString(Timestamp timestamp){
        Date date = timestamp.toDate();
        return date.toString();
    }
}

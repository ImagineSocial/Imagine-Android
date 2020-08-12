package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_ViewPager_Activity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HeaderViewHolder extends CustomViewHolder implements View.OnClickListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public View mItemView;
    public List<Community> facts;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mItemView = itemView;
    }

    public void bind() {

        DocumentReference topTopicRef = db.collection("TopTopicData").document("TopTopicData");

        topTopicRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    Map<String, Object> docData = documentSnapshot.getData();
                    String textOfTheWeek = (docData.get("textOfTheWeek") != null)
                            ? (String) docData.get("textOfTheWeek")
                            : (String) "";
                    TextView weekylTextView = itemView.findViewById(R.id.topTopic_weeklyTextView);
                    weekylTextView.setText(textOfTheWeek);

                    List<String> linkedFactIDs = (docData.get("linkedFactIDs") != null)
                            ? (List<String>) docData.get("linkedFactIDs")
                            : (List<String>) null;
                    getFacts(linkedFactIDs);
                } catch (NullPointerException e) {
                    System.out.println(documentSnapshot.getId() + "Got a problem with the topTopicData");
                }
            }
        });
    }

    public void getFacts(List<String> linkedFactIDs) {

        final List<Community> facts = new ArrayList<Community>();
        for (final String factID : linkedFactIDs) {
            DocumentReference factRef = db.collection("Facts").document(factID);

            factRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try {
                        Map<String, Object> docData = documentSnapshot.getData();
                        final String communityName = (docData.get("name") != null)
                                ? (String) docData.get("name")
                                : (String) "";
                        final String communityImageURL = (docData.get("imageURL") != null)
                                ? (String) docData.get("imageURL")
                                : (String) "";
                        final String description = (docData.get("description") != null)
                                ? (String) docData.get("description")
                                : (String) "";
                        final String displayOption =(String) docData.get("displayOption");

                        Community comm = new Community(communityName,communityImageURL,factID,description);
                        comm.displayOption = displayOption;
                        facts.add(comm);

                        if (facts.size() == 3) {
                            setFacts(facts);

                        }
                    } catch (NullPointerException e) {
                        System.out.println(documentSnapshot.getId() + "Got a problem fetching a fact for the top topic Data");
                    }
                }
            });
        }
    }

    public void setFacts(List<Community> facts) {
        final Community community1 = facts.get(0);
        final Community community2 = facts.get(1);
        final Community community3 = facts.get(2);
        this.facts = facts;

        ImageView firstImageView = itemView.findViewById(R.id.topTopic_firstImageView);
        ImageView secondImageView = itemView.findViewById(R.id.topTopic_secondImageView);
        ImageView thirdImageView = itemView.findViewById(R.id.topTopic_thirdImageView);
        TextView firstTextView = itemView.findViewById(R.id.topTopic_firstCommButton);
        TextView secondTextView = itemView.findViewById(R.id.topTopic_secondCommButton);
        TextView thirdTextView = itemView.findViewById(R.id.topTopic_thirdCommButton);

        firstImageView.setClipToOutline(true);
        firstImageView.setOnClickListener(this);
        Glide.with(itemView).load(community1.imageURL).into(firstImageView);
        firstTextView.setText(community1.name);
        firstTextView.setOnClickListener(this);

        secondImageView.setClipToOutline(true);
        secondImageView.setOnClickListener(this);
        Glide.with(itemView).load(community2.imageURL).into(secondImageView);
        secondTextView.setText(community2.name);
        secondTextView.setOnClickListener(this);

        thirdImageView.setClipToOutline(true);
        thirdImageView.setOnClickListener(this);
        Glide.with(itemView).load(community3.imageURL).into(thirdImageView);
        thirdTextView.setText(community3.name);
        thirdTextView.setOnClickListener(this);
    }

    public void goToCommunity(Community community) {
        Intent intent = new Intent(mContext, Community_ViewPager_Activity.class);
        intent.putExtra("name", community.name);
        intent.putExtra("description",community.description);
        intent.putExtra("imageURL", community.imageURL);
        intent.putExtra("commID", community.topicID);
        intent.putExtra("displayOption",community.displayOption);
        mContext.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        final Community community1 = facts.get(0);
        final Community community2 = facts.get(1);
        final Community community3 = facts.get(2);

        switch (v.getId()) {
            case R.id.topTopic_firstImageView:
                goToCommunity(community1);
                break;
            case R.id.topTopic_secondImageView:
                goToCommunity(community2);
                break;
            case R.id.topTopic_thirdImageView:
                goToCommunity(community3);
                break;
            case R.id.topTopic_firstCommButton:
                goToCommunity(community1);
                break;
            case R.id.topTopic_secondCommButton:
                goToCommunity(community2);
                break;
            case R.id.topTopic_thirdCommButton:
                goToCommunity(community3);
                break;
        }
    }
}


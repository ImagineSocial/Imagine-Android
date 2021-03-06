package com.imagine.myapplication.Feed.viewholder_classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_ViewPager_Activity;
import com.imagine.myapplication.CommunityPicker.CommunityPickActivity;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.LinkedCommunityCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.ReportDialogFragment;
import com.imagine.myapplication.VoteHelper;
import com.imagine.myapplication.post_classes.Post;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public abstract class CustomViewHolder extends RecyclerView.ViewHolder  {

    private static final String TAG = "CustomViewHolder";
    public View mItemView;
    public Context mContext;
    public Community community;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Activity mainActivty;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mItemView = itemView;
    }

    public void init(final Post post){
        // sets up the onClickListeners for the buttons
        final VoteHelper vote = new VoteHelper(itemView.getContext());
        this.mContext = itemView.getContext();
        ImageButton thanksButton = mItemView.findViewById(R.id.thanks_button);
        ImageButton wowButton = mItemView.findViewById(R.id.wow_button);
        ImageButton haButton = mItemView.findViewById(R.id.ha_button);
        ImageButton niceButton = mItemView.findViewById(R.id.nice_button);
        ImageView linkedTopicImageView = itemView.findViewById(R.id.topicImageView);
        linkedTopicImageView.setVisibility(View.INVISIBLE);
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


    public void setLinkedFact(final String linkedTopicID){
        // sets the linkedfactID and fetches communitydata to display
        Communities_Helper helper = new Communities_Helper(itemView.getContext());
        helper.fetchLinkedCommunity(linkedTopicID, new LinkedCommunityCallback() {
            @Override
            public void onCallback(Community comm) {
                community = comm;
                setUpLinkedCommunity(comm);
           }
        });
    }

    public void setUpLinkedCommunity(final Community comm){
        ImageView linkedTopicImageView = itemView.findViewById(R.id.topicImageView);
        linkedTopicImageView.setVisibility(View.VISIBLE);
        if (comm.imageURL != "") {
            Glide.with(itemView).load(comm.imageURL).into(linkedTopicImageView);
        } else {
            Glide.with(itemView).load(R.drawable.fact_stamp).into(linkedTopicImageView);
        }
        linkedTopicImageView.setClipToOutline(true);
        linkedTopicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recentString = "";
                try{
                    FileInputStream inputStreamReader = mContext.openFileInput("recents.txt");
                    if(inputStreamReader != null){
                        InputStreamReader reader = new InputStreamReader(inputStreamReader);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String onjString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while((onjString = bufferedReader.readLine()) != null){
                            stringBuilder.append("\n").append(onjString);
                        }
                        inputStreamReader.close();
                        recentString = stringBuilder.toString();
                        Gson gson = new Gson();
                        Community[] recents = gson.fromJson(recentString,Community[].class);
                        ArrayList<Community> recentsList = new ArrayList<>();
                        int counter = 0;
                        for(Community community : recents){
                            if(!community.topicID.equals(comm.topicID)){
                                recentsList.add(community);
                                counter++;
                                if(counter == 10) break;
                            }
                        }
                        recentsList.add(comm);
                        String newRecents = gson.toJson(recentsList);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext
                                .openFileOutput("recents.txt", Context.MODE_PRIVATE));
                        outputStreamWriter.write(newRecents);
                        outputStreamWriter.close();
                    }
                }
                catch(FileNotFoundException e){
                    Log.e("login activity", "File not found: " + e.toString());
                }
                catch(IOException e){
                    Log.e("login activity", "Can not read file: " + e.toString());
                }
                Intent intent = new Intent(itemView.getContext(), Community_ViewPager_Activity.class);
                Gson gson = new Gson();
                String jsonComm = gson.toJson(comm);
                intent.putExtra("comm", jsonComm);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    public void removePost(Post post){
        Post_Helper helper = new Post_Helper(itemView.getContext());
        helper.removePost(post);
    }

    public void showReportDialog(Post post){
        ReportDialogFragment frag = new ReportDialogFragment(itemView.getContext());
        frag.post = post;
        frag.show();
    }

    public void linkCommunity(Post post){
        Intent intent = new Intent(itemView.getContext(), CommunityPickActivity.class);
        intent.putExtra("postID",post.documentID);
        this.mainActivty.startActivityForResult(intent,5);
    }


    public void repostPost(Post post){
        System.out.println("!");
    }

    public String getType(){
        return "custom";
    }
}

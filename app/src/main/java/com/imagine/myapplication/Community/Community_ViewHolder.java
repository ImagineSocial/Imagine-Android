package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.R;
import com.imagine.myapplication.nav_fragments.Communities_Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Community_ViewHolder extends RecyclerView.ViewHolder {

    public Context mContext;
    public Community comm;

    public Community_ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final Community comm){
        // sets up the communityviews and the onClickListener
        TextView title_tv = itemView.findViewById(R.id.comm_title);
        TextView description_tv = itemView.findViewById(R.id.comm_description);
        ImageView imageView = itemView.findViewById(R.id.comm_picture);
        ConstraintLayout contentView = itemView.findViewById(R.id.community_content_view);
        contentView.setClipToOutline(true);
        final String name = comm.name;
        final String description = comm.description;
        final String imageURL = comm.imageURL;
        final String commID = comm.topicID;
        final String displayOption = comm.displayOption;
        this.comm = new Community(name,imageURL,commID,description);
        this.comm.displayOption = displayOption;
        title_tv.setText(name);
        description_tv.setText(description);
        if(imageURL != null) {
            Glide.with(itemView).load(imageURL).into(imageView);
        } else {
            Glide.with(itemView).load(R.drawable.fact_stamp);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToRecents(comm);
                Intent intent = new Intent(itemView.getContext(),Community_ViewPager_Activity.class);
                intent.putExtra("name", name);
                intent.putExtra("description",description);
                intent.putExtra("imageURL", imageURL);
                intent.putExtra("commID", commID);
                intent.putExtra("displayOption",displayOption);
                mContext.startActivity(intent);
            }
        });
    }

    public void addToRecents(Community comm){
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
                int counter =0;
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
    }

}

package com.imagine.myapplication.Community;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.imagine.myapplication.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Community_Recent_Viewholder extends RecyclerView.ViewHolder {

    public Community comm;

    public Community_Recent_Viewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(final Community comm){
        ImageView imageView = itemView.findViewById(R.id.recent_image);
        imageView.setClipToOutline(true);
        this.comm = comm;
        if(comm.imageURL != null){
            Glide.with(itemView).load(comm.imageURL).into(imageView);
        }else{
            Glide.with(itemView).load(R.drawable.fact_stamp).into(imageView);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToRecents(comm);
                Intent intent = new Intent(itemView.getContext(),Community_ViewPager_Activity.class);
                intent.putExtra("name", comm.name);
                intent.putExtra("description",comm.description);
                intent.putExtra("imageURL", comm.imageURL);
                intent.putExtra("commID", comm.topicID);
                intent.putExtra("displayOption",comm.displayOption);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    public void addToRecents(Community comm){
        String recentString = "";
        try{
            FileInputStream inputStreamReader = itemView.getContext().openFileInput("recents.txt");
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
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(itemView.getContext()
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

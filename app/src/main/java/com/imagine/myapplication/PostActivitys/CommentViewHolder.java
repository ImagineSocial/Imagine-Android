package com.imagine.myapplication.PostActivitys;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.imagine.myapplication.Comment;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;

import java.util.Date;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Comment comment;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Comment comm){
        this.comment = comm;
        ImageView profile = itemView.findViewById(R.id.profile_picture_imageView);
        TextView name = itemView.findViewById(R.id.name_textView);
        TextView date = itemView.findViewById(R.id.createDate_textView);
        TextView body = itemView.findViewById(R.id.comment_body);

        date.setText(dateToString(comm.sentAt));
        body.setText(comm.body);

        if(comm.userID.equals("anonym")){
            name.setText("Anonym");
            Glide.with(itemView).load(R.drawable.anonym_user).into(profile);
        }else{

            if(comm.user == null){
                Glide.with(itemView).load(R.drawable.default_user).into(profile);
                name.setText("Anonym");
                this.getUser(comm.userID);
            }else{
                name.setText(comm.user.name);
                if(!comm.user.imageURL.equals("")){
                    Glide.with(itemView).load(comm.user.imageURL).into(profile);
                }
            }
        }
    }

    public String dateToString(Timestamp timestamp){
        Date date = timestamp.toDate();
        return date.toString();
    }

    public void getUser(final String userID){
       DocumentReference userRef = db.collection("Users").document(userID);
       userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isComplete()){
                   DocumentSnapshot docSnap = task.getResult();
                   String imageURL = docSnap.getString("profilePictureURL");
                   String name = docSnap.getString("name");
                   String surname = docSnap.getString("surname");
                   User user = new User(name,surname,userID);
                   comment.user = user;
                   user.imageURL = (imageURL != null) ? imageURL : "";
                   setUserAgain();
               }else if(task.isCanceled()){

               }
           }
       });
    }

    public void setUserAgain (){

        TextView name = itemView.findViewById(R.id.name_textView);
        ImageView profile = itemView.findViewById(R.id.profile_picture_imageView);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String userString = gson.toJson(comment.user);
                Intent intent = new Intent(itemView.getContext(), UserActivity.class);
                intent.putExtra("user",userString);
                itemView.getContext().startActivity(intent);
            }
        });
        System.out.println(this.comment);
        if(comment.userID.equals("anonym")){
            name.setText("Anonym");
            Glide.with(itemView).load(R.drawable.default_user).into(profile);
        }else{

            if(comment.user == null){
                Glide.with(itemView).load(R.drawable.default_user).into(profile);
                name.setText("Anonym");
            }else{
                name.setText(comment.user.name);
                if(!comment.user.imageURL.equals("")){
                    Glide.with(itemView).load(comment.user.imageURL).into(profile);
                }
            }
        }
    }
}

package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.R;
import com.imagine.myapplication.User;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.LinkPost;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

public class LinkViewHolder extends CustomViewHolder {
    private static final String TAG = "LinkViewHolder";
    public Context mContext;

    public LinkViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final LinkPost post){
        init(post);
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        // PreView Widgets
        final ImageView preViewImage = itemView.findViewById(R.id.preView_image);
        final TextView preViewTitle = itemView.findViewById(R.id.preView_title);
        final TextView preViewDescription = itemView.findViewById(R.id.preView_description);

        String dateString =dateToString(post.createTime);
        title_textView.setText(post.title);
        createTime_textView.setText(dateString);
        RichPreview richPreview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                String imageURL = metaData.getImageurl();
                String description = metaData.getDescription();
                String title = metaData.getTitle();
                if((imageURL != null) && (!imageURL.equals(""))){
                    Glide.with(itemView).load(imageURL).into(preViewImage);
                }
                if((description != null) && (!description.equals(""))){
                   preViewDescription.setText(description);
                }
                if((title != null) && (!title.equals(""))){
                    preViewTitle.setText(title);
                }
            }

            @Override
            public void onError(Exception e) {
                System.out.println("");
            }
        });
        richPreview.getPreview(post.link);
        if(post.originalPoster == "anonym"){
            name_textView.setText("Anonym");
            Glide.with(itemView).load(R.drawable.default_user).into(
                    profilePicture_imageView);
        }else{
            getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                    setName(post);
                }
            });
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text  = "PicturePost clicked";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(mContext, text, duration);
                toast.show();
            }
        });
    }
    public void setName(LinkPost post){
        TextView username_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        username_textView.setText(post.user.name+ "  LinkPost");
        if(post.user.imageURL == null || post.user.imageURL == ""){
            Glide.with(itemView).load(R.drawable.default_user).into(
                    profilePicture_imageView
            );
        }
        else{
            Glide.with(itemView).load(post.user.imageURL).into(
                    profilePicture_imageView
            );
        }
    }

    public String getType(){
        return "link";
        }
}





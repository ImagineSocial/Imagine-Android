package com.imagine.myapplication.PostActivitys;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.imagine.myapplication.Comment;
import com.imagine.myapplication.CommentsCallback;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_ViewPager_Activity;
import com.imagine.myapplication.CommunityPicker.CommunityPickActivity;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.Post_Fragment_Classes.GifPostFragment;
import com.imagine.myapplication.R;
import com.imagine.myapplication.VoteHelper;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.user_classes.UserActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GifPostActivity extends AppCompatActivity {
    private static final String TAG = "GifPostActivity";
    public ArrayList<Comment> comments;
    public GIFPost post;
    public Community comm;
    public Context mContext;
    public Post_Helper helper = new Post_Helper();
    public ImageButton anonym;
    public ImageButton sendComment;
    public boolean anonymToggle = false;
    public EditText commentText;
    public String comment;
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Boolean isSendingComment = false;
    public RecyclerView recyclerView;
    public Comments_Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mContext = this;

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.post_frame,new GifPostFragment()).commit();
        // gets the post object from the intent
        Intent intent = getIntent();
        String objString = intent.getStringExtra("post");
        String commString = intent.getStringExtra("comm");
        Gson gson = new Gson();
        this.post = gson.fromJson(objString, GIFPost.class);
        this.comm = gson.fromJson(commString,Community.class);
        this.anonym = findViewById(R.id.post_commentview_anonym_button);
        this.sendComment = findViewById(R.id.post_commentview_send_button);
        this.commentText = findViewById(R.id.post_comment_edit_text);
    }

    @Override
    protected void onStart() {
        // stars the bind method and fetches the comments
        super.onStart();
        bind();
        Post_Helper helper = new Post_Helper();
        helper.getComments(post.documentID, new CommentsCallback() {
            @Override
            public void onCallback(ArrayList<Comment> comms) {
                System.out.println("!!");
                comments = comms;
                initRecyclerView();
            }
        });

        if(this.comm != null){
            this.setUpLinkedFact();
        }
    }

    public void bind(){
        // sets up the post specific views
        init();
        TextView title_textView = findViewById(R.id.title_textView);
        TextView createTime_textView = findViewById(R.id.createDate_textView);
        TextView username_textView = findViewById(R.id.name_textView);
        TextView description_textView = findViewById(R.id.description_tv);
        TextView commentCountLabel = findViewById(R.id.commentCountLabel);
        ImageView profilePicture_imageView = findViewById(
                R.id.profile_picture_imageView);
        final VideoView videoView = findViewById(R.id.gif_videoView);
        ConstraintLayout videoFrame = findViewById(R.id.video_frame);
        videoFrame.setClipToOutline(true);
        videoFrame.setClipToOutline(true);

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);
        commentCountLabel.setText(post.commentCount+"");

        ConstraintLayout descriptionView = findViewById(R.id.description_view);
        if (post.description.equals("")) {
            descriptionView.setVisibility(View.INVISIBLE);
        } else {
            String description = post.description.replace("\\n", "\n");
            description_textView.setText(description);
        }
        videoView.setVideoPath(post.link);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

                //Get your video's width and height
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                //Get VideoView's current width and height
                int videoViewWidth = videoView.getWidth();
                int videoViewHeight = videoView.getHeight();

                float xScale = (float) videoViewWidth / videoWidth;
                float yScale = (float) videoViewHeight / videoHeight;

                //For Center Crop use the Math.max to calculate the scale
                //float scale = Math.max(xScale, yScale);
                //For Center Inside use the Math.min scale.
                //I prefer Center Inside so I am using Math.min
                float scale = Math.max(xScale, yScale);

                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;

                //Set the new size for the VideoView based on the dimensions of the video
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.width = (int)scaledWidth;
                layoutParams.height = (int)scaledHeight;
                videoView.setLayoutParams(layoutParams);
            }
        });

        if(post.originalPoster.equals("anonym")){
            username_textView.setText(getResources().getString(R.string.anonym));
            Glide.with(this).load(R.drawable.default_user).into(
                    profilePicture_imageView);
        }else{
            username_textView.setText(post.user.name);
            profilePicture_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    String userString = gson.toJson(post.user);
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("user",userString);
                    mContext.startActivity(intent);
                }
            });
            if(post.user.imageURL != null || !post.user.imageURL.equals("")){
                Glide.with(this).load(post.user.imageURL).into(
                        profilePicture_imageView);
            } else  {
                Glide.with(this).load(R.drawable.default_user).into(
                        profilePicture_imageView);
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (post.user.userID.equals(user.getUid())) {
                    showLikeCount(post);
                }
            }
        }
        anonym.setAlpha(0.5f);
        this.anonym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonymToggle = !anonymToggle;
                if(anonymToggle){
                    anonym.setAlpha(1f);
                }else{
                    anonym.setAlpha(0.5f);
                }
            }
        });

        sendComment.setAlpha(0.5f);
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                comment  = s.toString();
                if(comment.equals("")){
                    sendComment.setAlpha(0.5f);
                    sendComment.setOnClickListener(null);
                }else{
                    sendComment.setAlpha(1f);
                    sendComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isSendingComment && !comment.equals("")) {
                                isSendingComment = true;
                                helper.addCommentToFirebase(new CommentsCallback() {
                                    @Override
                                    public void onCallback(ArrayList<Comment> comms) {
                                        isSendingComment = false;
                                        if(comms == null){
                                            Toast.makeText(mContext,getResources().getString(R.string.post_activity_comment_fail),Toast.LENGTH_SHORT).show();
                                        }else{
                                            commentText.setText(null);
                                            commentText.clearFocus();
                                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(commentText.getWindowToken(),
                                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                            comments.add(comms.get(0));
                                            adapter.getNewComments(comments);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }, post, anonymToggle, comment);
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageButton options = findViewById(R.id.feed_menu_button);
        if(auth.getCurrentUser()!= null&& post.originalPoster.equals(auth.getCurrentUser().getUid())){
            options.setVisibility(View.VISIBLE);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu();
                }
            });
        } else {
            options.setVisibility(View.INVISIBLE);
        }

    }

    public void initRecyclerView(){
        this.recyclerView = findViewById(R.id.post_activity_recyclerView);
        this.adapter = new Comments_Adapter(comments,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void init(){
        // sets up the onClickListeners for the buttons
        final VoteHelper vote = new VoteHelper();
        ImageButton thanksButton = findViewById(R.id.thanks_button);
        ImageButton wowButton = findViewById(R.id.wow_button);
        ImageButton haButton = findViewById(R.id.ha_button);
        ImageButton niceButton = findViewById(R.id.nice_button);
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
                ImageButton thanksBUtton = findViewById(R.id.thanks_button);
                TextView thanksCounttv = findViewById(R.id.thanks_count_textView);
                thanksBUtton.setEnabled(false);
                thanksBUtton.setVisibility(View.INVISIBLE);
                thanksCounttv.setText(String.valueOf(post.thanksCount++));
                break;
            case "wow":
                ImageButton wowButton = findViewById(R.id.wow_button);
                TextView wowCounttv = findViewById(R.id.wow_count_textView);
                wowButton.setEnabled(false);
                wowButton.setVisibility(View.INVISIBLE);
                wowCounttv.setText(String.valueOf(post.wowCount++));
                break;
            case "ha":
                ImageButton haButton = findViewById(R.id.ha_button);
                TextView haCounttv = findViewById(R.id.ha_count_textView);
                haButton.setEnabled(false);
                haButton.setVisibility(View.INVISIBLE);
                haCounttv.setText(String.valueOf(post.haCount++));
                break;
            case "nice":
                ImageButton niceButton = findViewById(R.id.nice_button);
                TextView niceCounttv = findViewById(R.id.nice_count_textView);
                niceButton.setEnabled(false);
                niceButton.setVisibility(View.INVISIBLE);
                niceCounttv.setText(String.valueOf(post.niceCount++));
                break;
            default:
                Log.d(TAG,"Invalid type String!");
                break;
        }
    }

    public void showLikeCount(Post post) {
        ImageButton thanksBUtton = findViewById(R.id.thanks_button);
        TextView thanksCounttv = findViewById(R.id.thanks_count_textView);
        thanksBUtton.setEnabled(false);
        thanksBUtton.setVisibility(View.INVISIBLE);
        thanksCounttv.setText(String.valueOf(post.thanksCount));

        ImageButton wowButton = findViewById(R.id.wow_button);
        TextView wowCounttv = findViewById(R.id.wow_count_textView);
        wowButton.setEnabled(false);
        wowButton.setVisibility(View.INVISIBLE);
        wowCounttv.setText(String.valueOf(post.wowCount));

        ImageButton haButton = findViewById(R.id.ha_button);
        TextView haCounttv = findViewById(R.id.ha_count_textView);
        haButton.setEnabled(false);
        haButton.setVisibility(View.INVISIBLE);
        haCounttv.setText(String.valueOf(post.haCount));

        ImageButton niceButton = findViewById(R.id.nice_button);
        TextView niceCounttv = findViewById(R.id.nice_count_textView);
        niceButton.setEnabled(false);
        niceButton.setVisibility(View.INVISIBLE);
        niceCounttv.setText(String.valueOf(post.niceCount));
    }

    public void setUpLinkedFact(){
        ImageView communityImage = findViewById(R.id.topicImageView);
        Glide.with(this).load(comm.imageURL).into(communityImage);
        communityImage.setClipToOutline(true);
        communityImage.setOnClickListener(new View.OnClickListener() {
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
                        for(Community comm : recents){
                            if(!comm.topicID.equals(comm.topicID)){
                                recentsList.add(comm);
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
                Intent intent = new Intent(mContext, Community_ViewPager_Activity.class);
                Gson gson = new Gson();
                String jsonComm = gson.toJson(comm);
                intent.putExtra("comm", jsonComm);
                mContext.startActivity(intent);
            }
        });
    }

    public void showMenu(){
        ImageButton options = findViewById(R.id.feed_menu_button);

        PopupMenu menu = new PopupMenu(this,options);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.remove_post:
                        helper.removePost(post);
                        return true;
                    case R.id.link_community:
                        linkCommunity(post);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.feed_post_menu, menu.getMenu());
        menu.show();
    }

    public void linkCommunity(Post post){
        Intent intent = new Intent(this, CommunityPickActivity.class);
        intent.putExtra("postID",post.documentID);
        startActivityForResult(intent,5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5){
            String name = data.getStringExtra("name");
            String imageURL = data.getStringExtra("imageURL");
            String commID = data.getStringExtra("commID");
            String postID = data.getStringExtra("postID");

            DocumentReference postRef = db.collection("Posts").document(postID);
            HashMap<String,Object> updateData = new HashMap<>();
            updateData.put("linkedFactID",commID);
            postRef.update(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        System.out.println("!");
                    }
                }
            });


            CollectionReference commRef = db.collection("Facts")
                    .document(commID).collection("posts");
            DocumentReference commPostRef = commRef.document(postID);
            HashMap<String,Object> map = new HashMap<>();
            map.put("createTime",new Timestamp(new Date()));
            commPostRef.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        System.out.println("Erfolg!");
                    } else if(task.isCanceled()){
                        System.out.println("Fail!");
                    }
                }
            });
        }
    }

}

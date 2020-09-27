package com.imagine.myapplication.Community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imagine.myapplication.R;
import com.synnapps.carouselview.ImageListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.Duration;

public class Community_New_Community_Activity extends AppCompatActivity {
    private static final String TAG = "Community_New_Community";
    public EditText title;
    public EditText description;
    public String displayOption;
    public Button shareButton;
    public ImageButton folderButton;
    public ImageView picturePreview;
    public Bitmap bitmap;
    public final int GALLERY =1;
    public float imageWidth;
    public float imageHeight;
    public Context mContext = this;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public StorageReference storage = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_new_activity);
        Intent intent = getIntent();
        this.displayOption = intent.getStringExtra("type");
        setUpViews();
    }

    public void setUpViews(){
        this.title = findViewById(R.id.new_community_title_text_edit);
        this.description = findViewById(R.id.new_community_description_text_edit);
        this.shareButton = findViewById(R.id.new_community_create_button);
        this.folderButton = findViewById(R.id.new_community_choose_image_button);
        this.picturePreview = findViewById(R.id.new_community_preview_picture);
        this.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTapped();
            }
        });

        this.folderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderButtonClicked();
            }
        });
    }

    public void shareTapped(){
        checkEditTexts();
    }

    public void checkEditTexts(){
        if(auth.getCurrentUser() == null){
            Toast.makeText(this,"Bitte logge dich ein um eine Community zu erstellen!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(title.getText().toString().equals("")){
            Toast.makeText(this,"Bitte geben Sie einen Name für Ihre COmmunity ein!",
                    Toast.LENGTH_SHORT).show();
        }else{
            if(description.getText().toString().equals("")){
                Toast.makeText(this,"Bitte geben Sie eine Beschriebung für Ihre Community ein!",
                        Toast.LENGTH_SHORT).show();
            } else{
                DocumentReference docRef = db.collection("Facts").document();
                if(this.bitmap != null){
                    uploadPhotoToFirebase(docRef);
                }else{
                    setUpCommunityData(docRef,null);
                }
            }
        }
    }

    public void folderButtonClicked(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GALLERY);
    }

    public void uploadPhotoToFirebase(final DocumentReference docRef){
        String id = docRef.getId();
        final StorageReference storeRef = storage.child("factPictures").child(id+".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.bitmap.compress(Bitmap.CompressFormat.JPEG,10,baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storeRef.putBytes(data);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    System.out.println("Bild wurde hochgeladen!"+TAG);
                    Task<Uri> imageURL = storeRef.getDownloadUrl();
                    imageURL.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uriString = uri.toString();
                            System.out.println("Hier ist die URL:" + uriString+" "+TAG);
                            setUpCommunityData(docRef,uriString);
                        }
                    });
                }else if(task.isCanceled()){
                    System.out.println("upload failed! "+TAG);
                }
            }
        });
    }

    public void setUpCommunityData(DocumentReference docRef,String url){
        FirebaseUser user = auth.getCurrentUser();
        String name = this.title.getText().toString();
        String description = this.description.getText().toString();
        String OP = user.getUid();
        String imageURL = url;
        List follower = new ArrayList<>();
        follower.add(OP);

        HashMap<String,Object> data = new HashMap<>();
        data.put("OP",OP);
        data.put("name",name);
        data.put("description",description);
        data.put("displayOption",this.displayOption);
        data.put("popularity", new Integer(10));
        data.put("createDate", new Date());
        data.put("follower", follower); // Set yourself as the first follower

        if(imageURL != null){
            data.put("imageURL",imageURL);
        }
        uploadData(docRef,data);
    }

    public void uploadData(DocumentReference docRef, HashMap<String,Object> data){
        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mContext,"Community erstellt!",Toast.LENGTH_SHORT);
                    resetEditText();
                } else if(task.isCanceled())
                    Toast.makeText(mContext,"Community NICHT erstellt!",Toast.LENGTH_SHORT);

            }
        });
    }

    public void resetEditText(){
        this.title.setText("");
        this.description.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY){
            if(resultCode == Activity.RESULT_OK){
                if(data != null){
                    final Uri contentURI = data.getData();
                    try{
                        if(Build.VERSION.SDK_INT <28){
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(),contentURI);
                            this.bitmap = bitmap;
                            imageWidth = (float) bitmap.getWidth();
                            imageHeight = (float) bitmap.getHeight();
                            picturePreview.setImageBitmap(this.bitmap);
                        }else{
                            ImageDecoder.Source source = ImageDecoder.createSource(
                                    getApplicationContext().getContentResolver(),contentURI);
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            this.bitmap = bitmap;
                            imageWidth = (float) bitmap.getWidth();
                            imageHeight = (float) bitmap.getHeight();
                            picturePreview.setImageBitmap(this.bitmap);
                        }
                    }catch(Exception e){
                        System.out.println(e.getStackTrace().toString()+" "+TAG);
                    }
                }
            }
        }
    }
}

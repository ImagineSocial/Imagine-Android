package com.imagine.myapplication.nav_fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imagine.myapplication.Post_Fragment_Classes.LinkPostFragment;
import com.imagine.myapplication.Post_Fragment_Classes.PicturePostFragment;
import com.imagine.myapplication.Post_Fragment_Classes.ThoughtPostFragment;
import com.imagine.myapplication.Post_Fragment_Classes.YouTubePostFragment;
import com.imagine.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class New_Post_Fragment extends Fragment implements View.OnClickListener {

    public StorageReference storeRef = FirebaseStorage.getInstance().getReference();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public String type = "thought";
    public int duration = Toast.LENGTH_SHORT;
    private static final int
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    public Bitmap image_inBitmap = null;
    public float imageHeight = 0f;
    public float imageWidth = 0f;
    public Uri imageUri;

    public float halfAlpha = 0.5f;
    public float fullAlpha = 1f;

    public final int GALLERY = 1;
    public final int IMAGE_CAPTURE = 2;

    public View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_post,container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button newThoughtButton = (Button) view.findViewById(R.id.new_thought_button);
        newThoughtButton.setOnClickListener(this);
        Button newPictureButton = (Button) view.findViewById(R.id.new_picture_button);
        newPictureButton.setOnClickListener(this);
        Button newLinkButton = (Button) view.findViewById(R.id.new_link_button);
        newLinkButton.setOnClickListener(this);
        Button newGIFButton = (Button) view.findViewById(R.id.new_gif_button);
        newGIFButton.setOnClickListener(this);
        ImageButton pictureFolder_button = getView().findViewById(R.id.pictureFolder_Button);
        pictureFolder_button.setOnClickListener(this);
        ImageButton pictureCamera_button = getView().findViewById(R.id.pictureCamera_button);
        pictureCamera_button.setOnClickListener(this);
        Button share_button = getView().findViewById(R.id.share_button);
        share_button.setOnClickListener(this);


        this.view = view;

        super.onViewCreated(view, savedInstanceState);


        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.post_preview, new ThoughtPostFragment())
                .commit();

        newThoughtButton.setAlpha(halfAlpha);

        pictureFolder_button.setEnabled(false);
        pictureFolder_button.setAlpha(halfAlpha);
        pictureCamera_button.setEnabled(false);
        pictureCamera_button.setAlpha(halfAlpha);

        setThought();
    }

    @Override
    public void onClick(View v) {

        final FragmentManager fragmentManager = getFragmentManager();
        Button newThoughtButton = (Button) view.findViewById(R.id.new_thought_button);
        Button newPictureButton = (Button) view.findViewById(R.id.new_picture_button);
        Button newLinkButton = (Button) view.findViewById(R.id.new_link_button);
        Button newGIFButton = (Button) view.findViewById(R.id.new_gif_button);

        newThoughtButton.setAlpha(fullAlpha);
        newPictureButton.setAlpha(fullAlpha);
        newLinkButton.setAlpha(fullAlpha);
        newGIFButton.setAlpha(fullAlpha);

        switch (v.getId()) {
            case R.id.new_thought_button:
                newThoughtButton.setAlpha(halfAlpha);

                setThought();
                fragmentManager.beginTransaction()
                        .replace(R.id.post_preview, new ThoughtPostFragment())
                        .commit();
                break;
            case R.id.new_picture_button:
                newPictureButton.setAlpha(halfAlpha);
                if(getView().getContext().checkSelfPermission(Manifest.
                        permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String []{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                }else{
                    showPicture();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.post_preview, new PicturePostFragment())
                        .commit();
                break;
            case R.id.new_link_button:
                newLinkButton.setAlpha(halfAlpha);

                showLink();
                fragmentManager.beginTransaction()
                        .replace(R.id.post_preview, new LinkPostFragment())
                        .commit();
                break;
            case R.id.new_gif_button:
                newGIFButton.setAlpha(halfAlpha);

                showYouTube();
                fragmentManager.beginTransaction()
                        .replace(R.id.post_preview, new YouTubePostFragment())
                        .commit();
                break;
            case R.id.pictureFolder_Button:
                choosePhotoFromGallery();
                break;
            case R.id.pictureCamera_button:
                takePhotoFromCamera();
                break;
            case R.id.share_button:
                shareTapped();
                break;
            default:
                setThought();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //Sollte noch woanders hin, Herr Schultz
        ImageButton cameraButton = getView().findViewById(R.id.pictureCamera_button);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ImageButton cameraButton = getView().findViewById(R.id.pictureCamera_button);
        if((requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)){
            cameraButton.setEnabled(true);
            cameraButton.setAlpha(fullAlpha);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView preview_image = getView().findViewById(R.id.picture_imageView);//TODO

        if(requestCode == GALLERY){
            if(data != null){
                Uri contentURI = data.getData();
                try{
                    if(Build.VERSION.SDK_INT <28){
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext()
                                .getContentResolver(),contentURI);
                        image_inBitmap = bitmap;
                        imageWidth = (float) bitmap.getWidth();
                        imageHeight = (float) bitmap.getHeight();
                        //preview_image.setImageBitmap(bitmap);
                        Glide.with(getView()).load(contentURI).into(preview_image);
                    }else{
                        ImageDecoder.Source source = ImageDecoder.createSource(getContext()
                                .getContentResolver(),contentURI);
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        Glide.with(getView()).load(contentURI).into(preview_image);
                        //preview_image.setImageBitmap(bitmap);
                        image_inBitmap = bitmap;
                        imageWidth = (float) bitmap.getWidth();
                        imageHeight = (float) bitmap.getHeight();
                    }
                }catch(Exception e){
                    System.out.println(e.getStackTrace().toString());
                }
            }
        }else if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                try {
                    if(Build.VERSION.SDK_INT < 28){
                        Bitmap b1 = MediaStore.Images.Media
                                .getBitmap(
                                        getContext().getContentResolver(), imageUri);

                        Glide.with(getView()).load(b1).into(preview_image);
                    }else{
                        ImageDecoder.Source source = ImageDecoder.createSource(getContext()
                                .getContentResolver(),imageUri);
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        Glide.with(getView()).load(imageUri).into(preview_image);
                    }

                } catch (IOException e) {
                    System.out.println("FEHLER!");
                }
            } else {
                int rowsDeleted =
                        getContext().getContentResolver().delete(imageUri,
                                null, null);
                System.out.println("REIHEN GELÖSCHT!");
            }
        }
    }

    public void shareTapped(){
        // TODO
        EditText title_editText = getView().findViewById(R.id.title_editText);
        EditText link_editText = getView().findViewById(R.id.link_editText);
        ImageView preView_image = getView().findViewById(R.id.preview_imageView);

        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null){
            if(title_editText.getText().equals("") || title_editText.getText().equals(null)){
                Toast.makeText(getContext(),"Gib einen Titel ein!",duration);
            }else{
                DocumentReference docRef = db.collection("Posts").document();
                System.out.println("Das ist die Post ID:" + docRef.getId());

                switch(type){
                    case "picture":
                        if(preView_image.getDrawable() == null || imageHeight == 0f
                                || imageWidth == 0f){
                            Toast.makeText(getContext(),"Bitte wähle ein Bild aus", duration)
                            .show();
                        }else{
                            loadPictureToFirebase(docRef);
                        }
                        break;
                    case "thought":
                        postThought(docRef);
                        break;
                    case "link":
                        if(link_editText.getText().equals("") || link_editText.getText().equals(null)){
                            Toast.makeText(getContext(), "Gib bitte einen Link ein!", duration);
                        } else {
                            postLink(docRef);
                        }
                        break;
                    case "youTube":
                        if(link_editText.getText().equals("") || link_editText.getText().equals(null)){
                            Toast.makeText(getContext(), "Gib bitte einen Link ein!", duration);
                        } else {
                            postYouTube(docRef);
                        }
                        break;
                    default:
                        Toast.makeText(getContext(),"Type Fehler!", duration);
                        break;

                }
            }
        }else{
            Toast.makeText(getContext(),"Log dich ein um zu Posten!",duration).show();
        }
    }

    public void choosePhotoFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GALLERY);
    }

    public void takePhotoFromCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Kamera Test");
        values.put(MediaStore.Images.Media.DESCRIPTION,
                "Mit App aufgenommen!");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI,values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,IMAGE_CAPTURE);
    }

    public void setThought(){
        this.type = "thought";
        hidePicture();
        hideLink();
    }

    public void loadPictureToFirebase(final DocumentReference docRef){
        //TODO
        String pathString = docRef.getId()+".png";
        final StorageReference pictureRef = storeRef.child("postPictures").child(pathString);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image_inBitmap.compress(Bitmap.CompressFormat.JPEG,10,baos);
        byte [] data = baos.toByteArray();
        UploadTask uploadTask = pictureRef.putBytes(data);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    System.out.println("Bild wurde erfolgreich hochgeladen!");
                    Task<Uri> imageURL = pictureRef.getDownloadUrl();
                    imageURL.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uriString = uri.toString();
                            System.out.println("Hier ist die URL:" + uriString);
                            postPicture(docRef,uriString);
                        }
                    });

                }else if( task.isCanceled()){
                    System.out.println("Konnte Bild nicht hochladen!");
                }
            }
        });

    }
    public void postThought(DocumentReference docRef){

        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        EditText link_edit = getView().findViewById(R.id.link_editText);

        FirebaseUser user = auth.getCurrentUser();

        if( !user.equals(null)){
            String title = title_edit.getText().toString();
            String description = description_edit.getText().toString();
            HashMap<String,Object> data = new HashMap<>();
            data.put("title",title);
            data.put("description",description);
            data.put("originalPoster", user.getUid());
            data.put("createTime", new Timestamp(new Date())); // TODO
            data.put("thanksCount",new Integer(0));
            data.put("wowCount", new Integer(0));
            data.put("haCount", new Integer(0));
            data.put("niceCount", new Integer(0));
            data.put("type", "thought");
            data.put("repost", "normal");

            uploadData(docRef,data);
        }else
            System.out.println("Kein User in PostThought-Methode!");
    }
    public void postLink(DocumentReference docRef){
        //TODO
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        EditText link_edit = getView().findViewById(R.id.link_editText);

        FirebaseUser user = auth.getCurrentUser();
        int test = 0;
        if( !user.equals(null)){
            String title = title_edit.getText().toString();
            String description = description_edit.getText().toString();
            String link = link_edit.getText().toString();
            HashMap<String,Object> data = new HashMap<>();
            data.put("title",title);
            data.put("description",description);
            data.put("originalPoster", user.getUid());
            data.put("createTime", new Timestamp(new Date())); // TODO
            data.put("thanksCount",new Integer(0));
            data.put("wowCount", new Integer(0));
            data.put("haCount", new Integer(0));
            data.put("niceCount", new Integer(0));
            data.put("type", "link");
            data.put("repost", "normal");
            data.put("link",link);

            uploadData(docRef,data);
        }else
            System.out.println("Kein User in PostLink-Methode!");
    }

    public void postYouTube(DocumentReference docRef){
        //TODO
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        EditText link_edit = getView().findViewById(R.id.link_editText);

        FirebaseUser user = auth.getCurrentUser();

        if( !user.equals(null)){
            String title = title_edit.getText().toString();
            String description = description_edit.getText().toString();
            String link = link_edit.getText().toString();
            HashMap<String,Object> data = new HashMap<>();
            data.put("title",title);
            data.put("description",description);
            data.put("originalPoster", user.getUid());
            data.put("createTime", new Timestamp(new Date())); // TODO
            data.put("thanksCount",new Integer(0));
            data.put("wowCount", new Integer(0));
            data.put("haCount", new Integer(0));
            data.put("niceCount", new Integer(0));
            data.put("type", "youTubeVideo"); // TODO
            data.put("repost", "normal");
            data.put("link",link);

            uploadData(docRef,data);
        }else
            System.out.println("Kein User in PostYouTUbe-Methode!");
    }

    public void postPicture(DocumentReference docRef, String url){
        //TODO
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);

        System.out.println("Das ist die imageURL: "+url);
        FirebaseUser user = auth.getCurrentUser();
        if(!user.equals(null)){
            String title = title_edit.getText().toString();
            String description = description_edit.getText().toString();
            HashMap<String,Object> data = new HashMap<>();
            data.put("title",title);
            data.put("description",description);
            data.put("originalPoster", user.getUid());
            data.put("createTime", new Timestamp(new Date())); // TODO
            data.put("thanksCount",new Integer(0));
            data.put("wowCount", new Integer(0));
            data.put("haCount", new Integer(0));
            data.put("niceCount", new Integer(0));
            data.put("type", "picture");
            data.put("repost", "normal");
            data.put("imageURL", url);
            data.put("imageHeight", new Float(imageHeight));
            data.put("imageWidth", new Float(imageWidth));
            uploadData(docRef,data);
        }else
            System.out.println("Kein User in postPicture-Methode!");
    }

    public void uploadData(DocumentReference docRef, HashMap<String,Object> data){
        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Post erfolgreich erstellt!");
                    postedSuccessful();
                } else if(task.isCanceled())
                    System.out.println("Post Erstellung fehlgeschlagen!");

            }
        });
    }

    public void postedSuccessful(){
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        EditText link_edit = getView().findViewById(R.id.link_editText);
        ImageView preview_imageView = getView().findViewById(R.id.preview_imageView);

        title_edit.getText().clear();
        description_edit.getText().clear();
        link_edit.getText().clear();
        preview_imageView.setImageResource(0);

    }

    public void hidePicture(){
        ImageButton pictureCamera_button = getView().findViewById(R.id.pictureCamera_button);
        ImageButton pictureFolder_Button = getView().findViewById(R.id.pictureFolder_Button);
        TextView picture_label = getView().findViewById(R.id.picture_label);

        pictureCamera_button.setAlpha(halfAlpha);
        pictureFolder_Button.setAlpha(halfAlpha);
        picture_label.setAlpha(halfAlpha);

        pictureCamera_button.setEnabled(false);
        pictureFolder_Button.setEnabled(false);
    }

    public void hideLink(){
        TextView link_label = getView().findViewById(R.id.link_label);
        EditText link_editText = getView().findViewById(R.id.link_editText);

        link_label.setAlpha(halfAlpha);
        link_editText.setEnabled(false);
    }

    public void showPicture(){
        this.type = "picture";
        hideLink();

        ImageButton pictureCamera_button = getView().findViewById(R.id.pictureCamera_button);
        ImageButton pictureFolder_Button = getView().findViewById(R.id.pictureFolder_Button);
        TextView picture_label = getView().findViewById(R.id.picture_label);

        pictureCamera_button.setAlpha(fullAlpha);
        pictureCamera_button.setEnabled(true);
        pictureFolder_Button.setAlpha(fullAlpha);
        pictureFolder_Button.setEnabled(true);
        picture_label.setAlpha(fullAlpha);
    }

    public void showLink(){
        this.type = "link";
        hidePicture();

        TextView link_label = getView().findViewById(R.id.link_label);
        EditText link_editText = getView().findViewById(R.id.link_editText);

        link_editText.setEnabled(true);
        link_label.setAlpha(fullAlpha);
    }

    public void showYouTube(){
        this.type = "youTube";

        hidePicture();

        TextView link_label = getView().findViewById(R.id.link_label);
        EditText link_editText = getView().findViewById(R.id.link_editText);

        link_editText.setEnabled(true);
        link_label.setAlpha(fullAlpha);
    }

}

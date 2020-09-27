package com.imagine.myapplication.nav_fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_New_Post;
import com.imagine.myapplication.CommunityPicker.CommunityPickActivity;
import com.imagine.myapplication.Post_Fragment_Classes.LinkPostFragment;
import com.imagine.myapplication.Post_Fragment_Classes.MultiPictureFragment;
import com.imagine.myapplication.Post_Fragment_Classes.PicturePostFragment;
import com.imagine.myapplication.Post_Fragment_Classes.ThoughtPostFragment;
import com.imagine.myapplication.Post_Fragment_Classes.YouTubePostFragment;
import com.imagine.myapplication.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.CarouselViewPager;
import com.synnapps.carouselview.ImageListener;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

public class New_Post_Fragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "New_Post_Fragment";
    public StorageReference storeRef = FirebaseStorage.getInstance().getReference();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public String type;
    public int duration = Toast.LENGTH_SHORT;
    private static final int
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    public Bitmap image_inBitmap = null;
    public Bitmap[] multi_bitmaps = new Bitmap[3];
    public ArrayList<String> imageURLS;
    public float imageHeight = 0f;
    public float imageWidth = 0f;
    public Uri imageUri;
    public String linkedFactID;
    public float halfAlpha = 0.5f;
    public float fullAlpha = 1f;
    public final int GALLERY = 1;
    public final int IMAGE_CAPTURE = 2;
    public final int MULTIPLE_IMAGES = 3;
    public final int COMMUNITY_PICK = 4;
    public Button shareButton;
    public View view;
    public Community comm;
    public Community_New_Post new_post_activity;

    public New_Post_Fragment(){

    }

    public New_Post_Fragment(Community_New_Post new_post_activity, Community comm){
        this.new_post_activity = new_post_activity;
        this.comm = comm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_post,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // sets up the fragments views and calls setthought()
        // default viewsetup when fragment is created
        super.onViewCreated(view, savedInstanceState);
        Button newThoughtButton = (Button) view.findViewById(R.id.new_thought_button);
        newThoughtButton.setOnClickListener(this);
        Button newPictureButton = (Button) view.findViewById(R.id.new_picture_button);
        newPictureButton.setOnClickListener(this);
        Button newLinkButton = (Button) view.findViewById(R.id.new_link_button);
        newLinkButton.setOnClickListener(this);
        ImageButton pictureFolder_button = getView().findViewById(R.id.pictureFolder_Button);
        pictureFolder_button.setOnClickListener(this);
        ImageButton pictureCamera_button = getView().findViewById(R.id.pictureCamera_button);
        pictureCamera_button.setOnClickListener(this);
        this.shareButton = getView().findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);
        Button commLinker = getView().findViewById(R.id.linkCommunity_button);
        commLinker.setOnClickListener(this);
        ImageButton infoButton = getView().findViewById(R.id.newPost_linkCommunity_infoButton);
        infoButton.setOnClickListener(this);

        CarouselView preview_imageView = getView().findViewById(R.id.preview_imageView);
        preview_imageView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Glide.with(getView()).load(R.drawable.default_image).into(imageView);
            }
        });
        preview_imageView.setPageCount(1);
        preview_imageView.setClipToOutline(true);
        this.view = view;
        newThoughtButton.setAlpha(halfAlpha);
        pictureFolder_button.setEnabled(false);
        pictureFolder_button.setAlpha(halfAlpha);
        pictureCamera_button.setEnabled(false);
        pictureCamera_button.setAlpha(halfAlpha);
        TableLayout tLayout = getView().findViewById(R.id.tableLayout);
        tLayout.setClipToOutline(true);
        TableRow tRow = getView().findViewById(R.id.tableRow);
        tRow.setClipToOutline(true);

        EditText editText = getView().findViewById(R.id.title_editText);
        final TextView characterLimitTextView = getView().findViewById(R.id.titleCharacterLimit_textView);
        final int titleCharacterLimit = getContext().getResources().getInteger(R.integer.titleMaximumCharacters);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int characterLeft = (titleCharacterLimit - s.length());
                characterLimitTextView.setText(String.valueOf(characterLeft));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setThought();
        if(this.comm != null && this.new_post_activity != null){
            this.setUpCommunityViews();
        }
    }

    @Override
    public void onClick(View v) {
        // sets up the onClickEvents for the different buttons
        Button newThoughtButton = (Button) view.findViewById(R.id.new_thought_button);
        Button newPictureButton = (Button) view.findViewById(R.id.new_picture_button);
        Button newLinkButton = (Button) view.findViewById(R.id.new_link_button);
        newThoughtButton.setAlpha(fullAlpha);
        newPictureButton.setAlpha(fullAlpha);
        newLinkButton.setAlpha(fullAlpha);

        //Change Community Stuff
        Button commLinker = getView().findViewById(R.id.linkCommunity_button);
        ImageView communityPreview = getView().findViewById(R.id.linkedCommunity_imageView);
        TextView communityPreviewLabel = getView().findViewById(R.id.linkedCommunity_label);
        ImageButton dismissCommunityButton = getView().findViewById(R.id.newPost_dismiss_choosen_community_button);
        ImageView destinationImageView = getView().findViewById(R.id.newPost_destination_imageView);
        TextView destinationLabel = getView().findViewById(R.id.newPost_destination_label);

        switch (v.getId()) {
            case R.id.new_thought_button:
                newThoughtButton.setAlpha(halfAlpha);

                setThought();
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
                break;
            case R.id.new_link_button:
                newLinkButton.setAlpha(halfAlpha);
                showLink();
                break;
            case R.id.pictureFolder_Button:
                new AlertDialog.Builder(getContext())
                        .setTitle("Poste ein oder mehrere Bilder")
                        .setMessage("Halte ein Bild gedrückt, um bis zu drei Bilder auszuwählen. Tippe anschließend auf 'Öffnen'")
                        .setPositiveButton("Mehrere", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chooseMultiplePhotosFromGallery();
                            }
                        })
                        .setNegativeButton("Einzeln", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                choosePhotoFromGallery();
                            }
                        })
                        .show();
                break;
            case R.id.pictureCamera_button:
                takePhotoFromCamera();
                break;
            case R.id.share_button:
                shareTapped();
                break;
            case R.id.linkCommunity_button:
                Intent intent = new Intent(getContext(), CommunityPickActivity.class);
                startActivityForResult(intent,COMMUNITY_PICK);
                break;
            case R.id.newPost_linkCommunity_infoButton:
                new AlertDialog.Builder(getContext())
                        .setMessage("Standardmäßig postest du im Imagine Feed. Poste hier alles, was " +
                                "du mit der Welt teilen möchtest.\n\nWählst du eine Community aus, kannst du entscheiden," +
                                " ob du deinen Beitrag im Imagine Feed teilst oder nur in einer Community. Die Follower einer Community sehen dann deinen Beitrag" +
                                " in ihrem angepassten Imagine-Feed.\nPoste in der Community also alles, was sehr themenspezifisch ist oder nicht für die breite Masse zugänglich ist.")
                        .show();
                break;
            case R.id.newPost_dismiss_choosen_community_button:
                this.linkedFactID = null;
                commLinker.setVisibility(View.VISIBLE);
                communityPreview.setImageBitmap(null);
                communityPreviewLabel.setText(null);
                dismissCommunityButton.setVisibility(View.INVISIBLE);

                Drawable res = getResources().getDrawable(R.drawable.feed_icon, null);
                destinationImageView.setImageDrawable(res);
                destinationLabel.setText("Feed");
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // called when the permission request has been answered
        if ((requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)){
                showPicture();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // is called when the gallery, camera or communitypicker intent return with a result
        super.onActivityResult(requestCode, resultCode, data);
        ImageView communityPreview = getView().findViewById(R.id.linkedCommunity_imageView);
        TextView communityPreviewLabel = getView().findViewById(R.id.linkedCommunity_label);
        ImageButton dismissCommunityButton = getView().findViewById(R.id.newPost_dismiss_choosen_community_button);
        Button chooseCommunityButton = getView().findViewById(R.id.linkCommunity_button);
        CarouselView carouselView = getView().findViewById(R.id.preview_imageView);
        Button new_picture_button = getView().findViewById(R.id.new_picture_button);
        final ImageView destinationImageView = getView().findViewById(R.id.newPost_destination_imageView);
        final TextView destinationLabel = getView().findViewById(R.id.newPost_destination_label);

        communityPreview.setClipToOutline(true);
        switch(requestCode){
            case GALLERY:
                if(data != null){
                    final Uri contentURI = data.getData();
                    try{
                        if(Build.VERSION.SDK_INT <28){
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext()
                                    .getContentResolver(),contentURI);
                            image_inBitmap = bitmap;
                            imageWidth = (float) bitmap.getWidth();
                            imageHeight = (float) bitmap.getHeight();
                            carouselView.setImageListener(new ImageListener() {
                                @Override
                                public void setImageForPosition(int position, ImageView imageView) {
                                    Glide.with(getView()).load(contentURI).into(imageView);
                                }
                            });
                            carouselView.setPageCount(1);
                            new_picture_button.setAlpha(halfAlpha);
                        }else{
                            ImageDecoder.Source source = ImageDecoder.createSource(getContext()
                                    .getContentResolver(),contentURI);
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            image_inBitmap = bitmap;
                            imageWidth = (float) bitmap.getWidth();
                            imageHeight = (float) bitmap.getHeight();
                            carouselView.setImageListener(new ImageListener() {
                                @Override
                                public void setImageForPosition(int position, ImageView imageView) {
                                    Glide.with(getView()).load(contentURI).into(imageView);
                                }
                            });
                            carouselView.setPageCount(1);
                            new_picture_button.setAlpha(halfAlpha);
                        }
                    }catch(Exception e){
                        System.out.println(e.getStackTrace().toString()+" "+TAG);
                    }
                }
                break;
            case IMAGE_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    try {
                        if(Build.VERSION.SDK_INT < 28){
                            image_inBitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            getContext().getContentResolver(), imageUri);
                            carouselView.setImageListener(new ImageListener() {
                                @Override
                                public void setImageForPosition(int position, ImageView imageView) {
                                    Glide.with(getView()).load(imageUri).into(imageView);
                                }
                            });
                            imageWidth = (float) image_inBitmap.getWidth();
                            imageHeight = (float) image_inBitmap.getHeight();
                            carouselView.setPageCount(1);
                            new_picture_button.setAlpha(halfAlpha);
                        }else{
                            ImageDecoder.Source source = ImageDecoder.createSource(getContext()
                                    .getContentResolver(),imageUri);
                            image_inBitmap = ImageDecoder.decodeBitmap(source);
                            carouselView.setImageListener(new ImageListener() {
                                @Override
                                public void setImageForPosition(int position, ImageView imageView) {
                                    Glide.with(getView()).load(imageUri).into(imageView);
                                }
                            });
                            imageWidth = (float) image_inBitmap.getWidth();
                            imageHeight = (float) image_inBitmap.getHeight();
                            carouselView.setPageCount(1);
                            new_picture_button.setAlpha(halfAlpha);
                        }

                    } catch (IOException e) {
                        System.out.println("error! "+TAG);
                    }
                } else {
                    System.out.println("resultcode not OK! "+TAG);
                }
                break;
            case MULTIPLE_IMAGES:
                if(resultCode == getActivity().RESULT_OK){
                    ClipData clipData = data.getClipData();
                    if(clipData != null){   //multiPicture
                        if(clipData.getItemCount()>3){
                            Toast.makeText(getContext(),"Bitte wähle nicht mehr als drei BIlder aus!",
                                    duration).show();
                            return;
                        }
                        this.type = "multiPicture";
                        final Uri[] uris = new Uri[clipData.getItemCount()];
                        for(int i=0; i < clipData.getItemCount(); i++){
                            uris[i] = clipData.getItemAt(i).getUri();
                        }
                        for(int i =0; i<uris.length;i++){
                            Uri uri = uris[i];
                            try{
                                if(Build.VERSION.SDK_INT <28){
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext()
                                            .getContentResolver(),uri);
                                    multi_bitmaps[i] = bitmap;
                                    if(i == 0){
                                        imageWidth = (float) bitmap.getWidth();
                                        imageHeight = (float) bitmap.getHeight();
                                    }
                                }else{
                                    ImageDecoder.Source source = ImageDecoder.createSource(getContext()
                                            .getContentResolver(),uri);
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    multi_bitmaps[i] = bitmap;
                                    if(i == 0){
                                        imageWidth = (float) bitmap.getWidth();
                                        imageHeight = (float) bitmap.getHeight();
                                    }
                                }
                            }catch(Exception e){
                                System.out.println("error! "+TAG);
                            }
                        }
                        carouselView.setImageListener(new ImageListener() {
                            @Override
                            public void setImageForPosition(int position, ImageView imageView) {
                                Glide.with(getView()).load(uris[position]).into(imageView);
                            }
                        });
                        carouselView.setPageCount(uris.length);
                        carouselView.setSlideInterval(2000);
                        carouselView.setPageTransformInterval(800);
                        new_picture_button.setAlpha(halfAlpha);
                    } else {    //picture
                        Toast.makeText(getContext(),"Um ein Bild auszuwählen klicken Sie bitte auf 'Einzeln'!",duration)
                        .show();
                        new_picture_button.setAlpha(halfAlpha);
                    }
                }
                break;
            case COMMUNITY_PICK:
                if(resultCode == getActivity().RESULT_OK){
                    String name = data.getStringExtra("name");
                    String imageURL = data.getStringExtra("imageURL");
                    String commID = data.getStringExtra("commID");
                    this.linkedFactID = commID;

                    dismissCommunityButton.setVisibility(View.VISIBLE);
                    dismissCommunityButton.setOnClickListener(this);
                    chooseCommunityButton.setVisibility(View.INVISIBLE);
                    communityPreviewLabel.setText(name);
                    Glide.with(getView()).load(imageURL).into(communityPreview);

                    new AlertDialog.Builder(getContext())
                            .setTitle("Wo möchtest du posten?")
                            .setMessage("Möchtest du den Beitrag mit allen im Hauptfeed teilen, oder nur in der Community posten?")
                            .setPositiveButton("Mit allen teilen", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //passiert eigentlich nichts?
                                }
                            })
                            .setNegativeButton("Mit der Community teilen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Drawable res = getResources().getDrawable(R.drawable.community_post_icon, null);
                                    destinationImageView.setImageDrawable(res);
                                    destinationLabel.setText("Community");
                                }
                            })
                            .show();
                }
                break;
        }
    }

    public void shareTapped(){
        // is called when the shareButton is clicked
        // checks if all required fields are filled
        // looks what type is set and shares the posts
        shareButton.setEnabled(false);
        EditText title_editText = getView().findViewById(R.id.title_editText);
        EditText link_editText = getView().findViewById(R.id.link_editText);
        CarouselView carousel = getView().findViewById(R.id.preview_imageView);
        FirebaseUser currentUser = auth.getCurrentUser();
        String title = title_editText.getText().toString();
        if(currentUser != null){
            if(title.equals("") || title_editText.getText().equals(null)){
                Toast.makeText(getContext(),"Gib bitte einen Titel ein!",duration).show();
                this.shareButton.setEnabled(true);
            }else{
                DocumentReference docRef;
                if(this.comm != null && this.new_post_activity != null){
                    docRef = db.collection("TopicPosts").document();
                }else{
                    docRef = db.collection("Posts").document();
                }
                System.out.println("Das ist die Post ID:" + docRef.getId()+" "+TAG);
                switch(type){
                    case "picture":
                        if(carousel.getPageCount() == 0 || imageHeight == 0f
                                || imageWidth == 0f){
                            Toast.makeText(getContext(),"Bitte wähle ein Bild aus", duration)
                            .show();
                            this.shareButton.setEnabled(true);
                        }else{
                            loadPictureToFirebase(docRef);
                        }
                        break;
                    case "thought":
                        postThought(docRef);
                        break;
                    case "link":
                        if(link_editText.getText().equals("") || link_editText.getText().equals(null)){
                            Toast.makeText(getContext(), "Gib bitte einen Link ein!", duration).show();
                            this.shareButton.setEnabled(true);
                        } else {
                            String url = link_editText.getText().toString();
                            if(isYouTubeURL(url)){
                                postYouTube(docRef);
                            } else if (isGIFURL(url)) {
                                postGIF(docRef);
                            } else {
                                postLink(docRef);
                            }
                        }
                        break;
                    case "gif":
                        if(link_editText.getText().equals("") || link_editText.getText().equals(null)){
                            Toast.makeText(getContext(), "Gib bitte einen Link ein!", duration).show();
                            this.shareButton.setEnabled(true);
                        } else {
                            postGIF(docRef);
                        }
                        break;
                    case "multiPicture":
                        if(!title_editText.getText().equals("") || title_editText.getText() != null ||
                            this.multi_bitmaps.length >=2 && carousel.getPageCount() >= 2){
                            this.shareButton.setEnabled(true);
                            loadPicturesToFirebase(docRef);
                        } else {
                            Toast.makeText(getContext(),"Bitte geben Sie einen Title an!",duration).show();
                        }
                        break;
                    default:
                        Toast.makeText(getContext(),"Type Fehler!", duration).show();
                        break;
                }
            }
        }else{
            Toast.makeText(getContext(),"Log dich ein um zu Posten.",duration).show();
            this.shareButton.setEnabled(true);
        }
    }

    public void choosePhotoFromGallery(){
        // starts the galleryintent (one picture)
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GALLERY);
    }

    public void chooseMultiplePhotosFromGallery(){
        // starts the gallery intent ( multiple pictures)
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("image/*");
        startActivityForResult(intent,this.MULTIPLE_IMAGES);
    }

    public void setUpCommunityViews(){
        ImageView commPreview = view.findViewById(R.id.linkedCommunity_imageView);
        commPreview.setClipToOutline(true);
        TextView communityPreviewLabel = getView().findViewById(R.id.linkedCommunity_label);
        Button chooseCommunityButton = getView().findViewById(R.id.linkCommunity_button);
        ImageView destinationImageView = getView().findViewById(R.id.newPost_destination_imageView);
        TextView destinationLabel = getView().findViewById(R.id.newPost_destination_label);

        this.linkedFactID = this.comm.topicID;
        chooseCommunityButton.setVisibility(View.INVISIBLE);
        communityPreviewLabel.setText(this.comm.name);
        Drawable res = getResources().getDrawable(R.drawable.community_post_icon, null);
        destinationImageView.setImageDrawable(res);
        destinationLabel.setText("Community");

        if(this.comm.imageURL != null){
            Glide.with(this.view).load(comm.imageURL).into(commPreview);
        }else{
            System.out.println("!");
        }
    }

    public void takePhotoFromCamera(){
        // start the camera intent
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"camera_imagine");
        values.put(MediaStore.Images.Media.DESCRIPTION,
                "Captured with the imagine app!");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI,values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,IMAGE_CAPTURE);
    }


    public void loadPictureToFirebase(final DocumentReference docRef){
        // upload the picture ( one) to Firebase FireStore
        String pathString = docRef.getId()+".png";
        final StorageReference pictureRef = storeRef.child("postPictures").child(pathString);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size = image_inBitmap.getByteCount();
        System.out.println("!");
        int byteCount = image_inBitmap.getByteCount();
        int kByteCount = byteCount /1000;
        if(kByteCount < 50000){
            image_inBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        }else if(kByteCount <100000){
            image_inBitmap.compress(Bitmap.CompressFormat.JPEG,40,baos);
        }else if(kByteCount < 200000){
            image_inBitmap.compress(Bitmap.CompressFormat.JPEG,25,baos);
        }else{
            image_inBitmap.compress(Bitmap.CompressFormat.JPEG,10,baos);
        }

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
                            System.out.println("Hier ist die URL:" + uriString+" "+TAG);
                            postPicture(docRef,uriString);
                        }
                    });

                }else if( task.isCanceled()){
                    System.out.println("upload failed! "+TAG);
                }
            }
        });

    }

    public void loadPicturesToFirebase(final DocumentReference docRef){
        // upload multiple pictures to Firebase Firestore
        if(this.multi_bitmaps.length >= 2 && this.multi_bitmaps.length <= 3){
            final int count = multi_bitmaps.length;
            imageURLS = new ArrayList<>();
            int index = 0;

            for(Bitmap bitmap: multi_bitmaps){
                final StorageReference storageRef = storeRef.child("postPictures").child(docRef.getId()+"-"+index+".png");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                multi_bitmaps[index].compress(Bitmap.CompressFormat.JPEG,10,baos);
                index++;
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        Uri uri = task.getResult();
                                        String uriString = uri.toString();
                                        imageURLS.add(uriString);
                                        if(imageURLS.size() == count){
                                            postMultiPicture(docRef);
                                        }
                                    } else {
                                        System.out.println("getURL canceled!");
                                    }
                                }
                            });
                        } else if(task.isCanceled()){
                            System.out.println("Upload canceled!");
                        }
                    }
                });
            }
        } else {
            Toast.makeText(getContext(),"Bitte wähle mehrere Bilder aus!",duration).show();
        }
    }

    public void postThought(DocumentReference docRef){
        // method to post a thought
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
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
            data.put("report", "normal");
            uploadData(docRef,data);
        }else
            System.out.println("Kein User in PostThought-Methode! "+TAG);
    }
    public void postLink(final DocumentReference docRef){
        // method to post a link
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        EditText link_edit = getView().findViewById(R.id.link_editText);
        final FirebaseUser user = auth.getCurrentUser();
        if( !user.equals(null)){
            final String title = title_edit.getText().toString();
            final String description = description_edit.getText().toString();
            final String link = link_edit.getText().toString();
            RichPreview preview = new RichPreview(new ResponseListener() {
                @Override
                public void onData(MetaData metaData) {
                    String linkImageURL = metaData.getImageurl();
                    String linkShortURL = metaData.getSitename();
                    String linkTitle = metaData.getTitle();
                    String linkDescription = metaData.getDescription();
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("title",title);
                    data.put("description",description);
                    data.put("originalPoster", user.getUid());
                    data.put("createTime", new Timestamp(new Date()));
                    data.put("thanksCount",new Integer(0));
                    data.put("wowCount", new Integer(0));
                    data.put("haCount", new Integer(0));
                    data.put("niceCount", new Integer(0));
                    data.put("type", "link");
                    data.put("report", "normal");
                    data.put("link",link);
                    if(linkImageURL != null) data.put("linkImageURL",metaData.getImageurl());
                    if(linkShortURL != null) data.put("linkShortURL",metaData.getSitename());
                    if(linkDescription != null) data.put("linkDescription",metaData.getDescription());
                    if(linkTitle != null) data.put("linkTitle",metaData.getTitle());
                    uploadData(docRef,data);
                }

                @Override
                public void onError(Exception e) {
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("title",title);
                    data.put("description",description);
                    data.put("originalPoster", user.getUid());
                    data.put("createTime", new Timestamp(new Date()));
                    data.put("thanksCount",new Integer(0));
                    data.put("wowCount", new Integer(0));
                    data.put("haCount", new Integer(0));
                    data.put("niceCount", new Integer(0));
                    data.put("type", "link");
                    data.put("report", "normal");
                    data.put("link",link);
                    uploadData(docRef,data);
                }
            });
            preview.getPreview(link);
        }else
            System.out.println("Kein User in PostLink-Methode! "+TAG);
    }

    public void postGIF(DocumentReference docRef){
        // method to post a GIF
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        EditText link_edit = getView().findViewById(R.id.link_editText);
        FirebaseUser user = auth.getCurrentUser();
        String link = link_edit.getText().toString();
        if (user != null) {
         if (link.contains(".mp4")){
                String title = title_edit.getText().toString();
                String description = description_edit.getText().toString();
                HashMap<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("description", description);
                data.put("originalPoster", user.getUid());
                data.put("createTime", new Timestamp(new Date()));
                data.put("thanksCount", new Integer(0));
                data.put("wowCount", new Integer(0));
                data.put("haCount", new Integer(0));
                data.put("niceCount", new Integer(0));
                data.put("type", "GIF");
                data.put("report", "normal");
                data.put("link", link);
                uploadData(docRef, data);
            } else {
             Toast.makeText(getContext(), "Im Moment unterstützen wir leider nur Links im .mp4 Format.", Toast.LENGTH_SHORT).show();
             this.shareButton.setEnabled(true);
             }
        } else {
            System.out.println("Kein User in PostYouTUbe-Methode! "+TAG);
        }
    }

    public void postYouTube(DocumentReference docRef){
        // method to post a YouTubeVIdeo
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
            data.put("createTime", new Timestamp(new Date()));
            data.put("thanksCount",new Integer(0));
            data.put("wowCount", new Integer(0));
            data.put("haCount", new Integer(0));
            data.put("niceCount", new Integer(0));
            data.put("type", "youTubeVideo");
            data.put("report", "normal");
            data.put("link",link);
            uploadData(docRef,data);
        }else
            System.out.println("Kein User in PostYouTUbe-Methode! "+TAG);
    }

    public void postPicture(DocumentReference docRef, String url){
        // method to post a Picture
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        System.out.println("Das ist die imageURL: "+url+" "+TAG);
        FirebaseUser user = auth.getCurrentUser();
        if(!user.equals(null)){
            String title = title_edit.getText().toString();
            String description = description_edit.getText().toString();
            HashMap<String,Object> data = new HashMap<>();
            data.put("title",title);
            data.put("description",description);
            data.put("originalPoster", user.getUid());
            data.put("createTime", new Timestamp(new Date()));
            data.put("thanksCount",new Integer(0));
            data.put("wowCount", new Integer(0));
            data.put("haCount", new Integer(0));
            data.put("niceCount", new Integer(0));
            data.put("type", "picture");
            data.put("report", "normal");
            data.put("imageURL", url);
            data.put("imageHeight", new Float(imageHeight));
            data.put("imageWidth", new Float(imageWidth));
            uploadData(docRef,data);
        }else
            System.out.println("Kein User in postPicture-Methode! "+TAG);
    }

    public void postMultiPicture(DocumentReference documentReference){
        // method to post a multiPicturePost
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        FirebaseUser user = auth.getCurrentUser();
        if(!user.equals(null)){
            String title = title_edit.getText().toString();
            String description = description_edit.getText().toString();
            HashMap<String,Object> data = new HashMap<>();
            data.put("title",title);
            data.put("description",description);
            data.put("originalPoster", user.getUid());
            data.put("createTime", new Timestamp(new Date()));
            data.put("thanksCount",new Integer(0));
            data.put("wowCount", new Integer(0));
            data.put("haCount", new Integer(0));
            data.put("niceCount", new Integer(0));
            data.put("type", "multiPicture");
            data.put("report", "normal");
            data.put("imageURLs", this.imageURLS);
            data.put("imageHeight", new Float(imageHeight));
            data.put("imageWidth", new Float(imageWidth));
            uploadData(documentReference,data);
        }else
            System.out.println("Kein User in postPicture-Methode! "+TAG);
    }

    public void uploadData(final DocumentReference docRef, HashMap<String,Object> data){
        // uploads the data to the DocumentReference docRef
        if(linkedFactID != null){
            data.put("linkedFactID",linkedFactID);
        }
        FirebaseUser user = auth.getCurrentUser();
        List notificationRecipients = new ArrayList<>();
        notificationRecipients.add(user.getUid());

        data.put("notificationRecipients", notificationRecipients); // So the ios User send notifications

        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if (linkedFactID != null) {
                        uploadCommunityData(docRef, linkedFactID);
                    }
                    if(comm != null && new_post_activity != null){
                        uploadCommunityData(docRef, comm.topicID);
                    }
                    uploadUserData(docRef);
                    System.out.println("Post erfolgreich erstellt!");
                    postedSuccessful();
                } else if(task.isCanceled())
                    System.out.println("Post Erstellung fehlgeschlagen!");
            }
        });
    }

    public void uploadCommunityData(DocumentReference docRef, String linkedFactID) {
        // uploads the posts data to the communityDocument if it is shared inside a
        // community_topic
        String documentID = docRef.getId();
        DocumentReference communityRef = db.collection("Facts").document(linkedFactID).collection("posts").document(documentID);
        Timestamp timestamp = new Timestamp(new Date());
        HashMap<String,Object> data = new HashMap<>();
        data.put("createTime",timestamp);
        if(this.comm != null && this.new_post_activity != null){
            data.put("type", "topicPost");
        }
        communityRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Community erfolgreich gelinked! "+TAG);
                } else if(task.isCanceled()) {
                    System.out.println("Community linken fehlgeschlagen! "+TAG);
                }
            }
        });
    }

    public void uploadUserData(DocumentReference docRef) {
        // upload the post data to the user document
        String documentID = docRef.getId();
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference userRef = db.collection("Users").document(user.getUid())
                .collection("posts").document(documentID);
        Timestamp timestamp = new Timestamp(new Date());
        HashMap<String,Object> data = new HashMap<>();
        data.put("createTime",timestamp);
        if(this.comm != null && this.new_post_activity != null){
            data.put("type", "topicPost");
        }
        userRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Community erfolgreich gelinked! "+TAG);
                } else if(task.isCanceled()) {
                    System.out.println("Community linken fehlgeschlagen! "+TAG);
                }
            }
        });
    }

    public void postedSuccessful(){
        // called when the posting process is successfull
        EditText title_edit = getView().findViewById(R.id.title_editText);
        EditText description_edit = getView().findViewById(R.id.description_editText);
        EditText link_edit = getView().findViewById(R.id.link_editText);
        final CarouselView preview_imageView = getView().findViewById(R.id.preview_imageView);
        title_edit.getText().clear();
        description_edit.getText().clear();
        link_edit.getText().clear();
        preview_imageView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Glide.with(getView()).load(R.drawable.default_image).into(imageView);
            }
        });
        preview_imageView.setPageCount(1);
        this.shareButton.setEnabled(true);
    }
    public void setThought(){
        this.type = "thought";
        hidePicture();
        hideLink();
    }

    public void hidePicture(){
        // hides the picutrefolder and picturecamerabutton
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
        // hides the link label and edittext
        TextView link_label = getView().findViewById(R.id.link_label);
        EditText link_editText = getView().findViewById(R.id.link_editText);
        link_label.setAlpha(halfAlpha);
        link_editText.setEnabled(false);
    }

    public void showGIF(){
        // sets up GIFViews and changes Type
        this.type = "gif";
        hidePicture();
        TextView link_label = getView().findViewById(R.id.link_label);
        EditText link_editText = getView().findViewById(R.id.link_editText);
        link_editText.setEnabled(true);
        link_label.setAlpha(fullAlpha);
    }

    public void showPicture(){
        // sets up pictureViews and changes type
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
        // sets up linkViews and changes type
        this.type = "link";
        hidePicture();
        TextView link_label = getView().findViewById(R.id.link_label);
        EditText link_editText = getView().findViewById(R.id.link_editText);
        link_editText.setEnabled(true);
        link_label.setAlpha(fullAlpha);
    }

    public boolean isYouTubeURL(String youTubeURL){
        // checks if url is valid youtube url
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (!youTubeURL.isEmpty() && youTubeURL.matches(pattern))
        {
            success = true;
        }
        else
        {
            success = false;
        }
        return success;
    }

    public boolean isGIFURL(String url){
        // checks if url is valid .mp4
        boolean success;

        if (url.endsWith(".mp4")) {
            success = true;
        } else {
            success = false;
        }
        return success;
    }

}

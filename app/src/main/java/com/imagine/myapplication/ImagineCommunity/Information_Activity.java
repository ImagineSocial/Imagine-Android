package com.imagine.myapplication.ImagineCommunity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Any;
import com.imagine.myapplication.R;

import java.util.Date;
import java.util.HashMap;

public class Information_Activity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Information_Fragment";
    public FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_and_impressum_activity);
        ImageButton toGDPRButton = findViewById(R.id.toGDPRButton);
        toGDPRButton.setOnClickListener(this);
        Button toWebsiteButton = findViewById(R.id.goToWebsiteButton);
        toWebsiteButton.setOnClickListener(this);
        Button deleteAccountButton = findViewById(R.id.delete_account_button);
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            deleteAccountButton.setAlpha(1);
            deleteAccountButton.setOnClickListener(this);
            deleteAccountButton.setEnabled(true);
        }
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.setting_and_impressum_activity,container, false);
//    }


//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }

    @Override
    public void onClick(View v) {
        // setting up the onClick events
        Intent intent = new Intent(Intent.ACTION_VIEW);

        switch (v.getId()) {
            case R.id.toGDPRButton:
                String gdprURL = "https://www.imagine.social/datenschutzerklärung-app";
                intent.setData(Uri.parse(gdprURL));
                this.startActivity(intent);
                break;
            case R.id.goToWebsiteButton:
                String url = "https://imagine.social";
                intent.setData(Uri.parse(url));
                this.startActivity(intent);
                break;
            case R.id.delete_account_button:
                sendDeleteRequest();
                break;
        }
    }

    public void sendDeleteRequest() {
        // sending delete request into MaltesDB
        final Button deleteAccountButton = findViewById(R.id.delete_account_button);
        deleteAccountButton.setAlpha((float) 0.5);
        deleteAccountButton.setEnabled(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference malteRef = db.collection("Users")
                .document("CZOcL3VIwMemWwEfutKXGAfdlLy1")
                .collection("notifications").document();
        Date date = new Date();
        Timestamp stamp = new Timestamp(date);
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("type","message");
        dataMap.put("message","Jemand möchte seinen Account löschen");
        dataMap.put("chatID","egal");
        dataMap.put("sentAt", stamp);
        dataMap.put("name", "System");
        dataMap.put("UserID", user.getUid());
        final Context context = this;
        malteRef.set(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("OLLOLO!!");

                    Toast.makeText(context,"Dein Account wird innerhalb von 48h gelöscht." +
                            " Logge dich aus, wir übernehmen den Rest.",Toast.LENGTH_LONG).show();
                } else{
                    System.out.println("upload to database failed! "+TAG);
                    deleteAccountButton.setAlpha(1);
                    deleteAccountButton.setEnabled(true);
                }
            }
        });

    }
}

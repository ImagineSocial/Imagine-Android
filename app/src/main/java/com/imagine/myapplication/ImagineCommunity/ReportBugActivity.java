package com.imagine.myapplication.ImagineCommunity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.R;

import java.util.Date;
import java.util.HashMap;

public class ReportBugActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_bug_activity);

        final Button sendButton = findViewById(R.id.bug_report_send_button);
        final EditText input = findViewById(R.id.bug_report_editText);

        mContext = this;

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String text = input.getText().toString();
                if (user != null) {
                    if (!text.equals("")) {
                        sendButton.setEnabled(false);
                        sendBugReport(text);
                    } else {
                        Toast.makeText(mContext, "We need more Information", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "You need to be logged in to report a bug to prevent spam.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendBugReport(final String text) {
        DocumentReference bugRef = db.collection("Feedback")
                .document("bugs").collection("bugs").document();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String,Object> data = new HashMap<>();
        data.put("description", text);
        data.put("createTime",new Timestamp(new Date()));
        data.put("OP", user.getUid());
        data.put("Device", "Android");

        final Button sendButton = findViewById(R.id.bug_report_send_button);
        final EditText editText = findViewById(R.id.bug_report_editText);

        bugRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    notifyMalte(text);
                    new AlertDialog.Builder(mContext)
                            .setTitle("Thank you for your input!")
                            .setMessage("Together we will build the future!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    editText.setText("");

                                    sendButton.setEnabled(true);
                                }
                            })
                            .show();
                    System.out.println("Successfully added proposal");
                }else{
                    sendButton.setEnabled(true);
                    Toast.makeText(mContext, "Something went wrong, please try later again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void notifyMalte(String text) {
        //Upload a notification to malte´s user document!
        DocumentReference malteRef = db.collection("Users")
                .document("CZOcL3VIwMemWwEfutKXGAfdlLy1")
                .collection("notifications").document();

        Date date = new Date();
        Timestamp stamp = new Timestamp(date);

        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("type","message");
        dataMap.put("message","We got an android bug"+text);
        dataMap.put("chatID","egal");
        dataMap.put("sentAt", stamp);
        dataMap.put("messageID","auch egal");

        malteRef.set(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Uploading user data to Maltes database was successfull! ");
                } else{
                    System.out.println("Uploading user data to Maltes database failed! ");
                }
            }
        });
    }

}

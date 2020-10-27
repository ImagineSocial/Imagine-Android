package com.imagine.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.post_classes.Post;

import java.util.HashMap;
import java.util.Locale;

public class ReportDialogFragment extends AlertDialog {

    public Post post;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public String reason = "";
    public EditText description;
    public Context mContext;

    public ReportDialogFragment(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_dialog_fragment);
        RadioButton hate = findViewById(R.id.hate_radio);
        RadioButton racism = findViewById(R.id.racism_radio);
        RadioButton spam = findViewById(R.id.spam_radio);
        RadioButton fake = findViewById(R.id.fake_radio);
        RadioButton unap = findViewById(R.id.unap_radio);
        RadioButton other = findViewById(R.id.else_radio);
        this.description = findViewById(R.id.report_reason);
        Button reportButton = findViewById(R.id.report_button);

        assert hate != null;
        hate.setOnClickListener(listener);
        assert racism != null;
        racism.setOnClickListener(listener);
        assert spam != null;
        spam.setOnClickListener(listener);
        assert fake != null;
        fake.setOnClickListener(listener);
        assert unap != null;
        unap.setOnClickListener(listener);
        assert other != null;
        other.setOnClickListener(listener);
        assert reportButton != null;
        reportButton.setOnClickListener(listener);

        this.mContext = getContext();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch(id){
                case R.id.hate_radio:
                    reason = "hate";
                    break;
                case R.id.racism_radio:
                    reason = "racism";
                    break;
                case R.id.spam_radio:
                    reason = "spam";
                    break;
                case R.id.fake_radio:
                    reason = "fake";
                    break;
                case R.id.unap_radio:
                    reason = "unap";
                    break;
                case R.id.else_radio:
                    reason = "other";
                    break;
                case R.id.report_button:
                    if(!reason.equals("")){
                        if(auth.getCurrentUser() == null){
                            Toast.makeText(getContext(),getContext().getResources().getString(R.string.report_dialog_fragment_login),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }else{
                            upLoadReportData();
                        }
                    }else{
                        Toast.makeText(getContext(),getContext().getResources().getString(R.string.report_dialog_fragment_toast),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void upLoadReportData(){
        Configuration conf = MainActivity.configContext.getResources().getConfiguration();
        final Locale locale = conf.locale;
        DocumentReference docRef = db.collection("Reports").document();
        HashMap<String,Object> data = new HashMap<>();
        data.put("language",locale.getLanguage());
        data.put("reportedPost",post.documentID);
        data.put("reportingUser",auth.getCurrentUser().getUid());
        data.put("description",description.getText().toString());
        data.put("reason",reason);
        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.report_dialog_fragment_succ)
                        ,Toast.LENGTH_SHORT).show();
                    if(post.isTopicPost){
                        switch (locale.getLanguage()){
                            case "de":
                                db.collection("TopicPosts").document(post.documentID).update("report","blocked");
                                break;
                            case "en":
                                db.collection("Data").document("en").collection("topicPosts").
                                        document(post.documentID).update("report","blocked");
                                break;
                            default:
                                db.collection("Data").document("en").collection("topicPosts").
                                        document(post.documentID).update("report","blocked");
                                break;
                        }
                        dismiss();
                    }else{
                        switch (locale.getLanguage()){
                            case "de":
                                db.collection("Posts").document(post.documentID).update("report","blocked");
                                break;
                            case "en":
                                db.collection("Data").document("en").collection("posts").
                                        document(post.documentID).update("report","blocked");
                                break;
                            default:
                                db.collection("Data").document("en").collection("posts").
                                        document(post.documentID).update("report","blocked");
                                break;
                        }
                        dismiss();
                    }
                }else{
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.report_dialog_fragment_fail)
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

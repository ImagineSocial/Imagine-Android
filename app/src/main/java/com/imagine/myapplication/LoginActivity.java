package com.imagine.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.datatype.Duration;

import io.opencensus.metrics.LongGauge;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public Context mContext = this;
    public float halfAlpha = 0.5f;
    public float fullAlpha = 1f;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseUser user;
    public boolean login = true;
    public int duration = Toast.LENGTH_SHORT;
    public EditText surname_ed;
    public TextView surname_label;
    public TextView repeatPasswort_label;
    public EditText repeatPassword_ed;
    public TextView name_label;
    public EditText name_ed;
    public EditText email_ed;
    public EditText password_ed;
    public Button login_button;
    public Button login_checked;
    public Button signup_checked;
    public CheckBox gdpr_checkbox;
    public Button gdpr_button;
    public TextView surname_infoLabel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Setting up the views!
        surname_ed = findViewById(R.id.surname_editText);
        surname_label = findViewById(R.id.surname_label);
        repeatPasswort_label = findViewById(R.id.repeatPassword_label);
        repeatPassword_ed = findViewById(R.id.repeatPassword_editText);
        name_label = findViewById(R.id.name_label);
        name_ed = findViewById(R.id.name_editText);
        email_ed = findViewById(R.id.email_editText);
        password_ed = findViewById(R.id.password_editText);
        login_button = findViewById(R.id.toolbarLoginButton);
        login_checked = findViewById(R.id.login_checked);
        signup_checked = findViewById(R.id.signup_checked);
        gdpr_checkbox = findViewById(R.id.GDPR_checkbox);
        gdpr_button = findViewById(R.id.gdpr_button);
        surname_infoLabel = findViewById(R.id.surname_info_label);
        //Setting up the onClickListeners
        setLogin();
        login_button.setText(getResources().getString(R.string.login_activity_login));
        login_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogin();
                login_button.setText(getResources().getString(R.string.login_activity_login));
            }
        });

        signup_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSignUp();
                login_button.setText(getResources().getString(R.string.login_activity_signup));
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login){
                    tryToLogin();
                } else {
                    tryToSignUp();
                }
            }
        });

        gdpr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String gdprURL = "https://donmalte.github.io";
                intent.setData(Uri.parse(gdprURL));
                mContext.startActivity(intent);
            }
        });
    }

    public void setLogin(){
        login = true;
        login_checked.setAlpha(halfAlpha);
        signup_checked.setAlpha(fullAlpha);
        name_ed.setVisibility(View.INVISIBLE);
        name_label.setVisibility(View.INVISIBLE);
        surname_ed.setVisibility(View.INVISIBLE);
        surname_label.setVisibility(View.INVISIBLE);
        repeatPassword_ed.setVisibility(View.INVISIBLE);
        repeatPasswort_label.setVisibility(View.INVISIBLE);
        gdpr_button.setVisibility(View.INVISIBLE);
        gdpr_checkbox.setVisibility(View.INVISIBLE);
        surname_infoLabel.setVisibility(View.INVISIBLE);
    }

    public void setSignUp(){
        login = false;
        signup_checked.setAlpha(halfAlpha);
        login_checked.setAlpha(fullAlpha);
        name_ed.setVisibility(View.VISIBLE);
        name_label.setVisibility(View.VISIBLE);
        surname_ed.setVisibility(View.VISIBLE);
        surname_label.setVisibility(View.VISIBLE);
        repeatPasswort_label.setVisibility(View.VISIBLE);
        repeatPassword_ed.setVisibility(View.VISIBLE);
        gdpr_button.setVisibility(View.VISIBLE);
        gdpr_checkbox.setVisibility(View.VISIBLE);
        surname_infoLabel.setVisibility(View.VISIBLE);
    }

    public void tryToLogin(){
        // Checking for required fields
        Editable password = password_ed.getText();
        if((password.toString() == "") || email_ed.getText().toString().equals("")){
            Toast.makeText(this,getResources().getString(R.string.login_activity_mailpass),duration).show();
        } else {
            startLogin();
        }
    }

    public void startLogin(){
        //Starting login succes ---> finish()
        String email = email_ed.getText().toString();
        String password = password_ed.getText().toString();
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user = authResult.getUser();
                System.out.println("!");
                MainActivity.resetFragmentsAfterLogin();
                LoginActivity.super.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(mContext,getResources().getString(R.string.login_activity_loginfail)+error,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void tryToSignUp(){
        //Checking for required fields
        if (gdpr_checkbox.isChecked()) {
            if (name_ed.getText().toString().equals("") || surname_ed.getText().toString().equals("")
                    || password_ed.getText().toString().equals("") || repeatPassword_ed.getText().toString().equals("")
                    || email_ed.getText().toString().equals("")) {
                Toast.makeText(mContext, getResources().getString(R.string.login_activty_fields), duration).show();

            } else {
                if (password_ed.getText().toString().equals(repeatPassword_ed.getText().toString())) {
                    startSignUp();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.login_activty_pass_veri), duration).show();
                }
            }
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.login_activity_data), duration).show();
        }
    }

    public void startSignUp(){
        // CreateUser success --> load User Data to Database and Auth
        login_button.setEnabled(false);
        String email = email_ed.getText().toString();
        String password = password_ed.getText().toString();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            AuthResult result = task.getResult();
                            user = result.getUser();
                            if(user != null){
                                loadUserToDataBase(user);
                                changeUsersAuthData(user);
                            }
                        }else{
                            Exception e = task.getException();
                            if(e instanceof FirebaseAuthWeakPasswordException){
                                Toast.makeText(mContext,getResources().getString(R.string.login_activity_fail_pass),duration).show();
                                login_button.setEnabled(true);
                            }
                            if( e instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(mContext,getResources().getString(R.string.login_activity_fail_mal),duration).show();
                                login_button.setEnabled(true);
                            }
                            if( e instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(mContext,getResources().getString(R.string.login_activity_fail_used),duration).show();
                                login_button.setEnabled(true);
                            }
                        }
                    }
                });
    }

    public void changeUsersAuthData(FirebaseUser user){
        // Send VerificationEmail and upDate the UserAuthProfile!
        String name = name_ed.getText().toString();
        String surname = surname_ed.getText().toString();
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        UserProfileChangeRequest request = builder.setDisplayName(name+" "+surname).build();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Verfication Email send! "+ TAG);
                } else{
                    System.out.println("Verification Email not send! "+TAG);
                }
            }
        });

        user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("ChangeRequest successfull! "+TAG);
                } else {
                    System.out.println("ChangeRequest failed!"+TAG);
                }
            }
        });
    }

    public void loadUserToDataBase(FirebaseUser user){
        // Upload user data to firebase collection "Users"
        //DocumentID == user.getUid()
        uploadToMaltesDatabase();
        DocumentReference userRef = db.collection("Users").document(user.getUid());
        String name = name_ed.getText().toString();
        String surname = surname_ed.getText().toString();
        Date date = new Date();
        Timestamp stamp = new Timestamp(date);
        ArrayList<String> array = new ArrayList<>();
        array.add("first500");
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("surname", surname);
        dataMap.put("fullname", name+" "+surname);
        dataMap.put("createDate", stamp);
        dataMap.put("badges",array);
        userRef.set(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Uploading user data sucessfull! "+TAG);
                    dismissAfterSuccess();
                } else  {
                    System.out.println("Uploading user data failed! "+TAG);
                }
            }
        });
    }

    public void uploadToMaltesDatabase(){
        //Upload a notification to malte´s user document!
        DocumentReference malteRef = db.collection("Users")
                .document("CZOcL3VIwMemWwEfutKXGAfdlLy1")
                .collection("notifications").document();
        String name = name_ed.getText().toString();
        Date date = new Date();
        Timestamp stamp = new Timestamp(date);

        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("type","message");
        dataMap.put("message","Hello Malte MatzS here! New User: "+name);
        dataMap.put("chatID","egal");
        dataMap.put("sentAt", stamp);
        dataMap.put("messageID","auch egal");

        malteRef.set(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Uploading user data to Maltes database was successfull! "+TAG);
                } else{
                    System.out.println("Uploading user data to Maltes database failed! "+TAG);
                }
            }
        });

    }

    public void dismissAfterSuccess(){
        // Finishing activity why no super.finish()?
        Toast.makeText(getBaseContext(),getResources().getString(R.string.login_activity_welcome), Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }
}

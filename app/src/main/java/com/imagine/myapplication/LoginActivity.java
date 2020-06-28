package com.imagine.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        surname_ed = findViewById(R.id.surname_editText);
        surname_label = findViewById(R.id.surname_label);
        repeatPasswort_label = findViewById(R.id.repeatPassword_label);
        repeatPassword_ed = findViewById(R.id.repeatPassword_editText);
        name_label = findViewById(R.id.name_label);
        name_ed = findViewById(R.id.name_editText);
        email_ed = findViewById(R.id.email_editText);
        password_ed = findViewById(R.id.password_editText);
        login_button = findViewById(R.id.login_button);
        login_checked = findViewById(R.id.login_checked);
        signup_checked = findViewById(R.id.signup_checked);
        setLogin();
        login_button.setText("Login");
        login_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogin();
                login_button.setText("Login");
            }
        });

        signup_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSignUp();
                login_button.setText("Sign up");
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
    }

    public void setLogin(){
        login_checked.setAlpha(fullAlpha);
        signup_checked.setAlpha(halfAlpha);
        login = true;
        name_ed.setVisibility(View.INVISIBLE);
        name_label.setVisibility(View.INVISIBLE);
        surname_ed.setVisibility(View.INVISIBLE);
        surname_label.setVisibility(View.INVISIBLE);
        repeatPassword_ed.setVisibility(View.INVISIBLE);
        repeatPasswort_label.setVisibility(View.INVISIBLE);
    }

    public void setSignUp(){
        signup_checked.setAlpha(fullAlpha);
        login_checked.setAlpha(halfAlpha);

        login = false;

        name_ed.setVisibility(View.VISIBLE);
        name_label.setVisibility(View.VISIBLE);
        surname_ed.setVisibility(View.VISIBLE);
        surname_label.setVisibility(View.VISIBLE);
        repeatPasswort_label.setVisibility(View.VISIBLE);
        repeatPassword_ed.setVisibility(View.VISIBLE);
    }

    public void tryToLogin(){
        if(repeatPassword_ed.getText().equals("") || email_ed.getText().equals("")){
            Toast.makeText(this,"Bitte Email und Passwort eingeben",duration).show();
        } else {
            startLogin();
        }
    }

    public void startLogin(){
        String email = email_ed.getText().toString();
        String password = password_ed.getText().toString();

        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user = authResult.getUser();
                System.out.println("!");
                LoginActivity.super.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(mContext,"Login failed! "+error,duration).show();
            }
        });
    }

    public void tryToSignUp(){
        if(name_ed.getText().toString().equals("") || surname_ed.getText().toString().equals("")
            || password_ed.getText().toString().equals("") || repeatPassword_ed.getText().toString().equals("")
            || email_ed.getText().toString().equals("")){
            Toast.makeText(mContext, "Bitte alle Felder ausfüllen!", duration).show();

        } else  {
            if(password_ed.getText().toString() != repeatPassword_ed.getText().toString()){
                Toast.makeText(mContext, "Passwörter stimmen nicht überein!", duration).show();
            } else {
                startSignUp();
            }
        }
    }

    public void startSignUp(){
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
                    Toast.makeText(mContext,"Sign up failed!",duration).show();
                }
            }
        });
    }

    public void changeUsersAuthData(FirebaseUser user){
        String name = name_ed.getText().toString();
        String surname = surname_ed.getText().toString();

        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        UserProfileChangeRequest request = builder.setDisplayName(name+" "+surname).build();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("EMAIL GESENDET!");
                } else{
                    System.out.println("EMAIL NICHT GESENDET!");
                }
            }
        });

        user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("ChangeRequest SUCC!!");
                } else {
                    System.out.println("ChangeRequest NO SUCC!");
                }
            }
        });
    }

    public void loadUserToDataBase(FirebaseUser user){
        uploadToMaltesDatabase();

        DocumentReference userRef = db.collection("Users").document(user.getUid());
        String name = name_ed.getText().toString();
        String surname = surname_ed.getText().toString();
        Date date = new Date();
        Timestamp stamp = new Timestamp(date);
        String[] array = new String[1];
        array[0] = "first500";

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
                    System.out.println("SUCC NEW USER!!");
                    dismissAfterSuccess();
                } else  {
                    System.out.println("NO SUCC NO USER!");
                }
            }
        });
    }

    public void uploadToMaltesDatabase(){
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
                    System.out.println("OLLOLO!!");
                } else{
                    System.out.println("HOIHOIHO!!");
                }
            }
        });

    }

    public void dismissAfterSuccess(){
        Toast.makeText(getBaseContext(),"Willkommen bei Imagine",duration).show();
        super.onBackPressed();
    }
}

package com.imagine.myapplication.Community;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.R;

import org.w3c.dom.Document;

import java.util.HashMap;

public class ArgumentsDialogFragment extends androidx.fragment.app.DialogFragment {

    public EditText title;
    public EditText description;
    public Button addButton;
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public String proOrCon;
    public String commID;
    public Context mContext;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CommunityFactsFragment facts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_argument_dialog_fragment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.title = view.findViewById(R.id.newArgument_title_editText);
        this.description = view.findViewById(R.id.newArgument_description_editText);
        this.addButton = view.findViewById(R.id.newArgument_createButton);
        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArgumentButton();
            }
        });
        mContext = view.getContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public void addArgumentButton(){
        if(!title.getText().toString().equals("") && !description.getText().toString().equals("")){
            if(auth.getCurrentUser() != null){
                this.addArgument();
            }else{
                Toast.makeText(getContext(),getResources().getString(R.string.arguments_dialog_fragment_toast_no_user),
                        Toast.LENGTH_SHORT);
            }
        }else{
            Toast.makeText(getView().getContext(),getResources().getString(R.string.arguments_dialog_fragment_toast_fields_empty)
                    ,Toast.LENGTH_SHORT).show();
        }
    }

    public void addArgument(){
        DocumentReference docRef = db.collection("Facts").document(commID)
                .collection("arguments").document();
        HashMap<String, Object> data = new HashMap<>();
        data.put("OP", auth.getCurrentUser().getUid());
        data.put("description",description.getText().toString());
        data.put("downvotes",0);
        data.put("upvotes",0);
        data.put("title", title.getText().toString());
        data.put("proOrContra",proOrCon);
        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    facts.removeDialogFragment();
                }else{
                    Toast.makeText(mContext,getResources().getString(R.string.arguments_dialog_fragment_toast_argument_fail),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package com.imagine.myapplication.ImagineCommunity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.imagine.myapplication.Comment;
import com.imagine.myapplication.CommentsCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProposalActivity extends AppCompatActivity {

    Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proposal_activity);
        this.mContext = this;

        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //TO tell the textinput view to shut up when starting
        getProposals();

        Button sendButton = findViewById(R.id.proposal_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    sendTapped();
                } else {
                    Toast.makeText(mContext,"Please log in to make a proposal.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getProposals(){
        // fetches the comments for the postActivitys
        final ArrayList<Proposal> proposalArray = new ArrayList<>();

        Query proposalRef = db.collection("Campaigns").orderBy("supporter", Query.Direction.DESCENDING);
        proposalRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> docsList = querySnapshot.getDocuments();

                    for (DocumentSnapshot docSnap: docsList) {
                        final Proposal proposal = new Proposal();
                        proposal.title = docSnap.getString("title");
                        proposal.description = docSnap.getString("description");
                        proposal.summary = docSnap.getString("summary");
                        Timestamp sentAt = docSnap.getTimestamp("createTime");
                        proposal.OP = docSnap.getString("OP");
                        Double supporter = docSnap.getDouble("supporter");
                        Double opposition = docSnap.getDouble("opposition");
                        proposal.supporter = (int) supporter.intValue();
                        proposal.opposition = (int) opposition.intValue();

                        Long timeNow = new Date().getTime();
//                        String dateString = convertLongDateToAgoString(sentAt.toDate(),timeNow);

                        proposalArray.add(proposal);
                    }

                    System.out.println("###fodnenendn"+proposalArray);
                    setRecyclerView(proposalArray);
                }else if(task.isCanceled()){
                    System.out.println("Task Failed!");
                }
            }
        });
    }

    public void setRecyclerView(ArrayList<Proposal> proposals) {
        RecyclerView recyclerView = findViewById(R.id.proposal_recyclerView);
        ProposalAdapter adapter = new ProposalAdapter(proposals, this);
        recyclerView.setAdapter(adapter);
    }

    public void sendTapped() {
        EditText titleText = findViewById(R.id.proposal_title_textEdit);
        EditText summaryText = findViewById(R.id.proposal_summary_textEdit);
        EditText descriptionText = findViewById(R.id.proposal_idea_textEdit);

        String title = titleText.getText().toString();
        String summary = summaryText.getText().toString();
        String description = descriptionText.getText().toString();

        if (!title.equals("") && !summary.equals("") && !description.equals("")) {
            uploadToFirebase(title, summary, description);
        } else {
            Toast.makeText(mContext,"We need more Information", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadToFirebase(String title, String summary, String description) {
        DocumentReference proposalRef = db.collection("Campaigns").document();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String,Object> data = new HashMap<>();
        data.put("title",title);
        data.put("summary", summary);
        data.put("description", description);
        data.put("createTime",new Timestamp(new Date()));
        data.put("OP", user.getUid());
        data.put("category", "proposal");
        data.put("type", "normal");
        data.put("supporter", 0);
        data.put("opposition", 0);


        proposalRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Successfully added proposal");
                }else{
                    System.out.println("Trying to add proposal was not successfull: ");
                }
            }
        });

    }

}

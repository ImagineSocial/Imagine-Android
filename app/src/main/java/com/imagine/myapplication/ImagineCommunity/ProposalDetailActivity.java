package com.imagine.myapplication.ImagineCommunity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.imagine.myapplication.R;

public class ProposalDetailActivity extends AppCompatActivity {

    public static Proposal proposal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proposal_detail_activity);

        TextView title = findViewById(R.id.proposal_detail_title);
        TextView summary = findViewById(R.id.proposal_detail_summary);
        TextView description = findViewById(R.id.proposal_detail_description);
        title.setText(proposal.title);
        summary.setText(proposal.summary);
        description.setText(proposal.description);
    }
}

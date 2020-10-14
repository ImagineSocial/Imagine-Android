package com.imagine.myapplication.ImagineCommunity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Community.Addon_Feed_Activity;
import com.imagine.myapplication.R;

public class ProposalViewHolder extends RecyclerView.ViewHolder {

    public ProposalViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(final Proposal proposal) {
        TextView titleLabel = itemView.findViewById(R.id.proposal_cell_title_label);
        TextView summaryLabel = itemView.findViewById(R.id.proposal_cell_description_label);

        titleLabel.setText(proposal.title);
        summaryLabel.setText(proposal.summary);
        final Context context = itemView.getContext();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProposalDetailActivity.class);
                ProposalDetailActivity.proposal = proposal;
                context.startActivity(intent);
            }
        });
    }
}

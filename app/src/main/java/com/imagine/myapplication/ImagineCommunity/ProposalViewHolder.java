package com.imagine.myapplication.ImagineCommunity;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.R;

public class ProposalViewHolder extends RecyclerView.ViewHolder {

    public ProposalViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Proposal proposal) {
        TextView titleLabel = itemView.findViewById(R.id.proposal_cell_title_label);
        TextView summaryLabel = itemView.findViewById(R.id.proposal_cell_description_label);

        titleLabel.setText(proposal.title);
        summaryLabel.setText(proposal.summary);
    }
}

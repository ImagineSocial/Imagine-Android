package com.imagine.myapplication.ImagineCommunity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.imagine.myapplication.R;

import java.util.ArrayList;


public class ProposalAdapter extends RecyclerView.Adapter<ProposalViewHolder> {

    public ArrayList<Proposal> proposalList;
    public Context mContext;

    public ProposalAdapter(ArrayList<Proposal> proposalList, Context mContext) {
        this.proposalList = proposalList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ProposalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.proposal_cell,parent,false);
        ProposalViewHolder proposalViewHolder = new ProposalViewHolder(view);
        return proposalViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProposalViewHolder holder, int position) {
        Proposal proposal = proposalList.get(position);
        holder.bind(proposal);
    }

    @Override
    public int getItemCount() {
        System.out.println("##Return size: "+proposalList.size());
        return proposalList.size();
    }
}

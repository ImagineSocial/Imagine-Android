package com.imagine.myapplication.nav_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.ImagineCommunity.Imagine_Fund_Activity;
import com.imagine.myapplication.ImagineCommunity.Proposal;
import com.imagine.myapplication.ImagineCommunity.ProposalActivity;
import com.imagine.myapplication.ImagineCommunity.ProposalAdapter;
import com.imagine.myapplication.ImagineCommunity.ProposalCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.SettingsActivity;

import java.util.ArrayList;

public class Imagine_Community_Fragment extends Fragment implements View.OnClickListener {

    Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_imagine_community,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mContext = view.getContext();

        CardView settingCard = view.findViewById(R.id.imagine_community_settings_card);
        settingCard.setOnClickListener(this);
        CardView proposalCard = view.findViewById(R.id.imagine_community_proposal_card);
        proposalCard.setOnClickListener(this);
        CardView bugCard = view.findViewById(R.id.imagine_community_bugs_card);
        bugCard.setOnClickListener(this);
        CardView fundCard = view.findViewById(R.id.imagine_community_imagineFund_card);
        fundCard.setOnClickListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.imagine_community_recyclerview);

        getCampaigns(recyclerView);
    }

    public void getCampaigns(final RecyclerView recyclerView) {
        ProposalActivity proposalActivity = new ProposalActivity();
        proposalActivity.getProposals(true, new ProposalCallback() {
            @Override
            public void onCallback(ArrayList<Proposal> proposals) {
                ProposalAdapter adapter = new ProposalAdapter(proposals, mContext);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagine_community_bugs_card:
                //gotobugsview
                break;
            case R.id.imagine_community_imagineFund_card:
                Intent fundIntent = new Intent(mContext, Imagine_Fund_Activity.class);
                mContext.startActivity(fundIntent);
                break;
            case R.id.imagine_community_proposal_card:
                Intent intent = new Intent(mContext, ProposalActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.imagine_community_settings_card:
                Intent InfoIntent = new Intent(mContext, SettingsActivity.class);
                mContext.startActivity(InfoIntent);
                break;
        }
    }
}

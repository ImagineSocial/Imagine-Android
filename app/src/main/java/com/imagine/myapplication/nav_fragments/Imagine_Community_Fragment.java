package com.imagine.myapplication.nav_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.imagine.myapplication.ImagineCommunity.Information_Activity;
import com.imagine.myapplication.ImagineCommunity.ProposalActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.SettingsActivity;
import com.imagine.myapplication.user_classes.UserActivity;

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

        CardView settingCard = view.findViewById(R.id.imagine_community_settings_card);
        settingCard.setOnClickListener(this);
        CardView proposalCard = view.findViewById(R.id.imagine_community_proposal_card);
        proposalCard.setOnClickListener(this);
        CardView bugCard = view.findViewById(R.id.imagine_community_bugs_card);
        bugCard.setOnClickListener(this);
        CardView fundCard = view.findViewById(R.id.imagine_community_imagineFund_card);
        fundCard.setOnClickListener(this);

        this.mContext = view.getContext();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagine_community_bugs_card:
                //gotobugsview
                break;
            case R.id.imagine_community_imagineFund_card:
                //gotofunc
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

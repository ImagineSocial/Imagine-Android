package com.imagine.myapplication.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.ArgumentsCallback;
import com.imagine.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CommunityFactsFragment extends Fragment {
    HashMap<String,String> args;
    ArrayList<Argument> pros;
    ArrayList<Argument> cons;
    CommunityFactsFragment frag;

    public CommunityFactsFragment(HashMap<String, String> args) {
        this.args = args;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.community_viewpager_facts,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView proCountLabel = view.findViewById(R.id.comm_facts_prosCount);
        final TextView conCountLabel = view.findViewById(R.id.comm_facts_consCount);

        Communities_Helper communities_helper = new Communities_Helper();
        communities_helper.getProArguments(args.get("commID"), new ArgumentsCallback() {
            @Override
            public void onCallback(ArrayList<Argument> args) {
                pros = args;
                proCountLabel.setText("("+pros.size()+")");
                initProRecyclerView();
            }
        });
        communities_helper.getContraArguments(args.get("commID"), new ArgumentsCallback() {
            @Override
            public void onCallback(ArrayList<Argument> args) {
                cons = args;
                conCountLabel.setText("("+cons.size()+")");
                initContraRecyclerView();
            }
        });
        this.frag = this;
    }

    public void initProRecyclerView(){
        RecyclerView pro_recycler = getView().findViewById(R.id.comm_facts_pros_recyclerView);
        ArgumentsAdapter adapter = new ArgumentsAdapter(this.pros,this.getContext());
        adapter.frag = this.frag;
        pro_recycler.setAdapter(adapter);
        pro_recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
    public void initContraRecyclerView(){
        RecyclerView pro_recycler = getView().findViewById(R.id.comm_facts_cons_recyclerView);
        ArgumentsAdapter adapter = new ArgumentsAdapter(this.cons,this.getContext());
        adapter.frag = this.frag;
        pro_recycler.setAdapter(adapter);
        pro_recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    public void showDialogFragment(){
        ArgumentsDialogFragment frag = new ArgumentsDialogFragment();
        frag.show(getFragmentManager(),"DialogFragment");
    }
}

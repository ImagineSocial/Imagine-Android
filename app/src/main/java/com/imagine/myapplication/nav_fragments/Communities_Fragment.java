package com.imagine.myapplication.nav_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_Adapter;
import com.imagine.myapplication.CommunityCallback;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class Communities_Fragment extends Fragment {

    Communities_Helper helper = new Communities_Helper();
    ArrayList<Community> commList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_communities,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        helper.getCommunities(new CommunityCallback() {
            @Override
            public void onCallback(ArrayList<Community> communities) {
                commList = communities;
                initRecyclerView(view);
            }
        });
    }

    private void initRecyclerView (final View view){
        RecyclerView recyclerView = view.findViewById(R.id.communites_recyclerview);
        Context context = view.getContext();
        final Community_Adapter adapter = new Community_Adapter(commList,context);
        recyclerView.setAdapter(adapter);
        GridLayoutManager mLayoutManager = new GridLayoutManager(context,2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(adapter.getItemViewType(position)){
                    case R.layout.communities_header:
                        return 2;
                    case R.layout.community:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            boolean loading = true;
            int previousTotal =0;
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = lm.getChildCount();
                int totalItemCount = lm.getItemCount();
                int pastVisibleItems = lm.findFirstVisibleItemPosition();

                if(loading && totalItemCount > previousTotal){
                    loading = false;
                    previousTotal = totalItemCount;
                }
                if((totalItemCount-(pastVisibleItems+visibleItemCount))<=4&&!loading){
                    loading = true;
                    helper.getMoreCommunities(new CommunityCallback() {
                        @Override
                        public void onCallback(ArrayList<Community> comms) {
                            commList = comms;
                            Community_Adapter adapter =(Community_Adapter) recyclerView.getAdapter();
                            adapter.addMoreCommunities(commList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}

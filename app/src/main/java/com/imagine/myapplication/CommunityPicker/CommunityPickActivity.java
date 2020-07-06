package com.imagine.myapplication.CommunityPicker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Community.Communities_Helper;
import com.imagine.myapplication.Community.Community;
import com.imagine.myapplication.Community.Community_Adapter;
import com.imagine.myapplication.CommunityCallback;
import com.imagine.myapplication.R;

import java.util.ArrayList;

public class CommunityPickActivity extends AppCompatActivity {

    public Communities_Helper helper = new Communities_Helper();
    ArrayList<Community> commList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_communities);
        helper.getCommunities(new CommunityCallback() {
            @Override
            public void onCallback(ArrayList<Community> communities) {
                commList = communities;
                initRecyclerView();
            }
        });
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.communites_recyclerview);
        final CommunityPickerAdapter adapter = new CommunityPickerAdapter(commList,this,this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,2);
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
                            CommunityPickerAdapter adapter =(CommunityPickerAdapter) recyclerView.getAdapter();
                            adapter.addMoreCommunities(comms);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public void resultMethod(Community community){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("name",community.name);
        resultIntent.putExtra("imageURl", community.imageURL);
        resultIntent.putExtra("commID", community.topicID);
        setResult(RESULT_OK,resultIntent);
    }
}
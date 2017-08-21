package com.entuition.wekend.view.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.LikeReadState;
import com.entuition.wekend.model.data.like.ReadFriendObservable;
import com.entuition.wekend.model.data.like.asynctask.GetLikeFriendTask;
import com.entuition.wekend.model.data.like.asynctask.IGetLikeFriendCallback;
import com.entuition.wekend.model.data.like.asynctask.UpdateProfileReadStateTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2016. 4. 11..
 */
public class RecommendFriendListActivity extends AppCompatActivity implements RecommendFriendViewAdapter.ItemClickListener {

    private final String TAG = getClass().getSimpleName();

    private int productId;
    private List<LikeDBItem> friendList;

    private Toolbar toolbar;
    private FrameLayout progressBarHolder;
    private RecyclerView recyclerView;
    private RecommendFriendViewAdapter viewAdapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_friend);

        productId = getIntent().getIntExtra(Constants.PARAMETER_PRODUCT_ID, -1);

        friendList = new ArrayList<LikeDBItem>();

        initView();
        loadRecommendFriendData();

        ReadFriendObservable.getInstance().addObserver(new ReadFriendObserver());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.id_toolbar_container);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView title = (TextView) findViewById(R.id.id_toolbar_title);
        title.setText(getString(R.string.label_recommend_friend));

        recyclerView = (RecyclerView) findViewById(R.id.id_recommend_friend_recyclerview);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        viewAdapter = new RecommendFriendViewAdapter(this);
        viewAdapter.setData(friendList);
        viewAdapter.setItemClickListener(this);

        recyclerView.setAdapter(viewAdapter);

        progressBarHolder = (FrameLayout) findViewById(R.id.id_screen_dim_progress);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_recommend_friend_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecommendFriendData();
            }
        });
    }

    private void loadRecommendFriendData() {

        progressBarHolder.setVisibility(View.VISIBLE);

        GetLikeFriendTask task = new GetLikeFriendTask(this);
        task.setCallback(new IGetLikeFriendCallback() {
            @Override
            public void onSuccess(List<LikeDBItem> result) {
                progressBarHolder.setVisibility(View.GONE);

                friendList = result;
                viewAdapter.setData(friendList);
                viewAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailed() {
                progressBarHolder.setVisibility(View.GONE);
            }
        });

        task.execute(productId);
    }

    @Override
    public void onItemClicked(View view, int position) {

        final LikeDBItem item = friendList.get(position);

        final String userId = UserInfoDaoImpl.getInstance().getUserId(RecommendFriendListActivity.this);

        LikeReadState readState = new LikeReadState();
        readState.setLikeId(item.getLikeId());
        readState.setUserId(userId);
        readState.setProductId(item.getProductId());
        readState.setLikeUserId(item.getUserId());

        UpdateProfileReadStateTask readStateTask = new UpdateProfileReadStateTask();
        readStateTask.setCallback(new ISimpleTaskCallback() {
            @Override
            public void onPrepare() {}

            @Override
            public void onSuccess(@Nullable Object object) {
                Intent intent = new Intent(RecommendFriendListActivity.this, SendProposeProfileActivity.class);
                intent.putExtra(Constants.PARAMETER_PRODUCT_ID, item.getProductId());
                intent.putExtra(Constants.PARAMETER_PROPOSEE_ID, item.getUserId());
                intent.putExtra(Constants.PARAMETER_PROPOSER_ID, userId);
                intent.putExtra(Constants.PARAMETER_FRIEND_ID, item.getUserId());
                startActivity(intent);
            }

            @Override
            public void onFailed() {}
        });
        readStateTask.execute(readState);
    }

    private class ReadFriendObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            String likedUserId = String.valueOf(data);

            for (int i = 0 ; i < friendList.size() ; i ++) {
                LikeDBItem item = friendList.get(i);
                if (item.getUserId().equals(likedUserId)) {
                    viewAdapter.notifyItemChanged(i);
                    return;
                }
            }
        }
    }
}

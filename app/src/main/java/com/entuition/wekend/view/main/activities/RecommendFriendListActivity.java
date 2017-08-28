package com.entuition.wekend.view.main.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.LikeReadState;
import com.entuition.wekend.model.data.like.ReadFriendObservable;
import com.entuition.wekend.model.data.like.asynctask.GetLikeFriendTask;
import com.entuition.wekend.model.data.like.asynctask.IGetLikeFriendCallback;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.view.WekendActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2016. 4. 11..
 */
public class RecommendFriendListActivity extends WekendActivity implements RecommendFriendViewAdapter.ItemClickListener {

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
        if (reinitialize(savedInstanceState)) return;
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



        new UpdateProfileReadStateTask().execute(position);
    }

    private class ReadFriendObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {

            int position = (int) data;
            viewAdapter.notifyItemChanged(position);
        }
    }

    /**
     * Created by ryukgoo on 2017. 6. 29..
     */

    private class UpdateProfileReadStateTask extends AsyncTask<Integer, Void, Void> {

        int position;
        String userId;

        @Override
        protected Void doInBackground(Integer... params) {

            position = params[0];

            LikeDBItem item = friendList.get(position);
            userId = UserInfoDaoImpl.getInstance().getUserId(RecommendFriendListActivity.this);

            LikeReadState readState = new LikeReadState();
            readState.setLikeId(item.getLikeId());
            readState.setUserId(userId);
            readState.setProductId(item.getProductId());
            readState.setLikeUserId(item.getUserId());

            try {
                LikeDBDaoImpl.getInstance().updateProfileReadState(readState);
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            LikeDBItem item = friendList.get(position);
            item.setRead(true);

            Intent intent = new Intent(RecommendFriendListActivity.this, SendProposeProfileActivity.class);
            intent.putExtra(Constants.PARAMETER_PRODUCT_ID, item.getProductId());
            intent.putExtra(Constants.PARAMETER_PROPOSEE_ID, item.getUserId());
            intent.putExtra(Constants.PARAMETER_PROPOSER_ID, userId);
            intent.putExtra(Constants.PARAMETER_FRIEND_ID, item.getUserId());
            startActivity(intent);

            ReadFriendObservable.getInstance().read(position);
        }
    }
}

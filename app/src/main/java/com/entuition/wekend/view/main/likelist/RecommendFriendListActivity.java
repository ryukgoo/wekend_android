package com.entuition.wekend.view.main.likelist;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.LikeInfoRepository;
import com.entuition.wekend.data.source.mail.MailType;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.RecommendFriendActivityBinding;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.main.likelist.adapter.RecommendFriendViewAdapter;
import com.entuition.wekend.view.main.likelist.viewmodel.RecommendFriendNavigator;
import com.entuition.wekend.view.main.likelist.viewmodel.RecommendFriendViewModel;
import com.entuition.wekend.view.main.mailbox.MailProfileActivity;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2016. 4. 11..
 */
public class RecommendFriendListActivity extends AppCompatActivity implements RecommendFriendNavigator {

    public static final String TAG = RecommendFriendListActivity.class.getSimpleName();

    private RecommendFriendActivityBinding binding;
    private RecommendFriendViewModel model;

    private RecommendFriendViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int productId = getIntent().getIntExtra(Constants.ExtraKeys.PRODUCT_ID, -1);

        model = new RecommendFriendViewModel(this, this, productId,
                UserInfoRepository.getInstance(this), LikeInfoRepository.getInstance(this));

        binding = DataBindingUtil.setContentView(this, R.layout.recommend_friend_activity);
        binding.setModel(model);

        setupToolbar();
        setupRecyclerView();

        model.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.onResume();
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

    @Override
    public void gotoProfileView(String userId, String friendId, int productId) {
        Intent intent = new Intent(this, MailProfileActivity.class);
        intent.putExtra(Constants.ExtraKeys.USER_ID, userId);
        intent.putExtra(Constants.ExtraKeys.FRIEND_ID, friendId);
        intent.putExtra(Constants.ExtraKeys.PRODUCT_ID, productId);
        intent.putExtra(Constants.ExtraKeys.MAIL_TYPE, MailType.send.toString());
        startActivity(intent);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupRecyclerView() {
        binding.recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new RecommendFriendViewAdapter(model, new ArrayList<LikeInfo>(0));
        binding.recyclerview.setAdapter(adapter);
    }
}

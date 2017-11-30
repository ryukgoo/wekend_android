package com.entuition.wekend.view.main.likelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.entuition.wekend.R;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.LikeInfoRepository;
import com.entuition.wekend.data.source.product.ProductInfoRepository;
import com.entuition.wekend.databinding.LikeListFragmentBinding;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.common.AbstractFragment;
import com.entuition.wekend.view.common.DividerItemDecoration;
import com.entuition.wekend.view.main.campaign.CampaignDetailActivity;
import com.entuition.wekend.view.main.likelist.adapter.LikeListAdapter;
import com.entuition.wekend.view.main.likelist.viewmodel.LikeListNavigator;
import com.entuition.wekend.view.main.likelist.viewmodel.LikeListViewModel;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by ryukgoo on 2016. 6. 30..
 */
public class LikeListFragment extends AbstractFragment implements LikeListNavigator {

    public static final String TAG = LikeListFragment.class.getSimpleName();

    public static LikeListFragment newInstance() {
        return new LikeListFragment();
    }

    private LikeListViewModel model;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LikeListAdapter adapter;

    public LikeListFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        model = new LikeListViewModel(getActivity(), this,
                ProductInfoRepository.getInstance(getActivity()),
                LikeInfoRepository.getInstance(getActivity()));

        LikeListFragmentBinding binding = LikeListFragmentBinding.inflate(inflater, container, false);
        recyclerView = binding.likeList;
        setupRecyclerView();
        binding.setModel(model);

        model.onCreate();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new LikeListAdapter(model, new ArrayList<LikeInfo>(0));
        adapter.setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        model.onResume();
    }

    @Override
    public void onClickTitle() {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void reselect() {
        layoutManager.scrollToPosition(0);
    }

    @Override
    public void gotoCampaignDetail(int productId) {
        Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
        intent.putExtra(Constants.ExtraKeys.PRODUCT_ID, productId);
        startActivity(intent);
    }

    @Override
    public void onRemoveSwipeLayout(SwipeLayout layout) {
        adapter.removeShownLayouts(layout);
    }

    @Override
    public void onDeleteLikeItem(int position) {
        adapter.deleteItem(position);
    }

    @Override
    public void onDeleteLikeItemFailed() {

    }

    @Override
    public void onRefreshLikeItem(int position) {
        adapter.notifyItemChanged(position);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
    }
}

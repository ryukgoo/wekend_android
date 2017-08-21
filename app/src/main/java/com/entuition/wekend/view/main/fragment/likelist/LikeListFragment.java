package com.entuition.wekend.view.main.fragment.likelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.like.AddLikeObservable;
import com.entuition.wekend.model.data.like.DeleteLikeObservable;
import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.ReadLikeObservable;
import com.entuition.wekend.model.data.like.asynctask.DeleteLikeProductTask;
import com.entuition.wekend.model.data.like.asynctask.GetLikeProductTask;
import com.entuition.wekend.model.data.like.asynctask.UpdateLikeReadStateTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.view.main.ChangeTitleObservable;
import com.entuition.wekend.view.main.DropdownVisibleObservable;
import com.entuition.wekend.view.main.activities.CampaignDetailActivity;
import com.entuition.wekend.view.main.fragment.AbstractFragment;
import com.entuition.wekend.view.main.fragment.SimpleViewHolder;
import com.entuition.wekend.view.util.DividerItemDecoration;

import java.util.Observable;
import java.util.Observer;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by ryukgoo on 2016. 6. 30..
 */
public class LikeListFragment extends AbstractFragment {

    private final String TAG = getClass().getSimpleName();

    private FrameLayout progressbar;
    private RecyclerView likeListView;
    private LikeListAdapter likeListAdapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout refreshLayout;

    public LikeListFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AddLikeObservable.getInstance().addObserver(new AddLikeObserver());
        DeleteLikeObservable.getInstance().addObserver(new DeleteLikeObserver());
        ReadLikeObservable.getInstance().addObserver(new ReadLikeObserver());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_likelist, container, false);

        ChangeTitleObservable.getInstance().change(getString(R.string.label_likelist));
        DropdownVisibleObservable.getInstance().change(false);

        initView(rootView);
        loadLikeListData();

        return rootView;
    }

    private void initView(View rootView) {likeListView = (RecyclerView) rootView.findViewById(R.id.id_like_list_recycler_view);
        progressbar = (FrameLayout) rootView.findViewById(R.id.id_screen_dim_progress);

        // Layout Manager
        layoutManager = new LinearLayoutManager(getActivity());
        likeListView.setLayoutManager(layoutManager);

        // Item Decorator
        likeListView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        likeListView.setItemAnimator(new FadeInLeftAnimator());

        likeListAdapter = new LikeListAdapter(getActivity());
        likeListAdapter.setOnItemClickListener(new ItemClickListener());
        likeListAdapter.setMode(Attributes.Mode.Single);
//        likeListView.setAdapter(likeListAdapter);

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.id_like_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLikeListData();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.d(TAG, "onHiddenChanged > hidden : " + hidden);

        if (!hidden) {
            ChangeTitleObservable.getInstance().change(getString(R.string.label_likelist));
            DropdownVisibleObservable.getInstance().change(false);

//            likeListAdapter.closeAllOpenedLayout();
        }
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
    public void refresh() {
//        loadLikeListData();
    }

    private void loadLikeListData() {
        GetLikeProductTask task = new GetLikeProductTask(getActivity());
        task.setCallback(new GetLikeProductCallback());
        task.execute();
    }

    private class GetLikeProductCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() {
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(@Nullable Object object) {
            likeListView.setAdapter(likeListAdapter);
            likeListAdapter.notifyDataSetChanged();
            progressbar.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailed() {
            // TODO : Exceptions~!!!
            progressbar.setVisibility(View.GONE);
        }
    }

    private class AddLikeObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            int productId = (Integer) data;
            Log.d(TAG, "AddLikeObserver > update!!! > productId : " + productId);
//            int position = LikeDBDaoImpl.getInstance().getPositionByProductId(productId);
//            likeListAdapter.notifyItemInserted(position);

            likeListAdapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
        }
    }

    private class DeleteLikeObserver implements Observer {
        @Override
        public void update(Observable observable, Object data) {
            int productId = (Integer) data;
            int position = LikeDBDaoImpl.getInstance().getPositionByProductId(productId);
            likeListAdapter.notifyItemRemoved(position);
        }
    }

    private class ReadLikeObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            int productId = (Integer) data;

            int position = LikeDBDaoImpl.getInstance().getPositionByProductId(productId);
            likeListAdapter.notifyItemChanged(position);
        }
    }

    private class ItemClickListener implements SimpleViewHolder.IViewHolderListener {

        @Override
        public void onClickItem(int position) {

            LikeDBItem item = LikeDBDaoImpl.getInstance().getList().get(position);

            new UpdateLikeReadStateTask().execute(item);

            Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
            intent.putExtra(Constants.PARAMETER_PRODUCT_ID, item.getProductId());
            getActivity().startActivity(intent);
        }

        @Override
        public void onClickDeleteButton(final int position, final SwipeLayout layout) {

            likeListAdapter.mItemManger.removeShownLayouts(layout);

            LikeDBItem item = LikeDBDaoImpl.getInstance().getList().get(position);

            DeleteLikeProductTask task = new DeleteLikeProductTask();
            task.setCallback(new DeleteLikeProductTask.IDeleteLikeProductCallback() {

                @Override
                public void onSuccess(int likedCount) {
//
                }

                @Override
                public void onFailed() {

                }
            });
            task.execute(item);
        }

        @Override
        public boolean onTouchItem(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                if (likeListAdapter.isOpenLayout()) {
                    likeListAdapter.closeAllOpenedLayout();
                    return true;
                }
            }
            return false;
        }
    }
}

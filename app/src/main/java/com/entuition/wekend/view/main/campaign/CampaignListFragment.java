package com.entuition.wekend.view.main.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.like.LikeInfoRepository;
import com.entuition.wekend.data.source.product.ProductFilterOptions;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.ProductInfoRepository;
import com.entuition.wekend.data.source.product.enums.IProductEnum;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.CampaignFragmentBinding;
import com.entuition.wekend.databinding.OptionFilterBinding;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.common.AbstractFragment;
import com.entuition.wekend.view.main.campaign.adapter.CampaignLayoutManager;
import com.entuition.wekend.view.main.campaign.adapter.CampaignListAdapter;
import com.entuition.wekend.view.main.campaign.adapter.FilterSpinnerAdapter;
import com.entuition.wekend.view.main.campaign.viewmodel.CampaignListNavigator;
import com.entuition.wekend.view.main.campaign.viewmodel.CampaignListViewModel;
import com.entuition.wekend.view.main.campaign.viewmodel.OptionFilterListener;
import com.entuition.wekend.view.main.campaign.viewmodel.OptionFilterViewModel;
import com.entuition.wekend.view.main.container.viewmodel.ContainerViewModel;

import java.util.ArrayList;

/**
 * Created by Kim on 2015-08-23.
 */
public class CampaignListFragment extends AbstractFragment implements CampaignListNavigator {

    public static final String TAG = CampaignListFragment.class.getSimpleName();

    private ContainerViewModel containerViewModel;

    private CampaignFragmentBinding binding;
    private CampaignListViewModel model;

    private CampaignLayoutManager layoutManager;
    private CampaignListAdapter adapter;
    private RecyclerView recyclerView;
    private OptionFilterViewModel filterViewModel;

    public static CampaignListFragment newInstance() {
        return new CampaignListFragment();
    }

    public CampaignListFragment() {}

    public void setContainerViewModel(ContainerViewModel containerViewModel) {
        this.containerViewModel = containerViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        model = new CampaignListViewModel(getActivity(), this, UserInfoRepository.getInstance(getActivity()),
                ProductInfoRepository.getInstance(getActivity()), LikeInfoRepository.getInstance(getActivity()));

        binding = CampaignFragmentBinding.inflate(inflater, container, false);
        recyclerView = binding.campaignList;
        binding.setModel(model);

        setupRecyclerView();

        model.onCreate();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new CampaignListAdapter(model, new ArrayList<ProductInfo>(0));
        recyclerView.setAdapter(adapter);
        setupOptionFilter(binding.filterContainer);
    }

    @Override
    public void onDestroy() {
        model.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_campaign, menu);
        MenuItem searchItem = menu.findItem(R.id.id_action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.campaign_search_hint));

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterViewModel.close();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                // TODO : auto close search view
                model.searchProductInfos(query);
                containerViewModel.title.set(model.getTitle());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClickTitle() {
        filterViewModel.toggle();
    }

    @Override
    public boolean onBackPressed() {
        return filterViewModel.onBackPressed();
    }

    @Override
    public void reselect() {
        layoutManager.scrollToPosition(0);
    }

    private void setupRecyclerView() {
        layoutManager = new CampaignLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new PaginatedScrollListener(model, layoutManager));
    }

    private void setupOptionFilter(ViewGroup container) {

        filterViewModel = new OptionFilterViewModel(getActivity(), ProductInfoRepository.getInstance(getActivity()));

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        OptionFilterBinding binding = OptionFilterBinding.inflate(inflater, container, true);

        binding.spinnerMain.setAdapter(new FilterSpinnerAdapter(getActivity(), R.layout.spinner_item, new ArrayList<IProductEnum>(0)));
        binding.spinnerSub.setAdapter(new FilterSpinnerAdapter(getActivity(), R.layout.spinner_item, new ArrayList<IProductEnum>(0)));
        binding.spinnerRegion.setAdapter(new FilterSpinnerAdapter(getActivity(), R.layout.spinner_item, new ArrayList<IProductEnum>(0)));

        binding.setModel(filterViewModel);
        binding.setListener(new OptionFilterListener() {
            @Override
            public void onClickFilter(ProductFilterOptions options) {
                filterViewModel.close();
                model.filterProductInfos(options);
                containerViewModel.title.set(model.getTitle());
            }

            @Override
            public void onClickShowAll() {
                filterViewModel.reset();
                filterViewModel.close();
                ProductFilterOptions options = new ProductFilterOptions.Builder().build();
                model.filterProductInfos(options);
                containerViewModel.title.set(model.getTitle());
            }
        });

        filterViewModel.onCreate();
    }

    @Override
    public void gotoCampaignDetail(int productId) {
        Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
        intent.putExtra(Constants.ExtraKeys.PRODUCT_ID, productId);
        startActivity(intent);
    }

    @Override
    public void onNotifyItemChanged(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onFailedAddLike() {

    }

    @Override
    public void onFailedDeleteLike() {

    }

    private static class PaginatedScrollListener extends RecyclerView.OnScrollListener {

        private final CampaignListViewModel model;
        private final CampaignLayoutManager layoutManager;

        int pastVisiblesItems, visibleItemCount, totalItemCount, lastVisibleItemCount;

        PaginatedScrollListener(CampaignListViewModel model, CampaignLayoutManager layoutManager) {
            this.model = model;
            this.layoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) {
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItemCount = layoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisibleItemCount > 0 && lastVisibleItemCount >= totalItemCount - 1) {
                    model.loadPaginatedData();
                }
            }
        }
    }
/*

    @Override
    public void onResume() {
        super.onResume();

        slidingDrawerFilter.resumeFilterPosition();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            ChangeTitleObservable.getInstance().change(getTitleString());
            DropdownVisibleObservable.getInstance().change(true);

            campaignAdapter.notifyDataSetChanged();
        } else {
            ActivityUtils.hideKeyboard(getActivity());
        }
    }

    @Override
    public void refresh() {
//        campaignAdapter.notifyDataSetChanged();
    }

    private void refreshCampaignList() {
        ReadyCampaignListTask task = new ReadyCampaignListTask(getActivity());
        task.setCallback(new LoadProductCallback());
        task.execute(queryOptions);

        searchKeyword = null;

        ChangeTitleObservable.getInstance().change(getTitleString());
    }

    private void setListEnabled(boolean enabled) {
        campaignAdapter.setClickEnabled(enabled);
        layoutManager.setScrollEnabled(enabled);
    }

    private class AddLikeObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            int productId = (Integer) data;
            int changedPosition = ProductDaoImpl.getInstance().getPositionById(productId);
            campaignAdapter.notifyItemChanged(changedPosition);
        }
    }

    private class DeleteLikeObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            int productId = (Integer) data;
            int changedPosition = ProductDaoImpl.getInstance().getPositionById(productId);

            campaignAdapter.notifyItemChanged(changedPosition);
        }
    }

    private class ItemClickListener implements CampaignRecyclerViewAdapter.OnItemClickListener {

        @Override
        public void onItemClicked(int position) {

            ProductInfo productInfo = ProductDaoImpl.getInstance().loadProductList().get(position);

            Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
            intent.putExtra(Constants.PRODUCT_ID, productInfo.getId());
            startActivity(intent);
        }

        @Override
        public void onItemLikeClicked(int position) {

            ProductInfo productInfo = ProductDaoImpl.getInstance().loadProductList().get(position);

            if (!LikeDBDaoImpl.getInstance().hasLikeProduct(productInfo.getId())) {
                LikeDBItem likeDBItem = new LikeDBItem();
                likeDBItem.setUserId(UserInfoDaoImpl.getInstance(getActivity()).getUserId());
                likeDBItem.setProductId(productInfo.getId());

                LoadUserInfoAndProductInfoTask task = new LoadUserInfoAndProductInfoTask(getActivity());
                task.setCallback(new LoadUserInfoAndProductInfoCallback());
                task.execute(likeDBItem);
            } else {
                // Delete Like
                LikeDBItem likeDBItem = LikeDBDaoImpl.getInstance().getItemByProductId(productInfo.getId());
                if (likeDBItem == null) return;

                setListEnabled(false);

                DeleteLikeProductTask task = new DeleteLikeProductTask();
                task.setCallback(new DeleteLikeProductTask.IDeleteLikeProductCallback() {
                    @Override
                    public void onSuccess(int likedCount) {
                        setListEnabled(true);
                    }

                    @Override
                    public void onFailed() {
                        setListEnabled(true);
                    }
                });
                task.execute(likeDBItem);
            }
        }
    }

    private class LoadProductCallback implements IReadyCampaignListCallback {

        @Override
        public void onPrepare() {
            startTime = System.currentTimeMillis();
            Log.d(TAG, "onPrepare!!! > loadProductList > startTime : " + startTime);
            setListEnabled(false);
            progressbarLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCompleted(List<LikeDBItem> results) {
            endTime = System.currentTimeMillis();
            Log.d(TAG, "loadProductList > onComplete !!! > endTime : " + endTime);
            Log.d(TAG, "loadProductList > response Time > mills : " + (endTime - startTime));

            GetProductSubListTask task = new GetProductSubListTask();
            task.setCallback(new GetProductSubListCallback());
            task.execute();
        }

        @Override
        public void onFailed() {
            setListEnabled(true);
            progressbarLayout.setVisibility(View.GONE);
            scrollListener.setLoading(true);
        }
    }

    private class GetProductSubListCallback implements IGetProductSubListCallback {

        @Override
        public void onPrepare() {
            startTime = System.currentTimeMillis();
            Log.d(TAG, "onPrepare!!! > getSubList > startTime : " + startTime);

            setListEnabled(false);
            progressbarLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCompleted(List<ProductInfo> results) {
            setListEnabled(true);
            progressbarLayout.setVisibility(View.GONE);

            campaignAdapter.notifyDataSetChanged();

            if (campaignAdapter.getItemCount() == 0) {
                textViewNoResult.setVisibility(View.VISIBLE);
            } else {
                textViewNoResult.setVisibility(View.GONE);
            }

            refreshLayout.setEnabled(true);
            scrollListener.setLoading(true);
            refreshLayout.setRefreshing(false);

            if (isStartFromBeginning) layoutManager.scrollToPosition(0);

            endTime = System.currentTimeMillis();
            Log.d(TAG, "getSubList > onComplete !!! > endTime : " + endTime);
            Log.d(TAG, "getSubList > response Time > mills : " + (endTime - startTime));
            Log.d(TAG, "list size : " + ProductDaoImpl.getInstance().loadProductList().size());

        }

        @Override
        public void onFailed() {
            setListEnabled(true);
            progressbarLayout.setVisibility(View.GONE);
            scrollListener.setLoading(true);
        }
    }

    private class LoadUserInfoAndProductInfoCallback implements ILoadUserInfoAndProductInfoCallback {

        @Override
        public void onPrePared() {
            setListEnabled(false);
//            progressbarLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(UserInfo userInfo, ProductInfo productInfo) {
            LikeDBItem likeValue = new LikeDBItem();
            likeValue.setUserId(userInfo.getUserId());
            likeValue.setProductId(productInfo.getId());
            likeValue.setNickname(userInfo.getNickname());
            likeValue.setGender(userInfo.getGender());
            likeValue.setProductTitle(productInfo.getTitleKor());
            likeValue.setProductDesc(productInfo.getSubTitle());

            AddLikeProductTask task = new AddLikeProductTask();
            task.setCallback(new AddLikeProductCallback());
            task.execute(likeValue);
        }

        @Override
        public void onFailed() {
            setListEnabled(true);
//            progressbarLayout.setVisibility(View.GONE);
        }
    }

    private class AddLikeProductCallback implements IAddLikeProductCallback {

        @Override
        public void onSuccess(int friendCount) {
            setListEnabled(true);
            //            progressbarLayout.setVisibility(View.GONE);
        }

        @Override
        public void onFailed() {
            setListEnabled(true);
//            progressbarLayout.setVisibility(View.GONE);

            Toast.makeText(getActivity(), getString(R.string.campaign_detail_like_failed), Toast.LENGTH_SHORT).show();
        }
    }
    */
}
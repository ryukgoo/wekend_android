package com.entuition.wekend.view.main.fragment.campaign;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.like.AddLikeObservable;
import com.entuition.wekend.model.data.like.DeleteLikeObservable;
import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.asynctask.AddLikeProductTask;
import com.entuition.wekend.model.data.like.asynctask.DeleteLikeProductTask;
import com.entuition.wekend.model.data.like.asynctask.IAddLikeProductCallback;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.product.ProductQueryOptions;
import com.entuition.wekend.model.data.product.asynctask.GetProductSubListTask;
import com.entuition.wekend.model.data.product.asynctask.IGetProductSubListCallback;
import com.entuition.wekend.model.data.product.asynctask.ILoadUserInfoAndProductInfoCallback;
import com.entuition.wekend.model.data.product.asynctask.IReadyCampaignListCallback;
import com.entuition.wekend.model.data.product.asynctask.LoadUserInfoAndProductInfoTask;
import com.entuition.wekend.model.data.product.asynctask.ReadyCampaignListTask;
import com.entuition.wekend.model.data.product.asynctask.SearchProductsTask;
import com.entuition.wekend.model.data.product.enums.Category;
import com.entuition.wekend.model.data.product.enums.IProductEnum;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.view.main.ChangeTitleObservable;
import com.entuition.wekend.view.main.DropdownVisibleObservable;
import com.entuition.wekend.view.main.activities.CampaignDetailActivity;
import com.entuition.wekend.view.main.fragment.AbstractFragment;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Kim on 2015-08-23.
 */
public class CampaignListFragment extends AbstractFragment {

    private final String TAG = getClass().getSimpleName();

    private CampaignLayoutManager layoutManager;
    private RecyclerView campaignListView;
    private CampaignRecyclerViewAdapter campaignAdapter;
    private FrameLayout progressbarLayout;
    private SwipeRefreshLayout refreshLayout;
    private View dimLayout;
    private SearchView searchView;
    private TextView textViewNoResult;
    private String searchKeyword;

    private SlidingDrawerFilter slidingDrawerFilter;

    private ProductQueryOptions queryOptions;
    private ScrollListener scrollListener;
    private boolean isStartFromBeginning;

    // check response time ==>>> FOR TEST!!
    long startTime;
    long endTime;

    public CampaignListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setHasOptionsMenu(true);
        AddLikeObservable.getInstance().addObserver(new AddLikeObserver());
        DeleteLikeObservable.getInstance().addObserver(new DeleteLikeObserver());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campaign, container, false);

        ChangeTitleObservable.getInstance().change(getTitleString());
        DropdownVisibleObservable.getInstance().change(true);

        isStartFromBeginning = true;
        initViews(rootView);
        loadProductInfos(null);

        return rootView;
    }

    private void initViews(View view) {

        Log.d(TAG, "initViews");

        // initViews
        campaignListView = (RecyclerView) view.findViewById(R.id.id_list_campaign);
        progressbarLayout = (FrameLayout) view.findViewById(R.id.id_screen_dim_progress);
        dimLayout = view.findViewById(R.id.id_campaign_dim_layout);
        dimLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingDrawerFilter.isOpen()) {
                    slidingDrawerFilter.close();
                }
            }
        });

        textViewNoResult = (TextView) view.findViewById(R.id.id_textview_campaign_no_result);

        campaignAdapter = new CampaignRecyclerViewAdapter(getActivity());
        campaignAdapter.setOnItemClickListener(new ItemClickListener());

        layoutManager = new CampaignLayoutManager(getActivity());
        campaignListView.setLayoutManager(layoutManager);
        campaignListView.setItemAnimator(new DefaultItemAnimator());
        campaignListView.setAdapter(campaignAdapter);
        scrollListener = new ScrollListener();
        campaignListView.addOnScrollListener(scrollListener);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_campaign_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isStartFromBeginning = true;
                loadProductInfos(queryOptions);
            }
        });

        slidingDrawerFilter = new SlidingDrawerFilter(getActivity(), view);

        refreshLayout.setEnabled(false);
    }

    private void loadProductInfos(@Nullable ProductQueryOptions options) {
        if (options == null) {
            if (queryOptions == null) {
                queryOptions = new ProductQueryOptions.Builder()
                        .setOrderType(ProductQueryOptions.ORDER_BY_DATE)
                        .setMainCategory(Category.NONE)
                        .setSubCategory(Category.NONE)
                        .setProductRegion(Category.NONE)
                        .build();
            }
        } else {
            queryOptions = new ProductQueryOptions.Builder().cloneFrom(options).build();
        }

        refreshCampaignList();
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

    @Override
    public void onStart() {
        super.onStart();

        slidingDrawerFilter.setListener(new SlidingDrawerFilter.IFilterCallback() {

            @Override
            public void onOpenDrawer() {
                setListEnabled(false);
            }

            @Override
            public void onStartOpen() {
                dimLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCloseDrawer() {
                setListEnabled(true);
            }

            @Override
            public void onStartClose() {
                dimLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCompleteFilterSelect(int order, IProductEnum mainCategory, IProductEnum subCategory, IProductEnum region) {

                ProductQueryOptions options = new ProductQueryOptions.Builder()
                        .setOrderType(order)
                        .setMainCategory(mainCategory)
                        .setSubCategory(subCategory)
                        .setProductRegion(region)
                        .build();

                isStartFromBeginning = true;
                loadProductInfos(options);
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        slidingDrawerFilter.resumeFilterPosition();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            ChangeTitleObservable.getInstance().change(getTitleString());
            DropdownVisibleObservable.getInstance().change(true);

            campaignAdapter.notifyDataSetChanged();
        } else {
            closeInputMethod();
        }
    }

    @Override
    public void onClickTitle() {
        if (slidingDrawerFilter.isOpen()) {
            slidingDrawerFilter.close();
        } else {
            slidingDrawerFilter.open();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_campaign, menu);
        MenuItem searchItem = menu.findItem(R.id.id_action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("상점명/지역명 등으로 검색");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnSearchClick");

                if (slidingDrawerFilter.isOpen()) {
                    slidingDrawerFilter.close();
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "onCloseSearchView");
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, "onQueryTextSubmit > query : " + query);

                searchView.clearFocus();

                isStartFromBeginning = true;
                SearchProductsTask task = new SearchProductsTask(getActivity());
                task.setCallback(new ISimpleTaskCallback() {
                    @Override
                    public void onPrepare() {
                        progressbarLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(@Nullable Object object) {
                        GetProductSubListTask task = new GetProductSubListTask();
                        task.setCallback(new GetProductSubListCallback());
                        task.execute();

                        ChangeTitleObservable.getInstance().change(getTitleString());
                    }

                    @Override
                    public void onFailed() {
                        progressbarLayout.setVisibility(View.GONE);
                    }
                });
                task.execute(query);

                searchKeyword = query;

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.d(TAG, "onQueryTextChange > newText : " + newText);

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onBackPressed() {

        Log.d(TAG, "onBAckpressed!!!!@!@!@!@!@!@");

        if (slidingDrawerFilter.isOpen()) {
            slidingDrawerFilter.close();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void reselect() {
        layoutManager.scrollToPosition(0);
    }

    @Override
    public void refresh() {
        // TODO: ?
//        campaignAdapter.notifyDataSetChanged();
    }

    public void closeInputMethod() {

        Log.d(TAG, "closeInputMethod!!");

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public String getTitleString() {

        if (searchKeyword != null) return "\"" + searchKeyword + "\" 로 검색";

        String strTitle = getString(R.string.label_all);

        if (queryOptions ==  null) return strTitle;

        if (queryOptions.getMainCategory().getIdentifier() != Category.NONE.getIdentifier()) {
            strTitle = getString(queryOptions.getMainCategory().getResource());
        }

        if (queryOptions.getSubCategory().getIdentifier() != Category.NONE.getIdentifier()) {
            strTitle += ", " + getString(queryOptions.getSubCategory().getResource());
        }

        if (queryOptions.getProductRegion().getIdentifier() != Category.NONE.getIdentifier()) {
            strTitle += ", " + getString(queryOptions.getProductRegion().getResource());
        }

        return strTitle;
    }

    /**
     * AddLikeObserver
     */
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

    /**
     * ListItemClickListener
     */
    private class ItemClickListener implements CampaignRecyclerViewAdapter.OnItemClickListener {

        @Override
        public void onItemClicked(int position) {

            ProductInfo productInfo = ProductDaoImpl.getInstance().getProductList().get(position);

            Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
            intent.putExtra(Constants.PARAMETER_PRODUCT_ID, productInfo.getId());
            startActivity(intent);
        }

        @Override
        public void onItemLikeClicked(int position) {

            ProductInfo productInfo = ProductDaoImpl.getInstance().getProductList().get(position);

            if (!LikeDBDaoImpl.getInstance().hasLikeProduct(productInfo.getId())) {
                LikeDBItem likeDBItem = new LikeDBItem();
                likeDBItem.setUserId(UserInfoDaoImpl.getInstance().getUserId(getActivity()));
                likeDBItem.setProductId(productInfo.getId());

                LoadUserInfoAndProductInfoTask task = new LoadUserInfoAndProductInfoTask();
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

    /**
     * ScrollListener
     */
    private class ScrollListener extends RecyclerView.OnScrollListener {

        private boolean loading = true;
        int pastVisiblesItems, visibleItemCount, totalItemCount, lastVisibleItemCount;

        public void setLoading(boolean loading) {
            this.loading = loading;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) {
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItemCount = layoutManager.findLastCompletelyVisibleItemPosition();

                if (loading) {
                    if (lastVisibleItemCount > 0 && lastVisibleItemCount >= totalItemCount - 1) {
                        loading = false;

                        isStartFromBeginning = false;

                        GetProductSubListTask task = new GetProductSubListTask();
                        task.setCallback(new GetProductSubListCallback());
                        task.execute();

                        Log.d(TAG, "ScrollListenr > loadMore!!!!");
                    }
                }
            }
        }
    }

    /**
     * Load Product data Callback
     */
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
            Log.d(TAG, "list size : " + ProductDaoImpl.getInstance().getProductList().size());

        }

        @Override
        public void onFailed() {
            setListEnabled(true);
            progressbarLayout.setVisibility(View.GONE);
            scrollListener.setLoading(true);

            // TODO : Notice Failure
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
            // TODO : notice retry
            setListEnabled(true);
//            progressbarLayout.setVisibility(View.GONE);
        }
    }

    private class AddLikeProductCallback implements IAddLikeProductCallback {

        @Override
        public void onSuccess(int totalCount, int friendCount) {
            setListEnabled(true);
//            progressbarLayout.setVisibility(View.GONE);
        }

        @Override
        public void onFailed() {
            setListEnabled(true);
//            progressbarLayout.setVisibility(View.GONE);

            Toast.makeText(getActivity(), getString(R.string.campaign_detail_like_failed), Toast.LENGTH_SHORT).show();
            // TODO : retry like
        }
    }
}
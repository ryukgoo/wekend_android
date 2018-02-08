package com.entuition.wekend.view.main.likelist.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.util.Log;

import com.daimajia.swipe.SwipeLayout;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.LikeInfoDataSource;
import com.entuition.wekend.data.source.like.LikeInfoRepository;
import com.entuition.wekend.data.source.like.observable.AddLikeObservable;
import com.entuition.wekend.data.source.like.observable.DeleteLikeObservable;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.product.ProductReadState;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2017. 11. 7..
 */

public class LikeListViewModel extends AbstractViewModel {

    public static final String TAG = LikeListViewModel.class.getSimpleName();

    public final ObservableBoolean isNoData = new ObservableBoolean();
    public final ObservableBoolean isLoading = new ObservableBoolean();
    public final ObservableBoolean isRefreshing = new ObservableBoolean();

    public final ObservableArrayList<LikeInfo> likeInfos = new ObservableArrayList<>();

    private final ProductInfoDataSource productInfoDataSource;
    private final LikeInfoDataSource likeInfoDataSource;
    private final WeakReference<LikeListNavigator> navigator;

    public LikeListViewModel(Context context, LikeListNavigator navigator,
                             ProductInfoDataSource productInfoDataSource, LikeInfoDataSource likeInfoDataSource) {
        super(context);

        this.navigator = new WeakReference<LikeListNavigator>(navigator);
        this.productInfoDataSource = productInfoDataSource;
        this.likeInfoDataSource = likeInfoDataSource;

        isNoData.set(false);
        isLoading.set(false);
    }

    @Override
    public void onCreate() {
        AddLikeObservable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                likeInfoDataSource.refreshLikeInfos();
                loadLikeInfos();
            }
        });

        DeleteLikeObservable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                deleteLikeItem((LikeInfo) data);
            }
        });

        isLoading.set(true);
    }

    @Override
    public void onResume() {
        loadLikeInfos();
    }

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onRefresh() {
        isRefreshing.set(true);
        likeInfoDataSource.refreshLikeInfos();
        loadLikeInfos();
    }

    public void onClickItem(LikeInfo info) {
        likeInfoDataSource.readLike(info);
        if (navigator.get() != null) {
            navigator.get().gotoCampaignDetail(info.getProductId());
        }
    }

    public void onClickDeleteItem(LikeInfo info, final int position, final SwipeLayout layout) {
        Log.d(TAG, "onClickDeleteItem");
        likeInfoDataSource.deleteLike(info, new LikeInfoDataSource.DeleteLikeCallback() {
            @Override
            public void onCompleteDeleteLike(LikeInfo info, int remainCount) {
                if (navigator.get() != null) {
                    navigator.get().onRemoveSwipeLayout(layout);
                }
                if (remainCount == 0) isNoData.set(true);
            }

            @Override
            public void onFailedDeleteLike() {
                if (navigator.get() != null) {
                    navigator.get().onDeleteLikeItemFailed();
                }
            }
        });
    }

    public boolean isNewLike(LikeInfo info) {
        return !productInfoDataSource.isReadLikeInfo(info);
    }

    private void loadLikeInfos() {
        likeInfoDataSource.loadLikeInfos(new LikeInfoDataSource.LoadLikeInfoListCallback() {
            @Override
            public void onLikeInfoListLoaded(final List<LikeInfo> infos) {
                productInfoDataSource.getProductReadStates(new ProductInfoDataSource.LoadProductReadStatesCallback() {
                    @Override
                    public void onProductReadStatesLoaded(Map<Integer, ProductReadState> readStates) {
                        isLoading.set(false);
                        isRefreshing.set(false);

                        List<LikeInfo> sortedList = LikeInfoRepository.getSortedList(infos, readStates);

                        likeInfos.clear();
                        likeInfos.addAll(sortedList);
                        validateNoData();
                    }

                    @Override
                    public void onDataNotAvailable() {
                        isLoading.set(false);
                        isRefreshing.set(false);
                        validateNoData();
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                isLoading.set(false);
                isRefreshing.set(false);
                validateNoData();
            }
        });
    }

    private void deleteLikeItem(LikeInfo likeInfo) {
        int position = likeInfos.indexOf(likeInfo);
        if (position >= 0 && navigator.get() != null) navigator.get().onDeleteLikeItem(position);
    }

    private void validateNoData() {
        if (likeInfos.size() == 0) {
            isNoData.set(true);
        } else {
            isNoData.set(false);
        }
    }
}

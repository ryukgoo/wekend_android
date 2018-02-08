package com.entuition.wekend.view.main.campaign.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.LikeInfoDataSource;
import com.entuition.wekend.data.source.like.observable.AddLikeObservable;
import com.entuition.wekend.data.source.like.observable.DeleteLikeObservable;
import com.entuition.wekend.data.source.product.ProductFilterOptions;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class CampaignListViewModel extends AbstractViewModel {

    public static final String TAG = CampaignListViewModel.class.getSimpleName();

    public final ObservableBoolean isLoading = new ObservableBoolean();
    public final ObservableBoolean isNoData = new ObservableBoolean();
    public final ObservableBoolean isRefreshing = new ObservableBoolean();
    public final ObservableBoolean isListEnable = new ObservableBoolean();

    public final ObservableArrayList<ProductInfo> productInfos = new ObservableArrayList<>();

    private final UserInfoDataSource userInfoDataSource;
    private final ProductInfoDataSource productInfoDataSource;
    private final LikeInfoDataSource likeInfoDataSource;
    private final WeakReference<CampaignListNavigator> navigator;

    private final ProductInfoDataSource.LoadProductListCallback productListCallback =
            new ProductInfoDataSource.LoadProductListCallback() {
        @Override
        public void onProductListLoaded(List<ProductInfo> infos) {
            productInfos.clear();
            productInfos.addAll(infos);
            completeDataLoad();
        }

        @Override
        public void onDataNotAvailable() {
            completeDataLoad();
        }
    };

    public CampaignListViewModel(Context context, CampaignListNavigator navigator,
                                 UserInfoDataSource userInfoDataSource,
                                 ProductInfoDataSource productInfoDataSource,
                                 LikeInfoDataSource likeInfoDataSource) {
        super(context);

        this.navigator = new WeakReference<CampaignListNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
        this.productInfoDataSource = productInfoDataSource;
        this.likeInfoDataSource = likeInfoDataSource;
        isListEnable.set(true);
        isLoading.set(false);
        isNoData.set(false);
    }

    @Override
    public void onCreate() {
        loadProductInfos(null, null);

        AddLikeObservable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                notifyItemChanged((LikeInfo) data);
            }
        });

        DeleteLikeObservable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                notifyItemChanged((LikeInfo) data);
            }
        });
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onRefresh() {
        isRefreshing.set(true);
        loadProductInfos(null, null);
    }

    public void onClickCampaignItem(ProductInfo info) {
        if (!isListEnable.get()) return;
        if (navigator.get() != null) {
            navigator.get().gotoCampaignDetail(info.getId());
        }
    }

    public void onClickLikeCampaign(ProductInfo info, int position) {
        if (!isListEnable.get()) return;

        if (isLikedCampaign(info.getId())) {
            deleteLike(info, position);
        } else {
            addLike(info, position);
        }
    }

    public void filterProductInfos(final ProductFilterOptions options) {
        loadProductInfos(options, null);
    }

    public void searchProductInfos(final String keyword) {
        loadProductInfos(null, keyword);
    }

    public void loadPaginatedData() {
        isLoading.set(true);
        productInfoDataSource.getPaginatedList(new ProductInfoDataSource.LoadProductListCallback() {
            @Override
            public void onProductListLoaded(List<ProductInfo> infos) {
                productInfos.addAll(infos);
                completeDataLoad();
            }

            @Override
            public void onDataNotAvailable() {
                completeDataLoad();
            }
        });
    }

    public boolean isLikedCampaign(int id) {
        return likeInfoDataSource.containsProductKey(id);
    }

    public String getTitle() {
        return productInfoDataSource.getFilterOptions().getTitle(getApplication());
    }

    private void loadProductInfos(final ProductFilterOptions options, final String keyword) {
        isLoading.set(true);
        likeInfoDataSource.loadLikeInfos(new LikeInfoDataSource.LoadLikeInfoListCallback() {
            @Override
            public void onLikeInfoListLoaded(List<LikeInfo> likes) {
                if (options != null) {
                    productInfoDataSource.loadProductList(options, productListCallback);
                } else if (keyword != null) {
                    productInfoDataSource.loadProductList(keyword, productListCallback);
                } else {
                    productInfoDataSource.loadProductList(productListCallback);
                }
            }

            @Override
            public void onDataNotAvailable() {
                completeDataLoad();
            }
        });
    }

    private void addLike(final ProductInfo productInfo, final int position) {
        isListEnable.set(false);
        userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                likeInfoDataSource.addLike(userInfo, productInfo, new LikeInfoDataSource.AddLikeCallback() {
                    @Override
                    public void onCompleteAddLike(LikeInfo likeInfo) {
                        isListEnable.set(true);
                    }

                    @Override
                    public void onFailedAddLike() {
                        isListEnable.set(true);
                        if (navigator.get() != null) {
                            navigator.get().onFailedAddLike();
                        }
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                isListEnable.set(true);
                if (navigator.get() != null) {
                    navigator.get().onFailedAddLike();
                }
            }
        });
    }

    private void deleteLike(final ProductInfo productInfo, final int position) {
        isListEnable.set(false);
        String userId = userInfoDataSource.getUserId();
        likeInfoDataSource.getLikeInfo(userId, productInfo.getId(), new LikeInfoDataSource.GetLikeInfoCallback() {
            @Override
            public void onLikeInfoLoaded(LikeInfo info) {
                likeInfoDataSource.deleteLike(info, new LikeInfoDataSource.DeleteLikeCallback() {
                    @Override
                    public void onCompleteDeleteLike(LikeInfo info, int remainCount) {
                        isListEnable.set(true);
                    }

                    @Override
                    public void onFailedDeleteLike() {
                        isListEnable.set(true);
                        if (navigator.get() != null) {
                            navigator.get().onFailedDeleteLike();
                        }
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                isListEnable.set(true);
                if (navigator.get() != null) {
                    navigator.get().onFailedDeleteLike();
                }
            }
        });
    }

    private void notifyItemChanged(LikeInfo likeInfo) {
        final ProductInfo productInfo = productInfoDataSource.getProductInfo(likeInfo.getProductId());
        final int position = productInfos.indexOf(productInfo);
        if (position < 0) return;
        if (productInfo != null) {
            likeInfoDataSource.getLikeCount(productInfo.getId(), new LikeInfoDataSource.GetLikeCountCallback() {
                @Override
                public void onFriendCountLoaded(int count) {
                    ProductInfo.setLikeCountToInfo(productInfo, count);
                    if (navigator.get() != null) {
                        navigator.get().onNotifyItemChanged(position);
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    navigator.get().onNotifyItemChanged(position);
                }
            });
        }
    }

    private void completeDataLoad() {
        isLoading.set(false);
        isRefreshing.set(false);

        if (productInfos.size() == 0) {
            isNoData.set(true);
        } else {
            isNoData.set(false);
        }
    }
}

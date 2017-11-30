package com.entuition.wekend.view.main.likelist.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.LikeInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public class RecommendFriendViewModel extends AbstractViewModel {

    public static final String TAG = RecommendFriendViewModel.class.getSimpleName();

    public final ObservableBoolean isLoading = new ObservableBoolean();
    public final ObservableBoolean isRefreshing = new ObservableBoolean();

    public final ObservableArrayList<LikeInfo> friends = new ObservableArrayList<>();

    private final int productId;

    private final WeakReference<RecommendFriendNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;
    private final LikeInfoDataSource likeInfoDataSource;

    public RecommendFriendViewModel(Context context, RecommendFriendNavigator navigator, int productId,
                                    UserInfoDataSource userInfoDataSource,
                                    LikeInfoDataSource likeInfoDataSource) {
        super(context);

        this.productId = productId;
        this.navigator = new WeakReference<RecommendFriendNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
        this.likeInfoDataSource = likeInfoDataSource;

        isLoading.set(false);
        isRefreshing.set(false);
    }

    @Override
    public void onCreate() {
        isLoading.set(true);
    }

    @Override
    public void onResume() {
        loadFriends();
    }

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onRefresh() {
        isRefreshing.set(true);
        loadFriends();
    }

    public void onClickItem(LikeInfo info) {

        likeInfoDataSource.readFriend(info);

        String userId = userInfoDataSource.getUserId();
        navigator.get().gotoProfileView(userId, info.getUserId(), info.getProductId());
    }

    private void loadFriends() {
        userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                likeInfoDataSource.loadLikeInfos(productId, userInfo.getGender(), new LikeInfoDataSource.LoadLikeInfoListCallback() {
                    @Override
                    public void onLikeInfoListLoaded(List<LikeInfo> likeInfos) {

                        friends.clear();
                        friends.addAll(likeInfos);

                        isLoading.set(false);
                        isRefreshing.set(false);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        isLoading.set(false);
                        isRefreshing.set(false);
                    }
                });
            }
            @Override
            public void onDataNotAvailable() {
                isLoading.set(false);
                isRefreshing.set(false);
            }
        });
    }
}

package com.entuition.wekend.view.main.campaign.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.LikeInfoDataSource;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.ImageUtils;
import com.entuition.wekend.view.common.AbstractViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 11. 12..
 */

public class CampaignDetailViewModel extends AbstractViewModel {

    public static final String TAG = CampaignDetailViewModel.class.getSimpleName();

    public final ObservableField<ProductInfo> productInfo = new ObservableField<>();
    public final ObservableField<String> buttonText = new ObservableField<>();
    public final ObservableBoolean isStateLoading = new ObservableBoolean();
    public final ObservableBoolean isButtonEnable = new ObservableBoolean();

    private boolean isLikeProduct;

    private final int productId;
    private final UserInfoDataSource userInfoDataSource;
    private final ProductInfoDataSource productInfoDataSource;
    private final LikeInfoDataSource likeInfoDataSource;
    private final WeakReference<CampaignDetailNavigator> navigator;

    // For FaceBook
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    public CampaignDetailViewModel(Context context, CampaignDetailNavigator navigator, int productId,
                                   UserInfoDataSource userInfoDataSource,
                                   ProductInfoDataSource productInfoDataSource,
                                   LikeInfoDataSource likeInfoDataSource) {
        super(context);

        this.isLikeProduct = false;
        this.productId = productId;
        this.navigator = new WeakReference<CampaignDetailNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
        this.productInfoDataSource = productInfoDataSource;
        this.likeInfoDataSource = likeInfoDataSource;

        isStateLoading.set(false);
        isButtonEnable.set(false);
    }

    @Override
    public void onCreate() {
        productInfoDataSource.getProductInfo(productId, new ProductInfoDataSource.GetProductCallback() {
            @Override
            public void onProductInfoLoaded(ProductInfo info) {
                productInfo.set(info);
                loadLikeStatus(productId);
            }

            @Override
            public void onDataNotAvailable() {}
        });
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setFacebookShare(Activity activity) {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {}

            @Override
            public void onCancel() {
                if (navigator.get() != null) {
                    navigator.get().onCancelFacebookLink();
                }
            }

            @Override
            public void onError(FacebookException error) {
                if (navigator.get() != null) {
                    navigator.get().onErrorFacebookLink();
                }
            }
        });
    }

    public void sendKakaoTalkLink(Activity activity) {

        String imageUrl = ImageUtils.getCampaignImageUrl(productInfo.get().getId(), 0);

        LinkObject linkObject = LinkObject
                .newBuilder()
                .setWebUrl("https://fb.me/673785809486815")
                .setMobileWebUrl("https://fb.me/673785809486815")
                .setAndroidExecutionParams("productId=" + productInfo.get().getId())
                .setIosExecutionParams("productId=" + productInfo.get().getId())
                .build();

        ContentObject contentObject = ContentObject
                .newBuilder(productInfo.get().getTitleKor(), imageUrl, linkObject)
                .setDescrption(productInfo.get().getDescription())
                .setImageHeight(200)
                .setImageWidth(300)
                .build();

        int likeCount = productInfo.get().getLikeCount() / ProductInfo.LIKE_COUNT_DELIMETER;
        SocialObject socialObject = SocialObject
                .newBuilder()
                .setLikeCount(likeCount)
                .build();

        ButtonObject buttonObject = new ButtonObject("앱으로 보기", linkObject);

        FeedTemplate params = FeedTemplate
                .newBuilder(contentObject)
                .setSocial(socialObject)
                .addButton(buttonObject)
                .build();

        KakaoLinkService.getInstance().sendDefault(activity, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e(TAG, "KakaoLinkService > sendDefault > onFailure > errorResult : " + errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                Log.d(TAG, "KakaoLinkService > sendDefault > onSuccess > result : " + result.toString());
            }
        });
    }

    public void sendFacebookLink() {

        String imageUrl = ImageUtils.getCampaignImageUrl(productInfo.get().getId(), 0);

        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "article")
                .putString("og:title", productInfo.get().getTitleKor())
                .putString("og:description", productInfo.get().getDescription())
                .putString("og:image", imageUrl)
                .putString("og:url", "https://fb.me/673785809486815?productId=" + productInfo.get().getId())
                .build();

        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("news.publishes")
                .putObject("article", object)
                .build();

        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("article")
                .setAction(action)
                .build();

        shareDialog.show(content);
    }

    // Click Listeners
    public void onClickPhone() {
        if (navigator.get() != null) {
            navigator.get().call(productInfo.get().getTelephone());
        }
    }

    public void onClickAddress() {
        if (navigator.get() != null) {
            navigator.get().gotoGoogleMapView(productInfo.get().getTitleKor(), productInfo.get().getAddress());
        }
    }

    public void onClickLikeButton() {
        if (isLikeProduct) {
            if (navigator.get() != null) {
                navigator.get().gotoRecommendFriendView(productInfo.get().getId());
            }
        } else {
            isStateLoading.set(true);
            userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
                @Override
                public void onUserInfoLoaded(UserInfo userInfo) {
                    likeInfoDataSource.addLike(userInfo, productInfo.get(), new LikeInfoDataSource.AddLikeCallback() {
                        @Override
                        public void onCompleteAddLike(LikeInfo info) {
                            loadLikeStatus(info.getProductId());
                        }

                        @Override
                        public void onFailedAddLike() {
                            if (navigator.get() != null) {
                                navigator.get().onFailedAddLike();
                            }
                            isStateLoading.set(false);
                        }
                    });
                }

                @Override
                public void onDataNotAvailable() {
                    if (navigator.get() != null) {
                        navigator.get().onFailedAddLike();
                    }
                    isStateLoading.set(false);
                }

                @Override
                public void onError() {}
            });
        }
    }

    private void loadLikeStatus(final int productId) {
        isStateLoading.set(true);
        userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(final UserInfo userInfo) {
                likeInfoDataSource.getLikeInfo(userInfo.getUserId(), productId, new LikeInfoDataSource.GetLikeInfoCallback() {
                    @Override
                    public void onLikeInfoLoaded(LikeInfo info) {
                        likeInfoDataSource.getLikeCount(productId, userInfo.getGender(), new LikeInfoDataSource.GetLikeCountCallback() {
                            @Override
                            public void onFriendCountLoaded(int count) {
                                buttonText.set(getApplication().getString(R.string.recommend_friends_count, count));
                                isStateLoading.set(false);
                                isButtonEnable.set(true);
                                isLikeProduct = true;
                            }

                            @Override
                            public void onDataNotAvailable() {
                                buttonText.set(getApplication().getString(R.string.button_like));
                                isStateLoading.set(false);
                                isButtonEnable.set(true);
                                isLikeProduct = false;
                            }
                        });
                    }

                    @Override
                    public void onDataNotAvailable() {
                        buttonText.set(getApplication().getString(R.string.button_like));
                        isStateLoading.set(false);
                        isButtonEnable.set(true);
                        isLikeProduct = false;
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                buttonText.set(getApplication().getString(R.string.button_like));
                isStateLoading.set(false);
                isButtonEnable.set(true);
                isLikeProduct = false;
            }

            @Override
            public void onError() {
                buttonText.set(getApplication().getString(R.string.button_like));
                isStateLoading.set(false);
                isButtonEnable.set(true);
                isLikeProduct = false;
            }
        });
    }
}

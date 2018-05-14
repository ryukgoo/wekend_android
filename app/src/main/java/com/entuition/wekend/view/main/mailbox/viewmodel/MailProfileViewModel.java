package com.entuition.wekend.view.main.mailbox.viewmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.data.google.billing.GoogleBillingController;
import com.entuition.wekend.data.google.billing.HasSubscriptionObserverable;
import com.entuition.wekend.data.source.mail.IMail;
import com.entuition.wekend.data.source.mail.MailDataSource;
import com.entuition.wekend.data.source.mail.MailType;
import com.entuition.wekend.data.source.mail.ProposeStatus;
import com.entuition.wekend.data.source.mail.ReceiveMail;
import com.entuition.wekend.data.source.mail.SendMail;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.Constants.ReadState;
import com.entuition.wekend.util.Utilities;
import com.entuition.wekend.view.common.AbstractViewModel;
import com.entuition.wekend.view.main.setting.adapter.ProfileViewPagerListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public class MailProfileViewModel extends AbstractViewModel implements ProfileViewPagerListener {

    public static final String TAG = MailProfileViewModel.class.getSimpleName();

    public final ObservableField<IMail> mail = new ObservableField<>();
    public final ObservableField<UserInfo> user = new ObservableField<>();
    public final ObservableField<UserInfo> friend = new ObservableField<>();
    public final ObservableArrayList<String> photos = new ObservableArrayList<>();
    public final ObservableField<String> phone = new ObservableField<>();
    public final ObservableField<ProductInfo> product = new ObservableField<>();
    public final ObservableField<String> point = new ObservableField<>();

    public final ObservableBoolean isStatusLoading = new ObservableBoolean();
    public final ObservableBoolean isButtonVisible = new ObservableBoolean();
    public final ObservableBoolean isMatchTextVisible = new ObservableBoolean();
    public final ObservableBoolean isMessageVisible = new ObservableBoolean();

    public final ObservableField<MailType> mailType = new ObservableField<>();
    public final ObservableField<ProposeStatus> status = new ObservableField<>();

    private final WeakReference<MailProfileNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;
    private final ProductInfoDataSource productInfoDataSource;
    private final MailDataSource mailDataSource;

    // GoogleBillingController
    private final GoogleBillingController billingController;

    private String userId;
    private String friendId;
    private int productId;

    public MailProfileViewModel(Context context, MailProfileNavigator navigator,
                                UserInfoDataSource userInfoDataSource,
                                ProductInfoDataSource productInfoDataSource,
                                MailDataSource mailDataSource) {
        super(context);

        this.navigator = new WeakReference<MailProfileNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
        this.productInfoDataSource = productInfoDataSource;
        this.mailDataSource = mailDataSource;

        this.billingController = GoogleBillingController.getInstance(context);

        isStatusLoading.set(false);
        isButtonVisible.set(false);
        isMatchTextVisible.set(false);
        isMessageVisible.set(false);
        status.set(ProposeStatus.notMade);
        point.set("");
    }

    @Override
    public void onCreate() {
        userInfoDataSource.getUserInfo(friendId, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(final UserInfo userInfo) {
                friend.set(userInfo);
                photos.addAll(userInfo.getPhotos() == null ? new ArrayList<String>(0) : userInfo.getPhotos());
                productInfoDataSource.getProductInfo(productId, new ProductInfoDataSource.GetProductCallback() {
                    @Override
                    public void onProductInfoLoaded(ProductInfo info) {
                        product.set(info);
                        isStatusLoading.set(true);
                        mailDataSource.getMail(userId, userInfo.getUserId(), info.getId(), new MailDataSource.GetMailCallback() {
                            @Override
                            public void onMailLoaded(IMail iMail) {
                                isStatusLoading.set(false);

                                mail.set(iMail);
                                status.set(ProposeStatus.valueOf(iMail.getStatus()));
                                validateStatus();
                            }

                            @Override
                            public void onDataNotAvailable() {
                                isStatusLoading.set(false);

                                status.set(ProposeStatus.none);
//                                validateStatus();
                            }
                        });
                    }

                    @Override
                    public void onDataNotAvailable() {
                        Log.d(TAG, "onDataNotAvailable");
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "onDataNotAvailable");
            }

            @Override
            public void onError() {}
        });

        loadUserInfo();

        HasSubscriptionObserverable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                loadUserInfo();
            }
        });
    }

    private void loadUserInfo() {
        userInfoDataSource.getUserInfo(userId, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                user.set(userInfo);
            }

            @Override
            public void onDataNotAvailable() {}

            @Override
            public void onError() {}
        });

        GoogleBillingController.getInstance(getApplication()).checkSubcribing(new GoogleBillingController.OnValidateSubcribe() {
            @Override
            public void onValidateSubcribed(boolean isSubcribed, UserInfo userInfo) {
                if (isSubcribed) {
                    point.set(getApplication().getString(R.string.subscription_enabled));
                } else {
                    if (userInfo != null) {
                        point.set(getApplication().getString(R.string.formatted_point, userInfo.getBalloon()));
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onClickPagerItem(String photo) {}

    public void setIntent(Intent intent) {
        userId = intent.getStringExtra(Constants.ExtraKeys.USER_ID);
        friendId = intent.getStringExtra(Constants.ExtraKeys.FRIEND_ID);
        productId = intent.getIntExtra(Constants.ExtraKeys.PRODUCT_ID, -1);
        mailType.set(MailType.valueOf(intent.getStringExtra(Constants.ExtraKeys.MAIL_TYPE)));
    }

    public void onClickShowProduct() {
        if (navigator.get() != null) {
            navigator.get().gotoCampaignDetail(productId);
        }
    }

    public void onClickProposeButton() {

        String purchaseTimestamp = user.get().getPurchaseTime();
        String expiresTimestamp = user.get().getExpiresTime();

        if (purchaseTimestamp != null && expiresTimestamp != null) {

            long purchaseTimeMillis = Utilities.getDateFromTimeStamp(purchaseTimestamp).getTime();
            long expiresTimeMillis = Utilities.getDateFromTimeStamp(expiresTimestamp).getTime();
            long now = new Date().getTime();

            Log.d(TAG, "purchaseTime : " + purchaseTimeMillis);
            Log.d(TAG, "expiresTime : " + expiresTimeMillis);
            Log.d(TAG, "nowTime : " + now);

            if (expiresTimeMillis >= now) {
                if (navigator.get() != null) { navigator.get().showUserInputDialog(); }
                return;
            }
        }

        Log.d(TAG, "subcribe is not available");

        if (navigator.get() != null) {
            navigator.get().confirmPropose(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        if (isEnoughRemainedPoint()) {
                            navigator.get().showUserInputDialog();
                        } else {
                            navigator.get().showNotEnoughPoint();
                        }
                    }
                }
            });
        }
    }

    public void propose(final String message) {

        isStatusLoading.set(true);
        SendMail sendMail = new SendMail();
        sendMail.setUserId(userId);
        sendMail.setSenderNickname(user.get().getNickname());
        sendMail.setReceiverId(friendId);
        sendMail.setReceiverNickname(friend.get().getNickname());
        sendMail.setProductId(productId);
        sendMail.setProductTitle(product.get().getTitleKor());
        sendMail.setStatus(ProposeStatus.notMade.toString());
        sendMail.setMessage(message);
        sendMail.setIsRead(ReadState.read.ordinal());
        sendMail.setUpdatedTime(Utilities.getTimestamp());
        sendMail.setResponseTime(sendMail.getUpdatedTime());

        mailDataSource.updateMail(sendMail, new MailDataSource.UpdateMailCallback() {
            @Override
            public void onCompleteUpdateMail(final IMail iMail) {
                userInfoDataSource.consumePoint(Constants.CONSUME_POINT, new UserInfoDataSource.ConsumePointCallback() {
                    @Override
                    public void onConsumePointComplete(UserInfo userInfo) {

                        user.set(userInfo);
                        isStatusLoading.set(false);

                        mail.set(iMail);
                        status.set(ProposeStatus.valueOf(iMail.getStatus()));
                        validateStatus();
                        if (navigator.get() != null) {
                            navigator.get().onProposeComplete(iMail.getFriendNickname());
                        }
                    }

                    @Override
                    public void onPointNotEnough() {
                        isStatusLoading.set(false);
                        if (navigator.get() != null) {
                            navigator.get().showNotEnoughPoint();
                        }
                    }

                    @Override
                    public void onConsumeNotAvailable() {
                        isStatusLoading.set(false);
                        if (navigator.get() != null) {
                            navigator.get().showTryAgain();
                        }
                    }
                });
            }

            @Override
            public void onFailedUpdateMail() {
                isStatusLoading.set(false);
                if (navigator.get() != null) {
                    navigator.get().showTryAgain();
                }
            }
        });
    }

    public void onClickAcceptButton() {
        Log.d(TAG, "onClickAcceptButton");

        if (mail.get().getMailType() == MailType.receive) {

            ReceiveMail receiveMail = (ReceiveMail) mail.get();
            receiveMail.setStatus(ProposeStatus.Made.toString());
            receiveMail.setIsRead(ReadState.read.ordinal());
            receiveMail.setResponseTime(Utilities.getTimestamp());

            Log.d(TAG, "onClickAcceptButton > message : " + mail.get().getMessage());

            mailDataSource.updateMail(mail.get(), new MailDataSource.UpdateMailCallback() {
                @Override
                public void onCompleteUpdateMail(IMail iMail) {
                    mail.set(iMail);
                    status.set(ProposeStatus.valueOf(iMail.getStatus()));
                    validateStatus();
                    if (navigator.get() != null) {
                        navigator.get().onAcceptComplete(mail.get().getFriendNickname());
                    }
                }

                @Override
                public void onFailedUpdateMail() {

                }
            });
        }
    }

    public void onClickRejectButton() {
        Log.d(TAG, "onClickRejectButton");

        if (mail.get().getMailType() == MailType.receive) {

            ReceiveMail receiveMail = (ReceiveMail) mail.get();
            receiveMail.setStatus(ProposeStatus.reject.toString());
            receiveMail.setIsRead(ReadState.read.ordinal());
            receiveMail.setResponseTime(Utilities.getTimestamp());

            mailDataSource.updateMail(receiveMail, new MailDataSource.UpdateMailCallback() {
                @Override
                public void onCompleteUpdateMail(IMail iMail) {
                    mail.set(iMail);
                    status.set(ProposeStatus.valueOf(iMail.getStatus()));
                    validateStatus();
                    if (navigator.get() != null) {
                        navigator.get().onRejectComplete(mail.get().getFriendNickname());
                    }
                }

                @Override
                public void onFailedUpdateMail() { }
            });
        }
    }

    private void validateStatus() {
        boolean receiveAndNotMade = mailType.get() == MailType.receive && status.get() == ProposeStatus.notMade;
        isButtonVisible.set(receiveAndNotMade);
        isMatchTextVisible.set(!receiveAndNotMade);
        isMessageVisible.set(status.get() != ProposeStatus.none);

        switch (status.get()) {
            case Made:
                phone.set(friend.get().getPhone());
                break;
        }
    }

    private boolean isEnoughRemainedPoint() {
        return user.get().getBalloon() >= Constants.CONSUME_POINT;
    }
}

package com.entuition.wekend.view.main.mailbox.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;

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

    public final ObservableBoolean isStatusLoading = new ObservableBoolean();
    public final ObservableBoolean isButtonVisible = new ObservableBoolean();
    public final ObservableBoolean isMatchTextVisible = new ObservableBoolean();

    public final ObservableField<MailType> mailType = new ObservableField<>();
    public final ObservableField<ProposeStatus> status = new ObservableField<>();

    private final WeakReference<MailProfileNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;
    private final ProductInfoDataSource productInfoDataSource;
    private final MailDataSource mailDataSource;

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

        isStatusLoading.set(false);
        isButtonVisible.set(false);
        isMatchTextVisible.set(false);
        status.set(ProposeStatus.notMade);
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
        });

        userInfoDataSource.getUserInfo(userId, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                user.set(userInfo);
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

    @Override
    public void onClickPagerItem(String photo) {}

    public void setIntent(Intent intent) {
        userId = intent.getStringExtra(Constants.ExtraKeys.USER_ID);
        friendId = intent.getStringExtra(Constants.ExtraKeys.FRIEND_ID);
        productId = intent.getIntExtra(Constants.ExtraKeys.PRODUCT_ID, -1);
        mailType.set(MailType.valueOf(intent.getStringExtra(Constants.ExtraKeys.MAIL_TYPE)));
    }

    public void onClickProposeButton() {
        navigator.get().confirmPropose();
    }

    public void propose() {
        isStatusLoading.set(true);
        userInfoDataSource.consumePoint(500, new UserInfoDataSource.ConsumePointCallback() {
            @Override
            public void onConsumePointComplete(UserInfo userInfo) {

                user.set(userInfo);

                SendMail sendMail = new SendMail();
                sendMail.setUserId(userId);
                sendMail.setSenderNickname(userInfo.getNickname());
                sendMail.setReceiverId(friendId);
                sendMail.setReceiverNickname(friend.get().getNickname());
                sendMail.setProductId(productId);
                sendMail.setProductTitle(product.get().getTitleKor());
                sendMail.setStatus(ProposeStatus.notMade.toString());
                sendMail.setIsRead(ReadState.read.ordinal());
                sendMail.setUpdatedTime(Utilities.getTimestamp());
                sendMail.setResponseTime(sendMail.getUpdatedTime());

                mailDataSource.updateMail(sendMail, new MailDataSource.UpdateMailCallback() {
                    @Override
                    public void onCompleteUpdateMail(IMail iMail) {
                        Log.d(TAG, "onCompleteUpdateMail");
                        isStatusLoading.set(false);

                        mail.set(iMail);
                        status.set(ProposeStatus.valueOf(iMail.getStatus()));
                        validateStatus();
                        navigator.get().onProposeComplete(iMail.getFriendNickname());
                    }

                    @Override
                    public void onFailedUpdateMail() {
                        Log.d(TAG, "onFailedUpdateMail");
                        isStatusLoading.set(false);
                        // TODO : Error!!!! restore point????
                    }
                });
            }

            @Override
            public void onPointNotEnough() {
                navigator.get().showNotEnoughPoint();
                isStatusLoading.set(false);
            }

            @Override
            public void onConsumeNotAvailable() {
                navigator.get().showTryAgain();
                isStatusLoading.set(false);
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

            mailDataSource.updateMail(mail.get(), new MailDataSource.UpdateMailCallback() {
                @Override
                public void onCompleteUpdateMail(IMail iMail) {
                    mail.set(iMail);
                    status.set(ProposeStatus.valueOf(iMail.getStatus()));
                    validateStatus();
                    navigator.get().onAcceptComplete(mail.get().getFriendNickname());
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
            receiveMail.setStatus(ProposeStatus.Made.toString());
            receiveMail.setIsRead(ReadState.read.ordinal());
            receiveMail.setResponseTime(Utilities.getTimestamp());

            mailDataSource.updateMail(receiveMail, new MailDataSource.UpdateMailCallback() {
                @Override
                public void onCompleteUpdateMail(IMail iMail) {
                    mail.set(iMail);
                    status.set(ProposeStatus.valueOf(iMail.getStatus()));
                    validateStatus();
                    navigator.get().onRejectComplete(mail.get().getFriendNickname());
                }

                @Override
                public void onFailedUpdateMail() {

                }
            });
        }
    }

    private void validateStatus() {
        boolean receiveAndNotMade = mailType.get() == MailType.receive && status.get() == ProposeStatus.notMade;
        isButtonVisible.set(receiveAndNotMade);
        isMatchTextVisible.set(!receiveAndNotMade);

        switch (status.get()) {
            case Made:
                phone.set(friend.get().getPhone());
                break;
        }
    }
}
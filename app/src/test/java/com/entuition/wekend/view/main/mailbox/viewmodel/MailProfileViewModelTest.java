package com.entuition.wekend.view.main.mailbox.viewmodel;

import android.content.Context;

import com.entuition.wekend.data.source.mail.MailDataSource;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Created by ryukgoo on 2018. 1. 31..
 */
public class MailProfileViewModelTest {

    private Context contextMock;

    private MailProfileNavigator navigatorMock;

    private UserInfoDataSource userInfoDataSourceMock;

    private ProductInfoDataSource productInfoDataSourceMock;

    private MailDataSource mailDataSourceMock;

    private MailProfileViewModel viewModel;

    @Before
    public void setUp() throws Exception {

        contextMock = mock(Context.class);
        navigatorMock = mock(MailProfileNavigator.class);
        userInfoDataSourceMock = mock(UserInfoDataSource.class);
        productInfoDataSourceMock = mock(ProductInfoDataSource.class);
        mailDataSourceMock = mock(MailDataSource.class);

        viewModel = new MailProfileViewModel(contextMock, navigatorMock,
                userInfoDataSourceMock, productInfoDataSourceMock, mailDataSourceMock);

    }

    @Test
    public void loadFriendTest() {

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {

                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];
                callback.onUserInfoLoaded(new UserInfo());
                return null;
            }
        }).when(userInfoDataSourceMock).getUserInfo((String) isNull(), any(UserInfoDataSource.GetUserInfoCallback.class));

        viewModel.onCreate();


    }

}
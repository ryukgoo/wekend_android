package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.content.Intent;

import com.entuition.wekend.data.source.authentication.AuthenticationDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by ryukgoo on 2018. 1. 29..
 */

public class LaunchViewModelTest {

    private Context contextMock;

    private LaunchNavigator navigatorMock;

    private AuthenticationDataSource authenticationDataSourceMock;

    private UserInfoDataSource userInfoDataSourceMock;

    private LaunchViewModel viewModel;

    @Before
    public void setUp() throws Exception {

        contextMock = mock(Context.class);
        navigatorMock = mock(LaunchNavigator.class);
        authenticationDataSourceMock = mock(AuthenticationDataSource.class);
        userInfoDataSourceMock = mock(UserInfoDataSource.class);

        viewModel = spy(new LaunchViewModel(contextMock, navigatorMock, authenticationDataSourceMock, userInfoDataSourceMock));

    }

    @Test
    public void launchSuccessTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthenticationDataSource.GetTokenCallback callback = (AuthenticationDataSource.GetTokenCallback) invocation.getArguments()[0];
                callback.onCompleteGetToken();
                return null;
            }
        }).when(authenticationDataSourceMock).getToken(any(AuthenticationDataSource.GetTokenCallback.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];
                callback.onUserInfoLoaded(new UserInfo());
                return null;
            }
        }).when(userInfoDataSourceMock).getUserInfo((String) isNull(), any(UserInfoDataSource.GetUserInfoCallback.class));

        viewModel.parseIntent(new Intent());
        viewModel.onCreate();

        verify(userInfoDataSourceMock).getUserInfo((String) isNull(), any(UserInfoDataSource.GetUserInfoCallback.class));
        verify(navigatorMock).onAutoLogin(anyInt());
    }

    @Test
    public void getTokenFailedTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthenticationDataSource.GetTokenCallback callback = (AuthenticationDataSource.GetTokenCallback) invocation.getArguments()[0];
                callback.onFailedGetToken();
                return null;
            }
        }).when(authenticationDataSourceMock).getToken(any(AuthenticationDataSource.GetTokenCallback.class));

        viewModel.onCreate();

        verify(navigatorMock).onLoginView();
    }

    @Test
    public void getUserInfoFailedTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthenticationDataSource.GetTokenCallback callback = (AuthenticationDataSource.GetTokenCallback) invocation.getArguments()[0];
                callback.onCompleteGetToken();
                return null;
            }
        }).when(authenticationDataSourceMock).getToken(any(AuthenticationDataSource.GetTokenCallback.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];
                callback.onDataNotAvailable();
                return null;
            }
        }).when(userInfoDataSourceMock).getUserInfo((String) isNull(), any(UserInfoDataSource.GetUserInfoCallback.class));

        viewModel.onCreate();

        verify(navigatorMock).onLoginView();
    }

    @Test
    public void launchWithLinkTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthenticationDataSource.GetTokenCallback callback = (AuthenticationDataSource.GetTokenCallback) invocation.getArguments()[0];
                callback.onCompleteGetToken();
                return null;
            }
        }).when(authenticationDataSourceMock).getToken(any(AuthenticationDataSource.GetTokenCallback.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];
                callback.onUserInfoLoaded(new UserInfo());
                return null;
            }
        }).when(userInfoDataSourceMock).getUserInfo((String) isNull(), any(UserInfoDataSource.GetUserInfoCallback.class));

        viewModel.linkProductId = "123";
        viewModel.notificationType = 1;
        viewModel.onCreate();

        verify(navigatorMock).onReceiveLink(anyInt());
    }
}
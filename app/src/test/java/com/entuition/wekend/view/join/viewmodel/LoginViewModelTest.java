package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.view.View;

import com.entuition.wekend.data.source.authentication.AuthenticationDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryukgoo on 2018. 1. 29..
 */
public class LoginViewModelTest {

    private Context contextMock;

    private UserInfoDataSource userDataSourceMock;

    private LoginNavigator navigatorMock;

    private AuthenticationDataSource authenticationDataSourceMock;

    private LoginViewModel viewModel;

    @Before
    public void setUp() throws Exception {

        contextMock = mock(Context.class);
        userDataSourceMock = mock(UserInfoDataSource.class);
        navigatorMock = mock(LoginNavigator.class);
        authenticationDataSourceMock = mock(AuthenticationDataSource.class);

        viewModel = spy(new LoginViewModel(contextMock, navigatorMock, authenticationDataSourceMock, userDataSourceMock));
    }

    @Test
    public void loginTest() {

        when(viewModel.isValidInput()).thenReturn(true);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthenticationDataSource.LoginCallback callback = (AuthenticationDataSource.LoginCallback) invocation.getArguments()[2];
                callback.onCompleteLogin();
                System.out.println("onCompleteLogin");
                return null;
            }
        }).when(authenticationDataSourceMock).login(anyString(), anyString(), any(AuthenticationDataSource.LoginCallback.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];
                callback.onUserInfoLoaded(new UserInfo());
                System.out.println("onUserInfoLoaded");
                return null;
            }
        }).when(userDataSourceMock).getUserInfo((String) isNull(), any(UserInfoDataSource.GetUserInfoCallback.class));

        viewModel.onCreate();
        viewModel.email.set("email@gmail.com");
        viewModel.password.set("asdf00");
        viewModel.onClickLoginButton(any(View.class));

        assertFalse(viewModel.isLoading.get());
        verify(navigatorMock, never()).showInvalidInput();
        verify(navigatorMock).onCompleteLogin();
        verify(authenticationDataSourceMock).login(anyString(), anyString(), any(AuthenticationDataSource.LoginCallback.class));
        verify(userDataSourceMock).getUserInfo((String) isNull(), any(UserInfoDataSource.GetUserInfoCallback.class));
    }

    @Test
    public void invalidInputTest() {
        when(viewModel.isValidInput()).thenReturn(false);
        viewModel.onClickLoginButton(any(View.class));
        assertFalse(viewModel.isLoading.get());
        verify(navigatorMock).showInvalidInput();
    }

}
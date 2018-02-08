package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.view.View;

import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by ryukgoo on 2018. 2. 6..
 */
public class InputUserInfoViewModelTest {

    private Context contextMock;

    private UserInfoDataSource dataSourceMock;

    private InputUserInfoNavigator navigatorMock;

    private InputUserInfoViewModel viewModel;

    @Before
    public void setUp() throws Exception {

        contextMock = mock(Context.class);
        dataSourceMock = mock(UserInfoDataSource.class);
        navigatorMock = mock(InputUserInfoNavigator.class);

        viewModel = spy(new InputUserInfoViewModel(contextMock, navigatorMock, dataSourceMock));

    }

    @Test
    public void duplicatedNicknameTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];
                callback.onDataNotAvailable();
                return null;
            }
        }).when(dataSourceMock).searchUserInfoFromNickname(anyString(), any(UserInfoDataSource.GetUserInfoCallback.class));

        viewModel.onCreate();
        viewModel.nickname.set("nickname");
        viewModel.onClickCheckNicknameButton(any(View.class));
        viewModel.onClickNextButton(any(View.class));

        verify(navigatorMock).showAvailableNickname();
        verify(navigatorMock).onClickNextButton(anyString(), anyString());

    }

}
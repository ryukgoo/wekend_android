package com.entuition.wekend.view.main.setting.viewmodel;

import android.content.Context;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by ryukgoo on 2018. 1. 31..
 */
public class SettingProfileViewModelTest {

    private Context contextMock;

    private SettingProfileNavigator navigatorMock;

    private UserInfoDataSource userInfoDataSourceMock;

    private SettingProfileViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        contextMock = mock(Context.class);
        navigatorMock = mock(SettingProfileNavigator.class);
        userInfoDataSourceMock = mock(UserInfoDataSource.class);

        viewModel = spy(new SettingProfileViewModel(contextMock, navigatorMock, userInfoDataSourceMock));
    }

    @Test
    public void loadTest() {

        when(userInfoDataSourceMock.getUserId()).thenReturn("id");

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];

                UserInfo user = new UserInfo();
                user.setIntroduce("introduce");
                user.setCompany("company");
                user.setSchool("school");
                user.setArea("area");
                user.setPhone("phone");

                callback.onUserInfoLoaded(user);

                return null;
            }
        }).when(userInfoDataSourceMock).getUserInfo(anyString(), any(UserInfoDataSource.GetUserInfoCallback.class));

        viewModel.onResume();

        assertEquals("introduce", viewModel.user.get().getIntroduce());
        assertEquals("company", viewModel.user.get().getCompany());
        assertEquals("school", viewModel.user.get().getSchool());
        assertEquals("area", viewModel.user.get().getArea());
        assertEquals("phone", viewModel.user.get().getPhone());

    }

}
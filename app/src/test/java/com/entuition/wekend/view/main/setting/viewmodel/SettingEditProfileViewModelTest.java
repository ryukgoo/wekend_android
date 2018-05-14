package com.entuition.wekend.view.main.setting.viewmodel;

import android.content.Context;
import android.view.View;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryukgoo on 2018. 1. 29..
 */
public class SettingEditProfileViewModelTest {

    private Context contextMock;

    private SettingEditProfileNavigator navigatorMock;

    private UserInfoDataSource userInfoDataSourceMock;

    private SettingEditProfileViewModel viewModel;

    @Before
    public void setUp() throws Exception {

        contextMock = mock(Context.class);
        navigatorMock = mock(SettingEditProfileNavigator.class);
        userInfoDataSourceMock = mock(UserInfoDataSource.class);

        viewModel = spy(new SettingEditProfileViewModel(contextMock, navigatorMock, userInfoDataSourceMock));

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

        viewModel.onCreate();

        assertEquals("introduce", viewModel.introduce.get());
        assertEquals("company", viewModel.company.get());
        assertEquals("school", viewModel.school.get());
        assertEquals("area", viewModel.area.get());
        assertEquals("phone", viewModel.phone.get());

    }

    @Test
    public void editTest() {

        doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "123";
            }
        }).when(userInfoDataSourceMock).getUserId();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfo user = new UserInfo();
                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];
                callback.onUserInfoLoaded(user);
                return null;
            }
        }).when(userInfoDataSourceMock).getUserInfo(anyString(), any(UserInfoDataSource.GetUserInfoCallback.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfo user = (UserInfo) invocation.getArguments()[0];
                UserInfoDataSource.UpdateUserInfoCallback callback = (UserInfoDataSource.UpdateUserInfoCallback) invocation.getArguments()[1];

                user.setIntroduce(viewModel.introduce.get());
                user.setCompany(viewModel.company.get());
                user.setSchool(viewModel.school.get());
                user.setArea(viewModel.area.get());

                callback.onUpdateComplete(user);
                return null;
            }
        }).when(userInfoDataSourceMock).updateUserInfo(any(UserInfo.class), any(UserInfoDataSource.UpdateUserInfoCallback.class));

        viewModel.introduce.set("introduce");
        viewModel.company.set("company");
        viewModel.school.set("school");
        viewModel.area.set("area");
        viewModel.editDone();

        verify(userInfoDataSourceMock).getUserInfo(anyString(), any(UserInfoDataSource.GetUserInfoCallback.class));
        verify(userInfoDataSourceMock).updateUserInfo(any(UserInfo.class), any(UserInfoDataSource.UpdateUserInfoCallback.class));

        assertEquals("introduce", viewModel.user.get().getIntroduce());
        assertEquals("company", viewModel.user.get().getCompany());
        assertEquals("school", viewModel.user.get().getSchool());
        assertEquals("area", viewModel.user.get().getArea());

        verify(navigatorMock).dismiss();
    }

    @Test
    public void editFailedTest() {

        doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "123";
            }
        }).when(userInfoDataSourceMock).getUserId();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfo user = new UserInfo();
                UserInfoDataSource.GetUserInfoCallback callback = (UserInfoDataSource.GetUserInfoCallback) invocation.getArguments()[1];
                callback.onUserInfoLoaded(user);
                return null;
            }
        }).when(userInfoDataSourceMock).getUserInfo(anyString(), any(UserInfoDataSource.GetUserInfoCallback.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.UpdateUserInfoCallback callback = (UserInfoDataSource.UpdateUserInfoCallback) invocation.getArguments()[1];
                callback.onUpdateFailed();
                return null;
            }
        }).when(userInfoDataSourceMock).updateUserInfo(any(UserInfo.class), any(UserInfoDataSource.UpdateUserInfoCallback.class));

        viewModel.editDone();

        verify(userInfoDataSourceMock).getUserInfo(anyString(), any(UserInfoDataSource.GetUserInfoCallback.class));
        verify(userInfoDataSourceMock).updateUserInfo(any(UserInfo.class), any(UserInfoDataSource.UpdateUserInfoCallback.class));
        verify(navigatorMock).showUpdateError();

    }

    @Test
    public void requestCodeTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.RequestCodeCallback callback = (UserInfoDataSource.RequestCodeCallback) invocation.getArguments()[1];
                callback.onReceiveCode("123456");
                return null;
            }
        }).when(userInfoDataSourceMock).requestVerificationCode(anyString(), any(UserInfoDataSource.RequestCodeCallback.class));

        viewModel.phone.set("01000000000");
        viewModel.onClickRequestCode(any(View.class));

        assertFalse(viewModel.isVaildPhone.get());
        verify(navigatorMock).showRequestCode();
    }

    @Test
    public void requestCodeFailedTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.RequestCodeCallback callback = (UserInfoDataSource.RequestCodeCallback) invocation.getArguments()[1];
                callback.onFailedRequest();
                return null;
            }
        }).when(userInfoDataSourceMock).requestVerificationCode(anyString(), any(UserInfoDataSource.RequestCodeCallback.class));

        viewModel.phone.set("01000000000");
        viewModel.onClickRequestCode(any(View.class));

        verify(navigatorMock).showRequestCodeFailed();
    }

    @Test
    public void validateCodeTest() {

        when(userInfoDataSourceMock.validateVerificationCode(anyString())).thenReturn(true);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserInfoDataSource.UpdateUserInfoCallback callback = (UserInfoDataSource.UpdateUserInfoCallback) invocation.getArguments()[1];
                callback.onUpdateComplete(new UserInfo());
                return null;
            }
        }).when(userInfoDataSourceMock).updateUserInfo(any(UserInfo.class), any(UserInfoDataSource.UpdateUserInfoCallback.class));

        viewModel.code.set("123456");
        viewModel.phone.set("01000000000");
        viewModel.onClickValidateCode(any(View.class));

        assertFalse(viewModel.isValidCode.get());
        assertEquals("", viewModel.code.get());
        verify(navigatorMock).showEditPhoneComplete();
    }

    @Test
    public void validateCodeFailedTest() {
        when(userInfoDataSourceMock.validateVerificationCode(anyString())).thenReturn(false);

        viewModel.code.set("wrongCode");
        viewModel.onClickValidateCode(any(View.class));

        verify(navigatorMock).showInvalidCode();
    }
}
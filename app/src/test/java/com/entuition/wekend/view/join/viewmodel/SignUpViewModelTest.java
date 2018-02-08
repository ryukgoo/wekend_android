package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.view.View;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by ryukgoo on 2018. 1. 29..
 */
public class SignUpViewModelTest {

    private Context contextMock;

    private SignUpViewNavigator navigatorMock;

    private SignUpViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        contextMock = mock(Context.class);
        navigatorMock = mock(SignUpViewNavigator.class);

        viewModel = spy(new SignUpViewModel(contextMock, navigatorMock));
    }

    @Test
    public void test() {
        viewModel.onCreate();
        viewModel.onClickSignUpButton(any(View.class));

        verify(navigatorMock).onShowAgreement();
    }

}
package com.entuition.wekend.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Created by ryukgoo on 2018. 1. 23..
 */
@RunWith(JUnit4.class)
public class TextUtilsTest {

    @Test
    public void isValidEmailExpression() throws Exception {
        String email = "";
        boolean isVaild = TextUtils.isValidEmailExpression(email);
        assertEquals(isVaild, false);

        email = "test@entuition.com";
        isVaild = TextUtils.isValidEmailExpression(email);
        assertEquals(isVaild, true);

        email = "email";
        isVaild = TextUtils.isValidEmailExpression(email);
        assertEquals(isVaild, false);
    }

    @Test
    public void isValidPasswordExpression() throws Exception {
        String password = "";
        boolean isVaild = TextUtils.isValidPasswordExpression(password);
        assertEquals(isVaild, false);

        password = "12341235413";
        isVaild = TextUtils.isValidPasswordExpression(password);
        assertEquals(isVaild, false);

        password = "asdfasfdasdf";
        isVaild = TextUtils.isValidPasswordExpression(password);
        assertEquals(isVaild, false);

        password = "as00";
        isVaild = TextUtils.isValidPasswordExpression(password);
        assertEquals(isVaild, false);

        password = "asdf00";
        isVaild = TextUtils.isValidPasswordExpression(password);
        assertEquals(isVaild, true);

        password = "asdf00@@##$!@#";
        isVaild = TextUtils.isValidPasswordExpression(password);
        assertEquals(isVaild, true);
    }

    @Test
    public void isValidNicknameExpression() throws Exception {
        String nickname = "";
        boolean isValid = TextUtils.isValidNicknameExpression(nickname);
        assertEquals(isValid, false);

        nickname = "AA";
        isValid = TextUtils.isValidNicknameExpression(nickname);
        assertEquals(isValid, true);

        nickname = "012345678901234";
        isValid = TextUtils.isValidNicknameExpression(nickname);
        assertEquals(isValid, true);

        nickname = "ㅎㅏㄴㄱㅡㄹㅇㅣ ㅇㅗㅐ ㅇㅣㄹㅓㄴㅣ";
        isValid = TextUtils.isValidNicknameExpression(nickname);
        assertEquals(isValid, false);

        nickname = "ㅇㅡㄷㅏ";
        isValid = TextUtils.isValidNicknameExpression(nickname);
        assertEquals(isValid, false);

        nickname = "한글이제대로된다";
        isValid = TextUtils.isValidNicknameExpression(nickname);
        assertEquals(isValid, true);

        nickname = " Wekend";
        isValid = TextUtils.isValidNicknameExpression(nickname.trim());
        assertEquals(isValid, true);

        nickname = "일이삼사오육칠팔구십일이삼사오";
        isValid = TextUtils.isValidNicknameExpression(nickname);
        assertEquals(isValid, true);
    }

    @Test
    public void isValidPhoneNumberExpression() throws Exception {
        String phone = "";
        boolean isValid = TextUtils.isValidPhoneNumberExpression(phone);
        assertEquals(isValid, false);

        phone = "00000000000";
        isValid = TextUtils.isValidPhoneNumberExpression(phone);
        assertEquals(isValid, true);

        phone = "aaaaaaaaaaa";
        isValid = TextUtils.isValidPhoneNumberExpression(phone);
        assertEquals(isValid, false);
    }

    @Test
    public void isNullorEmptyString() throws Exception {
        String text = null;
        boolean isValid = TextUtils.isNullorEmptyString(text);
        assertEquals(isValid, true);

        text = "";
        isValid = TextUtils.isNullorEmptyString(text);
        assertEquals(isValid, true);

        text = "  ";
        isValid = TextUtils.isNullorEmptyString(text);
        assertEquals(isValid, false);

        text = "a";
        isValid = TextUtils.isNullorEmptyString(text);
        assertEquals(isValid, false);
    }

}
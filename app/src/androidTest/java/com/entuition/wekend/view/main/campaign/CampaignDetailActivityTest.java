package com.entuition.wekend.view.main.campaign;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import com.entuition.wekend.util.Constants;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Created by ryukgoo on 2018. 1. 26..
 */

@RunWith(AndroidJUnit4.class)
public class CampaignDetailActivityTest {

    @Test
    public void shouldContainTheCorrectExtras() throws Exception {

        Context context = mock(Context.class);
        Intent intent = new Intent(context, CampaignDetailActivity.class);
        intent.putExtra(Constants.ExtraKeys.PRODUCT_ID, 123);
        assertNotNull(intent);

        Bundle extras = intent.getExtras();
        assertNotNull(extras);
        assertEquals(123, extras.getInt(Constants.ExtraKeys.PRODUCT_ID));
    }

}
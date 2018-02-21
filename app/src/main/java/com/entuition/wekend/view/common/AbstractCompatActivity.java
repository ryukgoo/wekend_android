package com.entuition.wekend.view.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ryukgoo on 2017. 8. 28..
 */

public class AbstractCompatActivity extends AppCompatActivity {

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.clear();
    }
}

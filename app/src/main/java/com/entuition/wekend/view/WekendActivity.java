package com.entuition.wekend.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ryukgoo on 2017. 8. 28..
 */

public class WekendActivity extends AppCompatActivity {

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.clear();
    }

    protected boolean reinitialize(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Intent restartIntent = new Intent(this, SplashScreen.class);
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(restartIntent);
            return true;
        } else {
            return false;
        }
    }
}

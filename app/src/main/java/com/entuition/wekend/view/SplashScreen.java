package com.entuition.wekend.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.authentication.AuthenticateTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.view.join.LoginActivity;
import com.entuition.wekend.view.main.ContainerActivity;
import com.entuition.wekend.view.main.activities.CampaignDetailActivity;

import bolts.AppLinks;

/**
 * Created by Kim on 2015-08-04.
 */
public class SplashScreen extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private int startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        startPosition = getIntent().getIntExtra(Constants.START_ACTIVITY_POSITION, 0);

        Log.d(TAG, "density : " + getResources().getDisplayMetrics().density);
        Log.d(TAG, "xdpi : " + getResources().getDisplayMetrics().xdpi);
        Log.d(TAG, "ydpi : " + getResources().getDisplayMetrics().ydpi);

        Log.d(TAG, "width : " + getWindowManager().getDefaultDisplay().getWidth());
        Log.d(TAG, "height : " + getWindowManager().getDefaultDisplay().getHeight());
        Log.d(TAG, "widthPixel : " + getResources().getDisplayMetrics().widthPixels);
        Log.d(TAG, "heightPixel : " + getResources().getDisplayMetrics().heightPixels);

        Log.d(TAG, "widthDP : " + getResources().getConfiguration().screenWidthDp);
        Log.d(TAG, "heightDP : " + getResources().getConfiguration().screenHeightDp);

        AuthenticateTask task = new AuthenticateTask(this);
        task.setCallback(new ISimpleTaskCallback() {

            @Override
            public void onPrepare() { }

            @Override
            public void onSuccess(@Nullable Object object) {
                boolean isRegistered = (boolean) object;
                Intent intent = null;
                if (isRegistered) {
                    String productId = getProductIdFromIntent();
                    if (productId == null) {
                        intent = new Intent(SplashScreen.this, ContainerActivity.class);
                        intent.putExtra(Constants.START_ACTIVITY_POSITION, startPosition);
                    } else {
                        intent = new Intent(SplashScreen.this, CampaignDetailActivity.class);
                        intent.putExtra(Constants.PARAMETER_PRODUCT_ID, Integer.parseInt(productId));
                    }
                } else {
                    intent = new Intent(SplashScreen.this, LoginActivity.class);
                }

                startActivity(intent);
            }

            @Override
            public void onFailed() {

            }
        });
        task.execute();
    }

    private String getProductIdFromIntent() {

        Uri uri = getIntent().getData();
        if (uri != null) {
            Log.d(TAG, "uri : " + uri.toString());
            if (uri.getQueryParameter("productId") != null) {
                return uri.getQueryParameter("productId");
            }
        }

        Uri facebookUri = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (facebookUri != null) {
            Log.d(TAG, "facebookUri : " + facebookUri);
            return facebookUri.getQueryParameter("productId");
        }

        return null;
    }
}

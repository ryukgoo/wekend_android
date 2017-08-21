package com.entuition.wekend.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.authentication.DeveloperAuthenticationProvider;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.view.join.LoginActivity;
import com.entuition.wekend.view.main.ContainerActivity;

/**
 * Created by Kim on 2015-08-04.
 */
public class SplashScreen extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private boolean isAutoLoginSuccess;

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

        new PreFetchData().execute();
    }

    private class PreFetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            CognitoSyncClientManager.init(SplashScreen.this);

            isAutoLoginSuccess = false;
            String username = DeveloperAuthenticationProvider.getDevAuthClientInstance().getUsernameFromDevice();
            Log.d(TAG, "!! username : " + username);
            if (username != null) {
                DeveloperAuthenticationProvider authenticationProvider =
                        (DeveloperAuthenticationProvider) CognitoSyncClientManager.getCredentialsProvider().getIdentityProvider();
                CognitoSyncClientManager.addLogins(authenticationProvider.getProviderName(), username);
                String token = authenticationProvider.refresh();

                isAutoLoginSuccess = (token != null);

                Log.d(TAG, "!! isAutoLoginSuccess : " + isAutoLoginSuccess);

                if (isAutoLoginSuccess) {
                    String userId = UserInfoDaoImpl.getInstance().getUserId(SplashScreen.this);
                    UserInfoDaoImpl.getInstance().loadUserInfo(userId);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d(TAG, "!! isAutoLoginSuccess : " + isAutoLoginSuccess);

            Intent intent = null;
            if (isAutoLoginSuccess) {
                intent = new Intent(SplashScreen.this, ContainerActivity.class);
                intent.putExtra(Constants.START_ACTIVITY_POSITION, startPosition);
            } else {
                intent = new Intent(SplashScreen.this, LoginActivity.class);
            }

            startActivity(intent);
        }
    }
}

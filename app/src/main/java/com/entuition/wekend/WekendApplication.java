package com.entuition.wekend;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.entuition.wekend.data.CognitoSyncClientManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Kim on 2015-08-25.
 */
public class WekendApplication extends MultiDexApplication {

    private static final String TAG = "WekendApplication";

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate!!!!!");

        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            Log.d(TAG, "LeakCanary > isInAnalyzerProcess return");
            return;
        }

        LeakCanary.install(this);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(1200, 1200) // ?? -> default : (480. 800) device screen dimensions
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO);
//                .writeDebugLogs();

        ImageLoader.getInstance().init(config.build());
        CognitoSyncClientManager.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "Application onTerminated!!!");
        super.onTerminate();
    }

}

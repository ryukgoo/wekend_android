package com.entuition.wekend.view.common;

import android.content.Context;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public abstract class AbstractViewModel {

    private final Context context;

    public AbstractViewModel(Context context) {
        this.context = context.getApplicationContext();
    }

    protected Context getApplication() {
        return context;
    }

    abstract public void onCreate();

    abstract public void onResume();

    abstract public void onPause();

    abstract public void onDestroy();
}

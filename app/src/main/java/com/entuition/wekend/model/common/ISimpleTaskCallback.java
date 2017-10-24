package com.entuition.wekend.model.common;

import android.support.annotation.Nullable;

/**
 * Created by ryukgoo on 2016. 9. 7..
 */
public interface ISimpleTaskCallback {
    void onPrepare();
    void onSuccess(@Nullable Object object);
    void onFailed();
}

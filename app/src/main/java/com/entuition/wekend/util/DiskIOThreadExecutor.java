package com.entuition.wekend.util;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ryukgoo on 2017. 11. 27..
 */

public class DiskIOThreadExecutor implements Executor {

    private final Executor diskIO;

    public DiskIOThreadExecutor() {
        this.diskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        diskIO.execute(command);
    }
}

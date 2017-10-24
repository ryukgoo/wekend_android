package com.entuition.wekend.model.data.like.asynctask;

import android.os.AsyncTask;

import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.observable.ReadLikeObservable;

/**
 * Created by ryukgoo on 2017. 7. 20..
 */

public class UpdateLikeReadStateTask extends AsyncTask<LikeDBItem, Void, Void> {

    private LikeDBItem item;

    @Override
    protected Void doInBackground(LikeDBItem... params) {

        item = params[0];
        LikeDBDaoImpl.getInstance().updateLikeReadState(item);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ReadLikeObservable.getInstance().read(item.getProductId());
    }
}

package com.entuition.wekend.model.data.user.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.NoticeInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 8. 3..
 */

public class LoadNoticeInfoTask extends AsyncTask<String, Void, Void> {

    private final Context context;
    private ISimpleTaskCallback callback;
    private List<NoticeInfo> result;

    public LoadNoticeInfoTask(Context context) {
        this.context = context;
    }

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... params) {
        String type = params[0];
        result = UserInfoDaoImpl.getInstance(context).getNoticeInfoList(type);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (result != null) {
            callback.onSuccess(result);
        } else {
            callback.onFailed();
        }
    }
}

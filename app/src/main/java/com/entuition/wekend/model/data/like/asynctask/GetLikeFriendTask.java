package com.entuition.wekend.model.data.like.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.LikeReadState;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 4. 11..
 */
public class GetLikeFriendTask extends AsyncTask<Integer, Void, Void> {

    private static final String TAG = "GetLikeFriendTask";

    private Context context;
    private List<LikeDBItem> friendList;
    private IGetLikeFriendCallback callback;

    public GetLikeFriendTask(Context context) {
        this.context = context;
    }

    public void setCallback(IGetLikeFriendCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Integer... params) {

        int productId = params[0];
        String userId = UserInfoDaoImpl.getInstance().getUserId(context);
        String gender = UserInfoDaoImpl.getInstance().getUserInfo(userId).getGender();

        friendList = new ArrayList<LikeDBItem>();

        List<LikeDBItem> friends = LikeDBDaoImpl.getInstance().getLikedFriendList(productId, gender);
        PaginatedList<LikeReadState> readStates = LikeDBDaoImpl.getInstance().getLikeReadState(productId, userId);

        for (LikeDBItem item : friends) {
            for (LikeReadState state : readStates) {
                if (item.getUserId().equals(state.getLikeUserId())) {
                    item.setRead(true);
                    continue;
                }
            }
            friendList.add(item);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (friendList != null) {
            callback.onSuccess(friendList);
        } else {
            callback.onFailed();
        }
    }
}

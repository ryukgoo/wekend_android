package com.entuition.wekend.model.googleservice.googlemap;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.Utilities;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ryukgoo on 2016. 9. 1..
 */
public class GetLocationTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private LatLng location;
    private IGetLocationCallback callback;

    public GetLocationTask(Context context, IGetLocationCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... params) {

        String address = params[0];
        location = Utilities.getLocationFromAddress(context, address);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if (location != null) {
            callback.onSuccess(location);
        } else {
            callback.onFailed();
        }
    }
}

package com.entuition.wekend.model.googleservice.googlemap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ryukgoo on 2016. 9. 1..
 */
public interface IGetLocationCallback {
    void onSuccess(LatLng place);
    void onFailed();
}

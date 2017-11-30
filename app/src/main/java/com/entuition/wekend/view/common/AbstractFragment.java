package com.entuition.wekend.view.common;

import android.app.Fragment;

/**
 * Created by ryukgoo on 2016. 6. 30..
 */
public abstract class AbstractFragment extends Fragment {

    abstract public void onClickTitle();
    abstract public boolean onBackPressed();
    abstract public void reselect();
}

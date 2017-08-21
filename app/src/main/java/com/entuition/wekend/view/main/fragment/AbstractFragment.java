package com.entuition.wekend.view.main.fragment;

import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.entuition.wekend.R;

/**
 * Created by ryukgoo on 2016. 6. 30..
 */
public abstract class AbstractFragment extends Fragment {

    public AbstractFragment() {}

    abstract public void onClickTitle();
    abstract public boolean onBackPressed();
    abstract public void reselect();
    abstract public void refresh();
}

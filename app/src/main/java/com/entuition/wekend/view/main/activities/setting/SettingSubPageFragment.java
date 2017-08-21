package com.entuition.wekend.view.main.activities.setting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.entuition.wekend.R;

/**
 * Created by ryukgoo on 2016. 10. 20..
 */

public class SettingSubPageFragment extends Fragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private TextView titleText;
    private TextView description;

    private String title;
    private String message;

    public SettingSubPageFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting_subpage, container, false);

        titleText = (TextView) view.findViewById(R.id.id_setting_subpage_title);
        description = (TextView) view.findViewById(R.id.id_setting_subpage_description);

        titleText.setText(title);
        description.setText(message);

        // back button
        view.findViewById(R.id.id_setting_subpage_prev).setOnClickListener(this);

        return view;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.id_setting_subpage_prev :
                getActivity().onBackPressed();
                break;
        }

    }
}

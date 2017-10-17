package com.entuition.wekend.view.main.activities.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.view.common.WekendAbstractActivity;

/**
 * Created by ryukgoo on 2016. 8. 17..
 */
public class SettingSubListActivity extends WekendAbstractActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (reinitialize(savedInstanceState)) return;
        setContentView(R.layout.activity_setting_subpage);

        initView();
    }

    private void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.id_setting_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView title = (TextView) findViewById(R.id.id_toolbar_title);
        title.setText(getString(R.string.label_setting_help));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

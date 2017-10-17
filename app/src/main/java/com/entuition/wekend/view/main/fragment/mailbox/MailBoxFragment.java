package com.entuition.wekend.view.main.fragment.mailbox;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.entuition.wekend.R;
import com.entuition.wekend.view.common.UnscrollViewPager;
import com.entuition.wekend.view.main.ChangeTitleObservable;
import com.entuition.wekend.view.main.DropdownVisibleObservable;
import com.entuition.wekend.view.main.fragment.AbstractFragment;

/**
 * Created by ryukgoo on 2016. 6. 30..
 */
public class MailBoxFragment extends AbstractFragment implements TabLayout.OnTabSelectedListener {

    private final String TAG = getClass().getSimpleName();

    private AppSectionPagerAdapter appSectionPagerAdapter;
    private UnscrollViewPager viewPager;
    private TabLayout tabLayout;

    private ReceiveMailBoxFragment receiveMailBoxFragment;
    private SendMailBoxFragment sendMailBoxFragment;

    private String[] mailBoxTitles;

    public MailBoxFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mailbox_main, container, false);

        ChangeTitleObservable.getInstance().change(getString(R.string.label_mailbox));
        DropdownVisibleObservable.getInstance().change(false);

        mailBoxTitles = getResources().getStringArray(R.array.mailbox_titles);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            appSectionPagerAdapter = new AppSectionPagerAdapter(getChildFragmentManager());
        }

        viewPager = (UnscrollViewPager) rootView.findViewById(R.id.id_viewpager_mailbox);
        viewPager.setAdapter(appSectionPagerAdapter);
        appSectionPagerAdapter.registerDataSetObserver(new DataSetObserver() {

            @Override
            public void onChanged() {
                super.onChanged();

                Log.d(TAG, "onChange");

                receiveMailBoxFragment.refresh();
                sendMailBoxFragment.refresh();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        });

        tabLayout = (TabLayout) rootView.findViewById(R.id.id_tab_mailbox);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            ChangeTitleObservable.getInstance().change(getString(R.string.label_mailbox));
            DropdownVisibleObservable.getInstance().change(false);

            refresh();
        }
    }

    @Override
    public void onClickTitle() {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void reselect() {

    }

    @Override
    public void refresh() {
//        if (receiveMailBoxFragment != null) receiveMailBoxFragment.refresh();
//        if (sendMailBoxFragment != null) sendMailBoxFragment.refresh();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private class AppSectionPagerAdapter extends FragmentPagerAdapter {

        public AppSectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Log.d(TAG, "AppSectionPagerAdapter > getItem > Position : " + position);

            switch (position) {
                case 0 :
                    if (receiveMailBoxFragment == null) {
                        receiveMailBoxFragment = new ReceiveMailBoxFragment();
                    } else {
                        receiveMailBoxFragment.refresh();
                    }

                    return receiveMailBoxFragment;
                case 1 :
                    if (sendMailBoxFragment == null) {
                        sendMailBoxFragment = new SendMailBoxFragment();
                    } else {
                        sendMailBoxFragment.refresh();
                    }

                    return sendMailBoxFragment;

                default:
                    return receiveMailBoxFragment;
            }
        }

        @Override
        public int getCount() {
            return mailBoxTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mailBoxTitles[position];
        }
    }
}

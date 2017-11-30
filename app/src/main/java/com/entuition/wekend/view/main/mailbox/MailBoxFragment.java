package com.entuition.wekend.view.main.mailbox;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.mail.MailType;
import com.entuition.wekend.databinding.MailboxFragmentBinding;
import com.entuition.wekend.view.common.AbstractFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 6. 30..
 */
public class MailBoxFragment extends AbstractFragment implements TabLayout.OnTabSelectedListener {

    public static final String TAG = MailBoxFragment.class.getSimpleName();

    public static MailBoxFragment newInstance() {
        return new MailBoxFragment();
    }

    private MailboxFragmentBinding binding;

    private List<MailBoxListFragment> fragmentList;

    public MailBoxFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        binding = MailboxFragmentBinding.inflate(inflater, container, false);

        initFragments();
        setupSectionPager();
        setupTabLayout();

        return binding.getRoot();
    }

    @Override
    public void onClickTitle() {}

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void reselect() {
        getCurrentFragment().scrollTop();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        binding.sectionPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        getCurrentFragment().scrollTop();
    }

    private void initFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(MailBoxListFragment.newInstance(MailType.receive.toString()));
        fragmentList.add(MailBoxListFragment.newInstance(MailType.send.toString()));
    }

    private void setupSectionPager() {
        AppSectionPagerAdapter adapter = new AppSectionPagerAdapter(getFragmentManager(), getActivity(), fragmentList);
        binding.sectionPager.setAdapter(adapter);
    }

    private void setupTabLayout() {
        binding.tabLayout.setupWithViewPager(binding.sectionPager);
        binding.tabLayout.addOnTabSelectedListener(this);
    }

    private MailBoxListFragment getCurrentFragment() {
        return fragmentList.get(binding.sectionPager.getCurrentItem());
    }

    private static class AppSectionPagerAdapter extends FragmentPagerAdapter {

        private final List<MailBoxListFragment> fragmentList;
        private final String[] mailBoxTitles;

        AppSectionPagerAdapter(FragmentManager fm, Context context, List<MailBoxListFragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
            mailBoxTitles = context.getResources().getStringArray(R.array.mailbox_titles);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mailBoxTitles[position];
        }
    }
}

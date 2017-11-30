package com.entuition.wekend.view.main.mailbox;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.entuition.wekend.R;
import com.entuition.wekend.data.source.mail.IMail;
import com.entuition.wekend.data.source.mail.MailDataSource;
import com.entuition.wekend.data.source.mail.MailType;
import com.entuition.wekend.data.source.mail.ReceiveMailRepository;
import com.entuition.wekend.data.source.mail.SendMailRepository;
import com.entuition.wekend.databinding.MailboxListFragmentBinding;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.common.DividerItemDecoration;
import com.entuition.wekend.view.main.mailbox.adapter.MailBoxListAdapter;
import com.entuition.wekend.view.main.mailbox.viewmodel.MailBoxListViewModel;
import com.entuition.wekend.view.main.mailbox.viewmodel.MailBoxNavigator;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public class MailBoxListFragment extends Fragment implements MailBoxNavigator {

    public static final String TAG = MailBoxListFragment.class.getSimpleName();

    private static final String ARG_TYPE = "type_key";

    public static MailBoxListFragment newInstance(String type) {
        MailBoxListFragment fragment = new MailBoxListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    private MailType mailType;

    private MailboxListFragmentBinding binding;
    private MailBoxListViewModel model;

    private MailBoxListAdapter adapter;
    private LinearLayoutManager layoutManager;

    public MailBoxListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mailType = MailType.valueOf(getArguments().getString(ARG_TYPE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        MailDataSource mailDataSource = null;
        switch (mailType) {
            case receive:
                mailDataSource = ReceiveMailRepository.getInstance(getActivity());
                break;
            case send:
                mailDataSource = SendMailRepository.getInstance(getActivity());
                break;
        }

        model = new MailBoxListViewModel(getActivity(), this, mailType, mailDataSource);
        binding = MailboxListFragmentBinding.inflate(inflater, container, false);
        setupRecyclerView();
        binding.setModel(model);

        model.onCreate();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new MailBoxListAdapter(model, new ArrayList<IMail>(0));
        adapter.setMode(Attributes.Mode.Single);
        binding.recycler.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        model.onResume();
    }

    @Override
    public void gotoMailProfileView(IMail mail) {
        Intent intent = new Intent(getActivity(), MailProfileActivity.class);
        intent.putExtra(Constants.ExtraKeys.USER_ID, mail.getUserId());
        intent.putExtra(Constants.ExtraKeys.FRIEND_ID, mail.getFriendId());
        intent.putExtra(Constants.ExtraKeys.PRODUCT_ID, mail.getProductId());
        intent.putExtra(Constants.ExtraKeys.MAIL_TYPE, mail.getMailType().toString());
        getActivity().startActivity(intent);
    }

    @Override
    public void onCompleteDeleteMail(int position, SwipeLayout layout) {
        adapter.deleteItem(position, layout);
    }

    @Override
    public void onFailedDeleteMail() {

    }

    public void scrollTop() {
        layoutManager.scrollToPosition(0);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        binding.recycler.setItemAnimator(new FadeInLeftAnimator());
    }
}

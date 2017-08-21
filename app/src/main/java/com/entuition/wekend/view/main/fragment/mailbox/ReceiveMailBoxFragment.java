package com.entuition.wekend.view.main.fragment.mailbox;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.mail.ReceiveMail;
import com.entuition.wekend.model.data.mail.ReceiveMailDaoImpl;
import com.entuition.wekend.model.data.mail.UpdateReceiveMailObservable;
import com.entuition.wekend.model.data.mail.asynctask.DeleteReceiveMailTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.mail.asynctask.LoadReceiveMailTask;
import com.entuition.wekend.model.googleservice.gcm.MailNotificationObservable;
import com.entuition.wekend.view.main.activities.ReceiveProposeProfileActivity;
import com.entuition.wekend.view.main.fragment.SimpleViewHolder;
import com.entuition.wekend.view.util.DividerItemDecoration;

import java.util.Observable;
import java.util.Observer;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by ryukgoo on 2016. 5. 6..
 */
public class ReceiveMailBoxFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private FrameLayout progressbar;
    private RecyclerView mailListView;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout refreshLayout;

    private ReceiveMailListAdapter listAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MailNotificationObservable.getInstance().addObserver(new UpdateNotificationObserver());
        UpdateReceiveMailObservable.getInstance().addObserver(new UpdateNotificationObserver());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mailbox, container, false);

        initViews(rootView);
        loadReceiveMail();

        return rootView;
    }

    private void initViews(View rootView) {

        mailListView = (RecyclerView) rootView.findViewById(R.id.id_list_receive_mailbox);
        progressbar = (FrameLayout) rootView.findViewById(R.id.id_screen_dim_progress);

        layoutManager = new LinearLayoutManager(getActivity());
        mailListView.setLayoutManager(layoutManager);

        mailListView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        mailListView.setItemAnimator(new FadeInLeftAnimator());

        listAdapter = new ReceiveMailListAdapter(getActivity());
        listAdapter.setItemClickListener(new ItemClickListener());
        listAdapter.setMode(Attributes.Mode.Single);
        mailListView.setAdapter(listAdapter);

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.id_mail_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadReceiveMail();
            }
        });
    }

    private void loadReceiveMail() {
        LoadReceiveMailTask task = new LoadReceiveMailTask(getActivity());
        task.setCallback(new LoadReceiveMailCallback());
        task.execute();
    }

    private Intent getProfileIntent(ReceiveMail mail) {

        Intent intent = new Intent(getActivity(), ReceiveProposeProfileActivity.class);
        intent.putExtra(Constants.PARAMETER_PRODUCT_ID, mail.getProductId());
        intent.putExtra(Constants.PARAMETER_PROPOSEE_ID, mail.getUserId());
        intent.putExtra(Constants.PARAMETER_PROPOSER_ID, mail.getSenderId());
        intent.putExtra(Constants.PARAMETER_FRIEND_ID, mail.getSenderId());
        intent.putExtra(Constants.PARAMETER_PROPOSE_UPDATEDTIME, mail.getUpdatedTime());

        return intent;
    }

    public void refresh() {
        if (listAdapter != null) listAdapter.notifyDataSetChanged();
    }

    private class LoadReceiveMailCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() {
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(@Nullable Object result) {
            listAdapter.notifyDataSetChanged();
            progressbar.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailed() {
            progressbar.setVisibility(View.GONE);
        }
    }

    private class ItemClickListener implements SimpleViewHolder.IViewHolderListener {

        @Override
        public void onClickItem(int position) {
            final ReceiveMail mail = ReceiveMailDaoImpl.getInstance().getReceiveMailList().get(position);
            getActivity().startActivity(getProfileIntent(mail));
        }

        @Override
        public void onClickDeleteButton(final int position, final SwipeLayout layout) {

            final ReceiveMail mail = ReceiveMailDaoImpl.getInstance().getReceiveMailList().get(position);

            DeleteReceiveMailTask task = new DeleteReceiveMailTask();
            task.setCallback(new ISimpleTaskCallback() {
                @Override
                public void onPrepare() {

                }

                @Override
                public void onSuccess(@Nullable Object object) {
                    listAdapter.mItemManger.removeShownLayouts(layout);
                    listAdapter.notifyItemRemoved(position);

                    listAdapter.closeAllOpenedLayout();
                }

                @Override
                public void onFailed() {

                }
            });
            task.execute(mail);
        }

        @Override
        public boolean onTouchItem(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (listAdapter.isOpenLayout()) {
                    listAdapter.closeAllOpenedLayout();
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
    }

    private class UpdateNotificationObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            loadReceiveMail();
        }
    }
}

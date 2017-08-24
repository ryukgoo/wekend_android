package com.entuition.wekend.view.main.fragment.mailbox;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.mail.ReadSendMailObservable;
import com.entuition.wekend.model.data.mail.SendMail;
import com.entuition.wekend.model.data.mail.SendMailDaoImpl;
import com.entuition.wekend.model.data.mail.UpdateSendMailObservable;
import com.entuition.wekend.model.data.mail.asynctask.DeleteSendMailTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.mail.asynctask.LoadSendMailTask;
import com.entuition.wekend.model.googleservice.gcm.MailNotificationObservable;
import com.entuition.wekend.view.main.activities.SendProposeProfileActivity;
import com.entuition.wekend.view.main.fragment.SimpleViewHolder;
import com.entuition.wekend.view.util.DividerItemDecoration;

import java.util.Observable;
import java.util.Observer;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by ryukgoo on 2016. 5. 6..
 */
public class SendMailBoxFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private FrameLayout progressbar;
    private RecyclerView mailListView;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout refreshLayout;
    private TextView textViewNoResult;

    private SendMailListAdapter listAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MailNotificationObservable.getInstance().addObserver(new UpdateNotificationObserver());
        UpdateSendMailObservable.getInstance().addObserver(new UpdateNotificationObserver());
        ReadSendMailObservable.getInstance().addObserver(new ReadObserver());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mailbox, container, false);

        initViews(rootView);
        loadSendMail();

        return rootView;
    }

    private void initViews(View rootView) {

        mailListView = (RecyclerView) rootView.findViewById(R.id.id_list_receive_mailbox);
        progressbar = (FrameLayout) rootView.findViewById(R.id.id_screen_dim_progress);
        textViewNoResult = (TextView) rootView.findViewById(R.id.id_textview_mailbox_no_result);
        textViewNoResult.setText(R.string.mailbox_send_no_result);

        layoutManager = new LinearLayoutManager(getActivity());
        mailListView.setLayoutManager(layoutManager);

        mailListView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        mailListView.setItemAnimator(new FadeInLeftAnimator());

        listAdapter = new SendMailListAdapter(getActivity());
        listAdapter.setItemClickListener(new ItemClickListener());
        listAdapter.setMode(Attributes.Mode.Single);

        mailListView.setAdapter(listAdapter);

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.id_mail_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSendMail();
            }
        });
    }

    private void loadSendMail() {
        LoadSendMailTask task = new LoadSendMailTask(getActivity());
        task.setCallback(new LoadSendMailCallback());
        task.execute();
    }

    private Intent getProfileIntent(SendMail mail) {

        Intent intent = new Intent(getActivity(), SendProposeProfileActivity.class);
        intent.putExtra(Constants.PARAMETER_PRODUCT_ID, mail.getProductId());
        intent.putExtra(Constants.PARAMETER_PROPOSEE_ID, mail.getReceiverId());
        intent.putExtra(Constants.PARAMETER_PROPOSER_ID, mail.getUserId());
        intent.putExtra(Constants.PARAMETER_FRIEND_ID, mail.getReceiverId());
        intent.putExtra(Constants.PARAMETER_PROPOSE_UPDATEDTIME, mail.getUpdatedTime());

        return intent;
    }

    public void refresh() {
        if (listAdapter != null) listAdapter.notifyDataSetChanged();
    }

    private class LoadSendMailCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() {
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(@Nullable Object object) {
            listAdapter.notifyDataSetChanged();
            progressbar.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);

            if (listAdapter.getItemCount() == 0) {
                textViewNoResult.setVisibility(View.VISIBLE);
            } else {
                textViewNoResult.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFailed() {
            progressbar.setVisibility(View.GONE);
        }
    }

    private class ItemClickListener implements SimpleViewHolder.IViewHolderListener {

        @Override
        public void onClickItem(final int position) {
            final SendMail mail = SendMailDaoImpl.getInstance().getSendMailList().get(position);
            if (mail.getIsRead() == Constants.MAIL_STATUS_UNREAD) { new ReadTask().execute(position); }

            getActivity().startActivity(getProfileIntent(mail));
        }

        @Override
        public void onClickDeleteButton(final int position, final SwipeLayout layout) {

            SendMail mail = SendMailDaoImpl.getInstance().getSendMailList().get(position);

            DeleteSendMailTask task = new DeleteSendMailTask();
            task.setCallback(new ISimpleTaskCallback() {
                @Override
                public void onPrepare() {

                }

                @Override
                public void onSuccess(@Nullable Object object) {
                    listAdapter.mItemManger.removeShownLayouts(layout);
                    listAdapter.notifyItemRemoved(position);
                    listAdapter.closeAllOpenedLayout();

                    if (listAdapter.getItemCount() == 0) {
                        textViewNoResult.setVisibility(View.VISIBLE);
                    } else {
                        textViewNoResult.setVisibility(View.GONE);
                    }
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
            loadSendMail();
        }
    }

    private class ReadObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            int position = (int) data;
            listAdapter.notifyItemChanged(position);
        }
    }

    private class ReadTask extends AsyncTask<Integer, Void, Void> {

        int position;

        @Override
        protected Void doInBackground(Integer... params) {

            position = params[0];

            SendMail mail = SendMailDaoImpl.getInstance().getSendMailList().get(position);
            mail.setIsRead(Constants.MAIL_STATUS_READ);
            SendMailDaoImpl.getInstance().updateSendMail(mail);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ReadSendMailObservable.getInstance().read(position);
        }
    }

}


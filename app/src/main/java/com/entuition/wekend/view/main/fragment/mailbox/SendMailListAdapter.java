package com.entuition.wekend.view.main.fragment.mailbox;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.mail.SendMail;
import com.entuition.wekend.model.data.mail.SendMailDaoImpl;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.common.AnimateFirstDisplayListener;
import com.entuition.wekend.view.common.MaskBitmapDisplayer;
import com.entuition.wekend.view.main.fragment.SimpleViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 9. 6..
 */
public class SendMailListAdapter extends RecyclerSwipeAdapter<SimpleViewHolder> {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private DisplayImageOptions options;

    private List<SwipeLayout> openLayouts;

    private SimpleViewHolder.IViewHolderListener listener;

    public SendMailListAdapter(Context context) {
        this.context = context;

        this.openLayouts = new ArrayList<SwipeLayout>();

        options = new DisplayImageOptions.Builder()
                .displayer(new MaskBitmapDisplayer(context.getApplicationContext(), MaskBitmapDisplayer.MASK_SMALL))
                .showImageOnLoading(R.drawable.img_bg_thumb_s_logo)
                .showImageForEmptyUri(R.drawable.img_bg_thumb_s_logo)
                .showImageOnFail(R.drawable.img_bg_thumb_s_logo)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .build();
    }

    public void setItemClickListener(SimpleViewHolder.IViewHolderListener listener) {
        this.listener = listener;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);
        SimpleViewHolder viewHolder = new SimpleViewHolder(view, listener);
        viewHolder.swipeLayout.close();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        SendMail mail = SendMailDaoImpl.getInstance().getSendMailList().get(position);

        viewHolder.swipeLayout.close();
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                openLayouts.add(layout);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                openLayouts.remove(layout);
            }
        });

        viewHolder.title.setText(getMailTitle(mail));
        viewHolder.title.setTextColor(getTitleTextColor(mail));
        viewHolder.subTitle.setText("DATE : " + Utilities.convertDateStringFromTimestamp(mail.getUpdatedTime()));

        if (mail.getIsRead() == Constants.MAIL_STATUS_UNREAD) {
            viewHolder.newIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.newIcon.setVisibility(View.GONE);
        }

        String photoFileName = UserInfoDaoImpl.getUploadedPhotoFileName(mail.getReceiverId(), 0);
        String photoUrl = S3Utils.getS3Url(Constants.PROFILE_THUMB_BUCKET_NAME, photoFileName);
        ImageLoader.getInstance().displayImage(photoUrl, viewHolder.imageView, options, new AnimateFirstDisplayListener());

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return SendMailDaoImpl.getInstance().getSendMailList().size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.id_recyclerview_swipelayout;
    }


    public void closeAllOpenedLayout() {
        for (SwipeLayout layout : openLayouts) {
            layout.close();
        }

        openLayouts.clear();
        openLayouts = new ArrayList<SwipeLayout>();
    }

    public boolean isOpenLayout() {
        return (openLayouts != null && openLayouts.size() > 0);
    }

    private String getMailTitle(SendMail mail) {
        String subTitle = mail.getReceiverNickname();
        switch (mail.getStatus()) {
            case Constants.PROPOSE_STATUS_MADE :
                subTitle += context.getString(R.string.mailbox_message_propose_made);
                break;
            case Constants.PROPOSE_STATUS_NOT_MADE :
                subTitle += context.getString(R.string.mailbox_message_propose_not_made);
                break;
            case Constants.PROPOSE_STATUS_REJECT :
                subTitle += context.getString(R.string.mailbox_message_propose_reject);
                break;
            default:
                subTitle += context.getString(R.string.mailbox_message_propose_made);
                break;
        }
        return subTitle;
    }

    private int getTitleTextColor(SendMail mail) {
        if (mail.getStatus().equals(Constants.PROPOSE_STATUS_NOT_MADE)) {
            return Color.parseColor("#28b1ca");
        } else {
            return Color.parseColor("#43434a");
        }
    }
}

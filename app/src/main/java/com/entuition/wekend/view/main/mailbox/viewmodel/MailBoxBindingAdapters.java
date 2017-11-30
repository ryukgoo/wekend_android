package com.entuition.wekend.view.main.mailbox.viewmodel;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.mail.IMail;
import com.entuition.wekend.data.source.mail.ProposeStatus;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.util.ImageOptions;
import com.entuition.wekend.util.ImageUtils;
import com.entuition.wekend.util.Utilities;
import com.entuition.wekend.view.common.AnimateFirstDisplayListener;
import com.entuition.wekend.view.main.mailbox.adapter.MailBoxListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public class MailBoxBindingAdapters {

    public static final String TAG = MailBoxBindingAdapters.class.getSimpleName();

    @BindingAdapter("refreshMails")
    public static void refreshMailDatas(RecyclerView view, List<IMail> items) {
        MailBoxListAdapter adapter = (MailBoxListAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.replaceData(new ArrayList<IMail>(items));
        }
    }

    @BindingAdapter("mailListImage")
    public static void loadMailBoxImage(ImageView view, String userId) {
        String photoFileName = ImageUtils.getUploadedPhotoFileName(userId, 0);
        String photoUrl = ImageUtils.getHttpUrl(ImageUtils.PROFILE_THUMB_BUCKET_NAME, photoFileName);
        ImageLoader.getInstance().displayImage(photoUrl, view, ImageOptions.FRIEND_THUMB_CIRCLE_LIST, new AnimateFirstDisplayListener());
    }

    @BindingAdapter("setMailDate")
    public static void setMailItemDate(TextView view, String timestamp) {
        view.setText(view.getContext().getString(R.string.mail_item_date, Utilities.convertDateStringFromTimestamp(timestamp)));
    }

    @BindingAdapter("setMailTitle")
    public static void setMailItemTitle(TextView view, IMail mail) {
        ProposeStatus status = ProposeStatus.valueOf(mail.getStatus());
        String message = view.getContext().getString(status.getMessageId(mail.getMailType()), mail.getFriendNickname());
        view.setText(message);

        if (status == ProposeStatus.notMade) {
            view.setTextColor(Color.parseColor(mail.getHighlightColor()));
        } else {
            view.setTextColor(Color.parseColor("#43434a"));
        }
    }

    @BindingAdapter("mailProfileDesc")
    public static void setMailProfileProductDesc(TextView view, ProductInfo info) {

        Log.d(TAG, "setMailProfileProductDesc");

        if (info != null) {
            Log.d(TAG, "setMailProfileProductDesc > info : " + info.getTitleKor());
            String description = info.getTitleKor() + Utilities.HTML_NEW_LINE + Utilities.HTML_NEW_LINE
                    + info.getDescription();
            view.setText(Html.fromHtml(description));
        }
    }

    @BindingAdapter("textMatchResult")
    public static void setMatchResult(TextView view, ProposeStatus status) {
        view.setText(status.getStatusId());
    }
}

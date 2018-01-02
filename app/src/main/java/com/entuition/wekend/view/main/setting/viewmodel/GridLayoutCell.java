package com.entuition.wekend.view.main.setting.viewmodel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.entuition.wekend.databinding.GridLayoutCellBinding;

/**
 * Created by ryukgoo on 2017. 12. 21..
 */

public class GridLayoutCell extends LinearLayout {

    public static final String TAG = GridLayoutCell.class.getSimpleName();

    public GridLayoutCell(Context context) {
        this(context, null, 0);
    }

    public GridLayoutCell(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridLayoutCell(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        GridLayoutCellBinding binding = GridLayoutCellBinding.inflate(inflater);
        addView(binding.getRoot());

        binding.executePendingBindings();
    }
}

package com.entuition.wekend.view.common.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ryukgoo on 2017. 11. 7..
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = BaseViewHolder.class.getSimpleName();

    private final ViewDataBinding binding;

    public BaseViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Object obj) {
//        binding.setVariable(BR.obj, obj);
        binding.executePendingBindings();
    }
}

package com.entuition.wekend.view.main.campaign.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.entuition.wekend.data.source.product.enums.IProductEnum;
import com.entuition.wekend.databinding.SpinnerItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 10..
 */

public class FilterSpinnerAdapter extends ArrayAdapter<IProductEnum> {

    public static final String TAG = FilterSpinnerAdapter.class.getSimpleName();

    private List<IProductEnum> options;

    public FilterSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<IProductEnum> objects) {
        super(context, resource);

        this.options = objects;
    }

    public void setList(List<IProductEnum> items) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.clear();

        options.addAll(items);
    }

    @Override
    public int getCount() {
        return options == null ? 0 : options.size();
    }

    @Nullable
    @Override
    public IProductEnum getItem(int position) {
        return options.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getSpinnerItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getSpinnerItemView(position, convertView, parent);
    }

    private View getSpinnerItemView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SpinnerItemBinding binding;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = SpinnerItemBinding.inflate(inflater, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        String title = parent.getContext().getString(getItem(position).getResource());

        binding.setTitle(title);
        ((TextView) binding.getRoot()).setGravity(Gravity.CENTER);

        return binding.getRoot();
    }
}

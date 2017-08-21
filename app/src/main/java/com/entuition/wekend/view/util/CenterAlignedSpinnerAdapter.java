package com.entuition.wekend.view.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 23..
 */
public class CenterAlignedSpinnerAdapter extends ArrayAdapter<String> {

    private Object[] tags;

    public CenterAlignedSpinnerAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public CenterAlignedSpinnerAdapter(Context context, int resource, String[] objects, Object[] tags) {
        super(context, resource, objects);
        this.tags = tags;
    }

    public CenterAlignedSpinnerAdapter(Context context, int resource, List<String> objects, Object[] tags) {
        super(context, resource, objects);
        this.tags = tags;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ((TextView) view).setGravity(Gravity.CENTER);
        view.setTag(tags[position]);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        ((TextView) view).setGravity(Gravity.CENTER);

        return view;
    }
}

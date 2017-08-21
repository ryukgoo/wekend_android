package com.entuition.wekend.view.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.googleservice.billing.GoogleBillingController;
import com.entuition.wekend.view.main.ChangeTitleObservable;
import com.entuition.wekend.view.main.ContainerActivity;
import com.entuition.wekend.view.main.DropdownVisibleObservable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 7. 1..
 */
public class StoreFragment extends AbstractFragment implements AdapterView.OnItemClickListener, GoogleBillingController.OnQueryInventory {

    private final String TAG = getClass().getSimpleName();

    private TextView textPoint;
    private GridView gridView;
    private List<String> arrPrice;
    private List<String> arrBonus;
    private StoreAdapter adapter;
    private FrameLayout progressbarLayout;

    public StoreFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_store, container, false);
        initView(rootView);

        GoogleBillingController.getInstance(getActivity()).init();
        GoogleBillingController.getInstance(getActivity()).setListener(this);

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
//            titleContainer.setVisibility(View.VISIBLE);
//            title.setText(getString(R.string.label_store));
//            dropdown.setVisibility(View.GONE);
            ChangeTitleObservable.getInstance().change(getString(R.string.label_store));
            DropdownVisibleObservable.getInstance().change(false);

            GoogleBillingController.getInstance(getActivity()).init();
            GoogleBillingController.getInstance(getActivity()).setListener(this);
        } else {
            GoogleBillingController.getInstance(getActivity()).dismiss();
        }
    }

    @Override
    public void onClickTitle() {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void reselect() {

    }

    @Override
    public void refresh() {
        String userName = UserInfoDaoImpl.getInstance().getUserId(getActivity());
        UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(userName);
        textPoint.setText("보유포인트 " + userInfo.getBalloon() + "P");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getActivity(), "Item " + position + " Clicked!!", Toast.LENGTH_SHORT).show();

        progressbarLayout.setVisibility(View.VISIBLE);
        GoogleBillingController.getInstance(getActivity()).launchPurchase(getActivity(), position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult > requestCode : " + requestCode + ", resultCode : " + resultCode);

        if (!GoogleBillingController.getInstance(getActivity()).handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil");
        }
    }

    private void initView(View rootView) {
        ChangeTitleObservable.getInstance().change(getString(R.string.label_store));
        DropdownVisibleObservable.getInstance().change(false);

        arrPrice = Arrays.asList(getResources().getStringArray(R.array.store_price));
        arrBonus = Arrays.asList(getResources().getStringArray(R.array.store_bonus));
        adapter = new StoreAdapter();

        textPoint = (TextView) rootView.findViewById(R.id.id_textview_store_point);
        String userName = UserInfoDaoImpl.getInstance().getUserId(getActivity());
        UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(userName);
        textPoint.setText("보유포인트 " + userInfo.getBalloon() + "P");

        gridView = (GridView) rootView.findViewById(R.id.id_gridview_store);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(adapter);

        progressbarLayout = (FrameLayout) rootView.findViewById(R.id.id_screen_dim_progress);
    }

    @Override
    public void onQueryFinished() {
        progressbarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPurchaseFinished(String sku) {
        progressbarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onConsumePurchase() {
        progressbarLayout.setVisibility(View.GONE);

        String userName = UserInfoDaoImpl.getInstance().getUserId(getActivity());
        UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(userName);
        textPoint.setText("보유포인트 " + userInfo.getBalloon() + "P");

        ((ContainerActivity) getActivity()).refreshNavigationView();
    }

    @Override
    public void onQueryFailed() {
        progressbarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPurchaseFailed() {
        progressbarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onConsumeFailed() {
        // TODO : handle Failed
    }

    public class StoreAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public StoreAdapter() {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrPrice.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final StoreViewHolder holder;

            if (convertView == null) {
                holder = new StoreViewHolder();
                convertView = layoutInflater.inflate(R.layout.gridlist_item_store, null);
                holder.txtPrice = (TextView) convertView.findViewById(R.id.id_griditem_store_price);
                holder.txtBonus = (TextView) convertView.findViewById(R.id.id_griditem_store_bonus);

                convertView.setTag(holder);
            } else {
                holder = (StoreViewHolder) convertView.getTag();
            }

            holder.txtPrice.setText(arrPrice.get(position));
            holder.txtBonus.setText(arrBonus.get(position));

            return convertView;
        }
    }

    class StoreViewHolder {
        TextView txtPrice;
        TextView txtBonus;
    }

}

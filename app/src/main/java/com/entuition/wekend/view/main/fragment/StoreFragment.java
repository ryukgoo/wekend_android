package com.entuition.wekend.view.main.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.googleservice.billing.GoogleBillingController;
import com.entuition.wekend.view.main.ChangeTitleObservable;
import com.entuition.wekend.view.main.ContainerActivity;
import com.entuition.wekend.view.main.DropdownVisibleObservable;

/**
 * Created by ryukgoo on 2016. 7. 1..
 */
public class StoreFragment extends AbstractFragment implements StoreAdapter.ItemClickListener, GoogleBillingController.OnQueryInventory {

    private final String TAG = getClass().getSimpleName();

    private TextView textPoint;
    private RecyclerView recyclerView;
    private StoreAdapter adapter;
    private FrameLayout progressbarLayout;

    public StoreFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_store, container, false);

        GoogleBillingController.getInstance(getActivity()).init();
        GoogleBillingController.getInstance(getActivity()).setListener(this);

        initView(rootView);

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
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
        UserInfo userInfo = UserInfoDaoImpl.getInstance(getActivity()).getUserInfo();
        textPoint.setText("보유포인트 " + userInfo.getBalloon() + "P");
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

        textPoint = (TextView) rootView.findViewById(R.id.id_textview_store_point);
        UserInfo userInfo = UserInfoDaoImpl.getInstance(getActivity()).getUserInfo();
        textPoint.setText("보유포인트 " + userInfo.getBalloon() + "P");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.id_gridview_store);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));

        adapter = new StoreAdapter(getActivity());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        progressbarLayout = (FrameLayout) rootView.findViewById(R.id.id_screen_dim_progress);
    }

    @Override
    public void onQueryFinished() {
        Log.d(TAG, "onQueryFinished");
        progressbarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPurchaseFinished(String sku) {
        Log.d(TAG, "onPurchasedFinished > sku : " + sku);
        progressbarLayout.setVisibility(View.GONE);

        UserInfo userInfo = UserInfoDaoImpl.getInstance(getActivity()).getUserInfo();
        textPoint.setText("보유포인트 " + userInfo.getBalloon() + "P");

        ((ContainerActivity) getActivity()).refreshNavigationView();
    }

    @Override
    public void onConsumePurchase() {
        Log.d(TAG, "onComsumePurchase");
        progressbarLayout.setVisibility(View.GONE);

        UserInfo userInfo = UserInfoDaoImpl.getInstance(getActivity()).getUserInfo();
        textPoint.setText("보유포인트 " + userInfo.getBalloon() + "P");

        ((ContainerActivity) getActivity()).refreshNavigationView();
    }

    @Override
    public void onQueryFailed() {
        Log.d(TAG, "onQueryFailed");
        progressbarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPurchaseFailed() {
        Log.d(TAG, "onPurchaseFailed");
        progressbarLayout.setVisibility(View.GONE);

        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.CustomAlertDialog))
                .setTitle(getString(R.string.store_purchased_failed_title))
                .setMessage(getString(R.string.store_purchased_failed_message))
                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onConsumeFailed() {
        // TODO : handle Failed
        Log.d(TAG, "onConsumeFailed");

        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.CustomAlertDialog))
                .setTitle(getString(R.string.store_purchased_failed_title))
                .setMessage(getString(R.string.store_purchased_failed_message))
                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onItemClicked(View view, int position) {
//        Toast.makeText(getActivity(), "Item " + position + " Clicked!!", Toast.LENGTH_SHORT).show();

        progressbarLayout.setVisibility(View.VISIBLE);
        GoogleBillingController.getInstance(getActivity()).launchPurchase(getActivity(), position);

    }
}

package com.entuition.wekend.view.main.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.entuition.wekend.R;
import com.entuition.wekend.data.google.billing.GoogleBillingController;
import com.entuition.wekend.data.google.billing.SkuDetails;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.StoreFragmentBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.view.common.AbstractFragment;
import com.entuition.wekend.view.main.store.adapter.StoreAdapter;
import com.entuition.wekend.view.main.store.viewmodel.StoreNavigator;
import com.entuition.wekend.view.main.store.viewmodel.StoreViewModel;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2016. 7. 1..
 */
public class StoreFragment extends AbstractFragment implements StoreNavigator {

    public static final String TAG = StoreFragment.class.getSimpleName();

    public static StoreFragment newInstance() {
        return new StoreFragment();
    }

    private StoreFragmentBinding binding;
    private StoreViewModel model;
    private StoreAdapter adapter;

    public StoreFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        model = new StoreViewModel(getActivity(), this,
                UserInfoRepository.getInstance(getActivity()),
                GoogleBillingController.getInstance(getActivity()));

        binding = StoreFragmentBinding.inflate(inflater, container, false);
        binding.setModel(model);

        setupRecyclerView();

        model.onCreate();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new StoreAdapter(model, new ArrayList<SkuDetails>(0));
        binding.storeRecycler.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!model.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        model.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClickTitle() {}

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void reselect() {}

    private void setupRecyclerView() {
        binding.storeRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }

    @Override
    public void purchaseItem(int position) {
        model.launchPurchase(getActivity(), position);
    }

    @Override
    public void onPurchaseFinished() {

    }

    @Override
    public void onConsumePurchase() {

    }

    @Override
    public void onQueryInventoryFailed() {

    }

    @Override
    public void onPurchaseFailed() {
        AlertUtils.showAlertDialog(getActivity(), R.string.purchase_failed,
                R.string.purchase_failed_message);
    }

    /*
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
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "handleActivityResult > requestCode : " + requestCode + ", resultCode : " + resultCode);

        if (!GoogleBillingController.getInstance(getActivity()).handleActivityResult(requestCode, resultCode, data)) {
            super.handleActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "handleActivityResult handled by IABUtil");
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

//        ((ContainerActivity) getActivity()).refreshNavigationView();
    }

    @Override
    public void onConsumePurchase() {
        Log.d(TAG, "onComsumePurchase");
        progressbarLayout.setVisibility(View.GONE);

        UserInfo userInfo = UserInfoDaoImpl.getInstance(getActivity()).getUserInfo();
        textPoint.setText("보유포인트 " + userInfo.getBalloon() + "P");

//        ((ContainerActivity) getActivity()).refreshNavigationView();
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
    */
}

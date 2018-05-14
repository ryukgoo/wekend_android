package com.entuition.wekend.view.main.store;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
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
        Log.d(TAG, "onPurchaseFinished");
        AlertUtils.showAlertDialog(getActivity(), R.string.purchase_complete, R.string.purchase_complete_message);
    }

    @Override
    public void onConsumePurchase() {
        Log.d(TAG, "onConsumePurchase");
    }

    @Override
    public void onQueryInventoryFailed() {
        Log.d(TAG, "onQueryInventoryFailed");
    }

    @Override
    public void onPurchaseFailed() {
        AlertUtils.showAlertDialog(getActivity(), R.string.purchase_failed,
                R.string.purchase_failed_message);
    }

    @Override
    public void onAlreayHasSubscription() {
        AlertUtils.showAlertDialog(getActivity(), R.string.purchase_has_subs_title, R.string.purchase_has_subs_message);
    }
}

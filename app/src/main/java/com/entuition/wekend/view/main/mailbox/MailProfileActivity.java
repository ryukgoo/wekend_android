package com.entuition.wekend.view.main.mailbox;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.mail.MailDataSource;
import com.entuition.wekend.data.source.mail.MailType;
import com.entuition.wekend.data.source.mail.ReceiveMailRepository;
import com.entuition.wekend.data.source.mail.SendMailRepository;
import com.entuition.wekend.data.source.product.ProductInfoRepository;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.MailProfileActivityBinding;
import com.entuition.wekend.databinding.UserInputDialogBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.main.mailbox.viewmodel.MailProfileNavigator;
import com.entuition.wekend.view.main.mailbox.viewmodel.MailProfileViewModel;
import com.entuition.wekend.view.main.mailbox.viewmodel.UserInputDialogListener;
import com.entuition.wekend.view.main.mailbox.viewmodel.UserInputDialogViewModel;
import com.entuition.wekend.view.main.setting.adapter.ProfileViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2016. 4. 18..
 */
public class MailProfileActivity extends AppCompatActivity implements MailProfileNavigator, UserInputDialogListener {

    public static final String TAG = MailProfileActivity.class.getSimpleName();

    private MailProfileActivityBinding binding;
    private MailProfileViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MailDataSource mailDataSource = null;

        MailType mailType = MailType.valueOf(getIntent().getStringExtra(Constants.ExtraKeys.MAIL_TYPE));
        switch (mailType) {
            case receive:
                mailDataSource = ReceiveMailRepository.getInstance(this);
                break;
            case send:
                mailDataSource = SendMailRepository.getInstance(this);
                break;
        }

        model = new MailProfileViewModel(this, this, UserInfoRepository.getInstance(this),
                ProductInfoRepository.getInstance(this), mailDataSource);
        binding = DataBindingUtil.setContentView(this, R.layout.mail_profile_activity);
        binding.setModel(model);

        setupToolbar();
        setupViewPager();

        model.setIntent(getIntent());
        model.onCreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void confirmPropose(DialogInterface.OnClickListener listener) {
        AlertUtils.showAlertDialog(this,
                R.string.consume_point,
                R.string.consume_point_message,
                true,
                listener);
    }

    @Override
    public void onProposeComplete(String nickname) {
        String title = getString(R.string.propose_success);
        String message = getString(R.string.propose_success_message, nickname);
        AlertUtils.showAlertDialog(this, title, message);
    }

    @Override
    public void onAcceptComplete(String nickname) {
        String title = getString(R.string.propose_accepted);
        String message = getString(R.string.propose_accepte_message, nickname);
        AlertUtils.showAlertDialog(this, title, message);
    }

    @Override
    public void onRejectComplete(String nickname) {
        String title = getString(R.string.propose_rejected);
        String message = getString(R.string.propose_reject_message, nickname);
        AlertUtils.showAlertDialog(this, title, message);
    }

    @Override
    public void showUserInputDialog() {
        setupUserInputDialog();
    }

    @Override
    public void showNotEnoughPoint() {
        AlertUtils.showAlertDialog(this, R.string.not_enough_point,
                R.string.not_enough_point_message);
    }

    @Override
    public void showTryAgain() {
        AlertUtils.showAlertDialog(this, R.string.unknown_error,
                R.string.unknown_error_retry);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupViewPager() {
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(model, new ArrayList<String>(0));
        binding.profileViewPager.setAdapter(adapter);
    }

    @SuppressLint("RestrictedApi")
    private void setupUserInputDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        UserInputDialogBinding dialogBinding = UserInputDialogBinding.inflate(inflater, null, false);
        UserInputDialogViewModel dialogViewModel = new UserInputDialogViewModel(this);
        dialogBinding.setModel(dialogViewModel);

        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog))
                .setTitle(getString(R.string.propose_message_title))
                .setPositiveButton(getString(R.string.send), dialogViewModel)
                .setNegativeButton(getString(R.string.cancel), dialogViewModel)
                .setView(dialogBinding.getRoot())
                .show();
    }

    @Override
    public void onCompleteInputMessage(String message) {
        model.propose(message);
    }
}
package com.entuition.wekend.view.main.setting.viewmodel;

import android.app.Activity;
import android.content.Intent;

import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;

/**
 * Created by ryukgoo on 2017. 12. 26..
 */

public class MultiSelectImageViewModel extends SelectImageViewModel {

    public static final String TAG = MultiSelectImageViewModel.class.getSimpleName();

    private int cellIndex = -1;

    public MultiSelectImageViewModel(SelectImageNavigator navigator,
                                     UserInfoDataSource userInfoDataSource) {
        super(navigator, userInfoDataSource);
    }

    @Override
    public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_GALLERY :
                case ACTION_REQUEST_CAMERA :
                    super.onActivityResult(context, requestCode, resultCode, data);
                    break;
                case ACTION_REQUEST_CROP :
                    super.uploadImage(cellIndex);
                    break;
                default:
                    break;
            }
        }
    }

    public void onClickSelectImage(int index) {
        cellIndex = index;
        super.onClickSelectImage();
    }

    @Override
    public void deleteImage(int index) {
        cellIndex = index;
        super.deleteImage(index);
    }

    public int getCellIndex() {
        return cellIndex;
    }

}

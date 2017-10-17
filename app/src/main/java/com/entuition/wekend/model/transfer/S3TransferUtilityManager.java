package com.entuition.wekend.model.transfer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 1. 13..
 */
public class S3TransferUtilityManager {

    private static S3TransferUtilityManager instance;

    public static S3TransferUtilityManager getInstance(Context context) {
        if (instance == null) {
            synchronized (S3TransferUtilityManager.class) {
                if (instance == null) {
                    instance = new S3TransferUtilityManager(context);
                }
            }
        }

        return instance;
    }

    private static final String TAG = "S3TransferUtilityManager";

    private TransferUtility transferUtility;
    private List<TransferObserver> observers;
    private HashMap<Integer, S3TransferMap> transferRecordsMaps;
    private TransferListener uploadListener;
    private TransferListener downloadListener;

    private S3TransferUtilityManager(Context context) {
        Log.d(TAG, "S3TransferUtilityMAnager initialize!");
        transferUtility = CognitoSyncClientManager.getTranferUtility(context);
        initData();
    }

    private void initData() {

        Log.d(TAG, "initData");

        transferRecordsMaps = new HashMap<Integer, S3TransferMap>();
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
        for (TransferObserver observer : observers) {
            S3TransferMap map = new S3TransferMap();
            S3Utils.fillMap(map, observer, false);
            transferRecordsMaps.put(observer.getId(), map);

            if (!TransferState.COMPLETED.equals(observer.getState())
                && !TransferState.FAILED.equals(observer.getState())
                && !TransferState.CANCELED.equals(observer.getState())) {
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {

                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    }

                    @Override
                    public void onError(int id, Exception ex) {

                    }
                });
            }
        }
    }

    public void setUploadListener(TransferListener listener) {
        this.uploadListener = listener;
    }

    public void SetDownloadListener(TransferListener listener) {
        this.downloadListener = listener;
    }

    public void beginDownload(Context context, String key) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);

        TransferObserver observer = transferUtility.download(Constants.PROFILE_IMAGE_BUCKET_NAME, key, file);

        observers.add(observer);
        S3TransferMap map = new S3TransferMap(key);
        S3Utils.fillMap(map, observer, false);
        transferRecordsMaps.put(observer.getId(), map);
        observer.setTransferListener(downloadListener);
    }

    public void beginUpload(Context context, String filePath, String newFilename) {
        if (filePath == null) {
            Toast.makeText(context, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            File file = new File(filePath);
            TransferObserver observer = transferUtility.upload(Constants.PROFILE_IMAGE_BUCKET_NAME, newFilename, file);
            observers.add(observer);
            S3TransferMap map = new S3TransferMap(filePath);
            S3Utils.fillMap(map, observer, false);
            transferRecordsMaps.put(observer.getId(), map);
            observer.setTransferListener(uploadListener);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public String getFilePath(int id) {
        S3TransferMap map = transferRecordsMaps.get(id);
        return map.getUrl();
    }
}

package com.entuition.wekend.model.transfer;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.entuition.wekend.model.Constants;

/**
 * Created by ryukgoo on 2016. 3. 3..
 */
public class S3Utils {

    /**
     * Converts number of bytes into proper scale.
     *
     * @param bytes number of bytes to be converted.
     * @return A string that represents the bytes in a proper scale.
     */
    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[] {
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0;; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    /*
     * Fills in the map with information in the observer so that it can be used
     * with a SimpleAdapter to populate the UI
     */
    public static void fillMap(S3TransferMap map, TransferObserver observer, boolean isChecked) {
        int progress = (int) ((double) observer.getBytesTransferred() * 100 / observer
                .getBytesTotal());
        map.setId(observer.getId());
        map.setIsChecked(isChecked);
        map.setFileName(observer.getAbsoluteFilePath());
        map.setProgress(progress);
        map.setBytes(getBytesString(observer.getBytesTransferred()) + "/"
                + getBytesString(observer.getBytesTotal()));
        map.setState(observer.getState());
        map.setPercentage(progress + "%");
    }

    public static String getS3Url(String bucket, String key) {
        return "https://" + bucket + "." + Constants.AMAZON_S3_ADDRESS + "/" + key;
    }

}

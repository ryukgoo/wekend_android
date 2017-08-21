package com.entuition.wekend.model.transfer;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

/**
 * Created by ryukgoo on 2016. 3. 3..
 */
public class S3TransferMap {
    private int id;
    private boolean isChecked;
    private String fileName;
    private int progress;
    private String bytes;
    private TransferState state;
    private String percentage;
    private String url;

    public S3TransferMap() {
    }

    public S3TransferMap(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public TransferState getState() {
        return state;
    }

    public void setState(TransferState state) {
        this.state = state;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

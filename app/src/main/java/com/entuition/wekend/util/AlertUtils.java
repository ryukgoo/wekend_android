package com.entuition.wekend.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.entuition.wekend.R;

/**
 * Created by ryukgoo on 2017. 10. 26..
 */

public class AlertUtils {

    @SuppressLint("RestrictedApi")
    public static void showAlertDialog(Context context, int titleId, int messageId) {
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                .setTitle(context.getString(titleId))
                .setMessage(context.getString(messageId))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @SuppressLint("RestrictedApi")
    public static void showAlertDialog(Context context, String title, String message) {
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @SuppressLint("RestrictedApi")
    public static void showAlertDialog(Context context, int titleId, int messageId, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                .setTitle(context.getString(titleId))
                .setMessage(context.getString(messageId))
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    @SuppressLint("RestrictedApi")
    public static void showAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    @SuppressLint("RestrictedApi")
    public static void showAlertDialog(Context context, int titleId, int messageId, boolean needsNegative, DialogInterface.OnClickListener listener) {
        if (needsNegative) {
            new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                    .setTitle(context.getString(titleId))
                    .setMessage(context.getString(messageId))
                    .setPositiveButton(R.string.ok, listener)
                    .setNegativeButton(R.string.cancel, listener)
                    .show();
        } else {
            showAlertDialog(context, titleId, messageId, listener);
        }
    }

    @SuppressLint("RestrictedApi")
    public static void showAlertDialog(Context context, String title, String message, boolean needsNegative, DialogInterface.OnClickListener listener) {
        if (needsNegative) {
            new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok, listener)
                    .setNegativeButton(R.string.cancel, listener)
                    .show();
        } else {
            showAlertDialog(context, title, message, listener);
        }
    }
}

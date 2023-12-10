package com.japco.tablerototal.util;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.japco.tablerototal.R;

public class Dialogs {

    public static void showInfoDialog(Context context, String text, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton(R.string.accept_btn, okListener)
                .create();
        dialog.show();
    }

    public static void showInfoDialog(Context context, int text, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton(R.string.accept_btn, okListener)
                .create();
        dialog.show();
    }

    public static void showInfoDialog(Context context, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton(R.string.accept_btn, (DialogInterface d, int id) -> {
                    d.dismiss();
                })
                .create();
        dialog.show();
    }

    public static void showInfoDialog(Context context, int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton(R.string.accept_btn, (DialogInterface d, int id) -> {
                    d.dismiss();
                })
                .create();
        dialog.show();
    }

    public static void showConfirmDialog(Context context, String text, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton(R.string.confirm_btn, okListener)
                .setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> {
                   dialogInterface.dismiss();
                })
                .create();
        dialog.show();
    }

    public static void showConfirmDialog(Context context, int text, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton(R.string.confirm_btn, okListener)
                .setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .create();
        dialog.show();
    }

}

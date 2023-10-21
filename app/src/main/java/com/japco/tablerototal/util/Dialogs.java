package com.japco.tablerototal.util;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Dialogs {

    public static void showInfoDialog(Context context, String text, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton("Ok", okListener)
                .create();
        dialog.show();
    }

    public static void showInfoDialog(Context context, int text, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton("Ok", okListener)
                .create();
        dialog.show();
    }

    public static void showInfoDialog(Context context, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton("Ok", (DialogInterface d, int id) -> {
                    d.dismiss();
                })
                .create();
        dialog.show();
    }

    public static void showInfoDialog(Context context, int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setMessage(text)
                .setPositiveButton("Ok", (DialogInterface d, int id) -> {
                    d.dismiss();
                })
                .create();
        dialog.show();
    }

}

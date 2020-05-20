package com.medulance.driver.Utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by sahil on 23/9/16.
 */
public class DialogUtil {

    public static void showPopUp(Context context, String title, String msg, String positiveButtonText, DialogInterface.OnClickListener posDialogInteface, String negativeButtonText, DialogInterface.OnClickListener negDialogInteface) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (title != null) {
            alertDialogBuilder.setTitle(title);
        }
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton(positiveButtonText, posDialogInteface);
        alertDialogBuilder.setNegativeButton(negativeButtonText, negDialogInteface);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

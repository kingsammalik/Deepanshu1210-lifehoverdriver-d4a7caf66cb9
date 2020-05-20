package com.medulance.driver.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by sahil on 26/8/16.
 */
public class ToastUtils {

    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

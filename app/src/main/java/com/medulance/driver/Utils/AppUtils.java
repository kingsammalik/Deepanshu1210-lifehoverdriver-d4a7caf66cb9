package com.medulance.driver.Utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by sahil on 24/9/16.
 */
public class AppUtils {

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) { /**Function to check if chatservice running or not.**/
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

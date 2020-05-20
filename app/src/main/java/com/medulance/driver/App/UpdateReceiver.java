package com.medulance.driver.App;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.medulance.driver.Utils.AppUtils;
import com.medulance.driver.helper.SessionManager;

/**
 * Created by Sahil on 12/07/2016.
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri packageName = intent.getData();
        if (packageName.toString().equals("package:" + context.getPackageName())) {
            Log.e("updated", "updated");
            Log.e("package_name........", context.getPackageName());
            SessionManager sessionManager = new SessionManager(context);
            if (sessionManager.getKeyIsLoggedIn()) {
                if (!AppUtils.isServiceRunning(context, SendService.class)) {
                    context.startService(new Intent(context, SendService.class));
                }
            }
        }
    }
}

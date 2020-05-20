package com.medulance.driver.App;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.medulance.driver.helper.SessionManager;

public class ServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Service Stops", "Ohhhhhhh");

        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.getKeyIsLoggedIn()) {
            context.startService(new Intent(context, SendService.class));
        }
    }
}


package com.medulance.driver.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.medulance.driver.App.Constants;
import com.medulance.driver.Interfaces.GetGCMTokenInterface;

import java.io.IOException;

/**
 * Created by sahil on 11/9/16.
 */
public class GetGCMToken extends AsyncTask<Void, Void, String> {

    private static final String TAG = GetGCMToken.class.getSimpleName();
    private Context context;
    private GetGCMTokenInterface getGCMTokenInterface;
    private static final String[] TOPICS = {"all", Constants.Extras.APP_TOPIC, Constants.Extras.DRIVER};

    public GetGCMToken(Context context, GetGCMTokenInterface getGCMTokenInterface) {
        this.context = context;
        this.getGCMTokenInterface = getGCMTokenInterface;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            /*InstanceID instanceID = InstanceID.getInstance(context);
            String token = instanceID.getToken(context.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            subscribeTopics(token);*/
            return null;
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (getGCMTokenInterface != null) {
            getGCMTokenInterface.getResult(s);
        }
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(context);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}

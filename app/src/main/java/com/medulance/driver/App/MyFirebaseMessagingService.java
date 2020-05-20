package com.medulance.driver.App;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.medulance.driver.Activity.DialogActivity;
import com.medulance.driver.Activity.DriverTrackingActivity;
import com.medulance.driver.Activity.SplashActivity;
import com.medulance.driver.R;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.models.NewBookingModel;

import java.util.Map;

/**
 * Created by LifeHover on 12/19/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    SessionManager sessionManager;
    private static final String TAG = "MyAndroidFCMService";
    public static String RECEIVER_NOTIFY = "receiver_notify";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sessionManager = new SessionManager(this);
        //Log data to Log Cat
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Map map1 = remoteMessage.getData();
        Log.e(TAG, "Data: " + remoteMessage.getData());
        Bundle data = new Bundle();
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            data.putString(entry.getKey(), entry.getValue());
        }
        String key = data.getString("type") ;
        if (key != null) {
            if (key.equalsIgnoreCase("startRide")) {
                NewBookingModel newBookingModel = new NewBookingModel();
                newBookingModel.setBookingId(data.getString("bookingId"));
                sessionManager.setKeyBookingId(data.getString("bookingId"));
                newBookingModel.setCurrentlat(data.getString("currentlat"));
                newBookingModel.setCurrentlng(data.getString("currentlng"));
                newBookingModel.setDeslat(data.getString("deslat"));
                newBookingModel.setDeslng(data.getString("deslng"));
                newBookingModel.setCurrent(data.getString("current"));
                newBookingModel.setDes(data.getString("des"));
                newBookingModel.setName(data.getString("name"));
                newBookingModel.setMobile(data.getString("mobile"));
                newBookingModel.setBase_price(data.getString("base_price"));
                newBookingModel.setBase_km(data.getString("base_km"));
                newBookingModel.setPrice_per_km(data.getString("price_per_km"));
                newBookingModel.setBooking_type(data.getString("booking_type"));
                String json = new Gson().toJson(newBookingModel, NewBookingModel.class);
                sessionManager.setKeyBookingJson(json);
                Bundle bundle = new Bundle();
                Intent intent = new Intent(this, DriverTrackingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bundle.putInt(Constants.IntentParameters.TYPE, Constants.Extras.FROM_DIALOG);
                intent.putExtras(bundle);
                startActivity(intent);

            }else if (key.equalsIgnoreCase("driver")) {
                NewBookingModel newBookingModel = new NewBookingModel();
                newBookingModel.setBookingId(data.getString("bookingId"));
                sessionManager.setKeyBookingId(data.getString("bookingId"));
                newBookingModel.setCurrentlat(data.getString("currentlat"));
                newBookingModel.setCurrentlng(data.getString("currentlng"));
                newBookingModel.setDeslat(data.getString("deslat"));
                newBookingModel.setDeslng(data.getString("deslng"));
                newBookingModel.setCurrent(data.getString("current"));
                newBookingModel.setDes(data.getString("des"));
                newBookingModel.setFloor(data.getString("floor"));
                newBookingModel.setLift(data.getString("lift"));
                newBookingModel.setName(data.getString("name"));
                newBookingModel.setMobile(data.getString("mobile"));
                newBookingModel.setBase_price(data.getString("base_price"));
                newBookingModel.setBase_km(data.getString("base_km"));
                newBookingModel.setPrice_per_km(data.getString("price_per_km"));
                newBookingModel.setBooking_type(data.getString("booking_type"));
                String json = new Gson().toJson(newBookingModel, NewBookingModel.class);
                sessionManager.setKeyBookingJson(json);
                showSmallNotification("New Booking Request", "New Booking Request", 1);
                Intent intent = new Intent(this, DialogActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (key.equalsIgnoreCase("cancelBooking")) {

                showSmallNotification("Booking Request", "Your ride has been cancelled by the user.", 2);
                Intent intent = new Intent();
                intent.setAction(Constants.Extras.LOCATION_BROADCAST);
                intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.DRIVER_RIDE_CANCELLED);
                sendBroadcast(intent);
            } else if (key.equalsIgnoreCase("logOutByManager")){
                showSmallNotification("Logout", "Logout By Manager",4);
                ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cManager.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.Extras.LOCATION_BROADCAST);
                    intent.putExtra(Constants.IntentParameters.TYPE,Constants.Extras.LOGOUT_BY_MANAGER);
                    sendBroadcast(intent);

                } else {
                    ToastUtils.showShortToast(this, getString(R.string.no_connection));
                }
            }
            else if(key.equalsIgnoreCase("payingByCash")){
                showSmallNotification("Paying By Cash", "Collect Cash",5);
                String amount = data.getString("amount");
                Intent intent = new Intent();
                intent.setAction(Constants.Extras.LOCATION_BROADCAST);
                intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.PAYING_BY_CASH);
                intent.putExtra("amount",amount);
                //GcmBroadcastReceiver.completeWakefulIntent(intent);
                sendBroadcast(intent);
            }
            else if(key.equalsIgnoreCase("payingByPaytm")){
                showSmallNotification("Paying By Paytm", "Wait",3);
                Intent intent = new Intent();
                intent.setAction(Constants.Extras.LOCATION_BROADCAST);
                intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.PAYING_WITH_PAYTM);
                sendBroadcast(intent);
            }
            else if(key.equalsIgnoreCase("paidWithPaytm")){
                showSmallNotification("Paid by Paytm", "Recieve Payment",6);
                Intent intent = new Intent();
                intent.setAction(Constants.Extras.LOCATION_BROADCAST);
                intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.PAID_WITH_PAYTM);
                sendBroadcast(intent);
            } /*else if(key.equalsIgnoreCase("failedTransaction")){
                showSmallNotification("Payment Failed", "Said to pay",8);
                Intent intent = new Intent();
                intent.setAction(Constants.Extras.LOCATION_BROADCAST);
                intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.PAYMENT_FAILED);
                sendBroadcast(intent);
            }*/
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG, "Refreshed token: " + s);
    }

    private void showSmallNotification(final String title, final String message, int NOTIFYID) {
        Intent intent;
        if (NOTIFYID == 1) {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (NOTIFYID == 2) {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (NOTIFYID == 3) {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        final int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);

        Uri alarmSound;
        if (NOTIFYID == 1) {
            alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getPackageName() + "/raw/sound");
        } else {
            alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getPackageName() + "/raw/notification");
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);


        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(inboxStyle)
                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFYID, notification);
    }


}

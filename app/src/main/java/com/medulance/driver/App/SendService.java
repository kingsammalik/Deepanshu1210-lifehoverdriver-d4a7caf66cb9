package com.medulance.driver.App;

/**
 * Created by Sahil on 03/07/2016.
 */

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendService extends Service implements LocationListener {
    LocationManager locationManager;
    NotificationCompat.Builder mBuilder;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000 * 10, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000 * 10, 0, this);
        createNotification();
        startForeground(1,mBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        return START_STICKY;
    }

    private void createNotification(){
         mBuilder = new NotificationCompat.Builder(this,
                "Meduride" )
                .setOngoing(true)
                .setContentTitle( "Test" )
                .setContentText( "Hello! This is my first push notification" );
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                    .setUsage(AudioAttributes. USAGE_ALARM )
                    .build() ;
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new
                    NotificationChannel( "Meduride" , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color. RED ) ;
            mBuilder.setChannelId( "Meduride") ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        sendIntent(location.getLatitude(), location.getLongitude());
        //sendData(location.getLatitude(), location.getLongitude());
        sendRealData(location.getLatitude(), location.getLongitude());
    }

    private void sendRealData(double latitude, double longitude) {
        Log.e("Service","sending location "+latitude);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        //myRef.child("ambulances").child("location").child("5").child("lat");
        //myRef.setValue(lat);

        myRef = database.getReference("/ambulances/location/5");

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("lat", latitude);
        childUpdates.put("lng", longitude);

        myRef.updateChildren(childUpdates);
    }

    private void sendIntent(double latitude, double longitude) {
        MyApplication.getInstance().setLatLng(latitude, longitude);
        Intent intent = new Intent();
        intent.setAction(Constants.Extras.LOCATION_BROADCAST);
        intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.LOCATION_BROADCAST_TYPE);
        intent.putExtra(Constants.IntentParameters.LATITUDE, latitude);
        intent.putExtra(Constants.IntentParameters.LONGITUDE, longitude);
        sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void sendData(double lat, double lng) {
        SessionManager sessionManager = new SessionManager(this);
        OKHttpAPICalls okHttpAPICalls = new OKHttpAPICalls();
        Bundle bundle = new Bundle();
        bundle.putDouble(Constants.Extras.LATITUDE, lat);
        bundle.putDouble(Constants.Extras.LONGITUDE, lng);
        bundle.putString(Constants.Extras.DRIVER, sessionManager.getKeyUserId());
        if (sessionManager.getKeyUserId() == null) {
            return;
        }
        okHttpAPICalls.run(Constants.RequestTags.SEND_LOCATION, bundle);
        okHttpAPICalls.setOnOkHttpNotifyListener(new IOkHttpNotify() {
            @Override
            public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
                String jsonResponse = response.body().string();
            }

            @Override
            public void onHttpRequestFailure(String requestType, Request request, String errorMessage) {

            }
        });

    }

    @Override
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        super.onDestroy();
    }
}

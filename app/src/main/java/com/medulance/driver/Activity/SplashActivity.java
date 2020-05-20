package com.medulance.driver.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.medulance.driver.App.Constants;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.App.SendService;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.AppUtils;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.models.NewBookingModel;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity implements IOkHttpNotify {


    private List<String> permissionArray = new ArrayList<>();
    private ProgressBar progressBar;
    private String TAG = SplashActivity.class.getSimpleName();
    private SessionManager sessionManager;
    private OKHttpAPICalls okHttpAPICalls;
    private String gcmToken = "";
    Bundle mBundle;
    private FirebaseAnalytics mFirebaseAnalytics;
    String versionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_splash);
        //Auto updat

        try {
            versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            Log.e("",versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //FirebaseApp.initializeApp(this);
        //
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MyApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open")
                .setLabel("settings")
                .build());
        mBundle=new Bundle();
        initializeView();
        initiateOkHttp();

        sessionManager = MyApplication.getInstance().getSession();
        if (isGooglePlayServicesAvailable(this)) {
            checkallPermissions();
        }
        //gcmToken= FirebaseInstanceId.getInstance().getToken();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        gcmToken = task.getResult().getToken();
                        Log.w(TAG, "gcm token"+ gcmToken);

                    }
                });



    }

    private void initializeView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void initiateOkHttp() {
        this.okHttpAPICalls = new OKHttpAPICalls();
        this.okHttpAPICalls.setOnOkHttpNotifyListener(this);
    }

    public void updateStatus(){
        if(sessionManager.getKeyUserId()!=null) {
            if (gcmToken != null) {
                Log.e("Fcm Token",gcmToken);
                mBundle.putString(Constants.Extras.VERSION_NAME, versionName);
                mBundle.putString(Constants.Extras.DEVICE_ID, gcmToken);
                okHttpAPICalls.run(Constants.RequestTags.UPDATE_TOKEN, mBundle);
            } else {

                //gcmToken= FirebaseInstanceId.getInstance().getToken();
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                gcmToken = task.getResult().getToken();


                            }
                        });
                mBundle.putString(Constants.Extras.VERSION_NAME, versionName);
                mBundle.putString(Constants.Extras.DEVICE_ID, gcmToken);
                okHttpAPICalls.run(Constants.RequestTags.UPDATE_TOKEN, mBundle);
            }
        }
    }

    void checkallPermissions() {
        permissionArray.clear();
        for (int i = 0; i < Constants.Splash.PERMISSION_ARRAY.length; i++) {
            String str = Constants.Splash.PERMISSION_ARRAY[i];
            if (!checkPermission(str)) {
                permissionArray.add(str);
            }
        }
        init();
    }

    boolean checkPermission(String permission) {
        return (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
    }

    void init() {
        String string[] = new String[permissionArray.size()];
        string = permissionArray.toArray(string);
        if (this.permissionArray.size() > 0) {
            //ToastUtils.showLongToast(this, getString(R.string.permission_toast));
            ActivityCompat.requestPermissions(this, string, Constants.Splash.PERMISSION_REQUEST_CODE);
        } else {
            ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(getResources().getInteger(R.integer.splash_time));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            if (!sessionManager.getKeyIsLoggedIn()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                        sessionManager.setKeyBookingStatus(0);
                                        finish();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateStatus();
                                    }
                                });
                            }
                        }
                    }
                };
                timer.start();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage(R.string.no_internet);
                builder.setCancelable(false);
                builder.setNegativeButton(R.string.ok_large, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    void checkGps() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getString(R.string.enable_gps));
            dialog.setPositiveButton(getString(R.string.enable_large), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent, Constants.Splash.RESPONSE_CODE);
                }
            });
            dialog.setNegativeButton(getString(R.string.close_large), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        } else {
            if (!AppUtils.isServiceRunning(this, SendService.class)) {
                startService(new Intent(this, SendService.class));
            }
            getDataFromServer();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.Splash.RESPONSE_CODE) {
            checkGps();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.Splash.PERMISSION_REQUEST_CODE) {
            checkallPermissions();
        }
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        String jsonResponse = response.body().string();
        switch (requestType) {
            case Constants.RequestTags.UPDATE_TOKEN:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status=jsonObject.getString("status");
                    //JSONObject data = jsonObject.getJSONObject("data");
                        if(status.equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkGps();
                                }
                            });

                        }
                        else{

                        }
                    } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLongToast(SplashActivity.this, getString(R.string.try_again_later));
                        }
                    });
                }
                break;
            case Constants.RequestTags.RESTORE_STATE:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status=jsonObject.getString("status");
                    if(status.equalsIgnoreCase("200")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        final String type = data.getString("type");
                        if(type.equalsIgnoreCase("driver")){
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
                            Intent intent = new Intent(this, DialogActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else if(type.equalsIgnoreCase("startRide")){
                            Intent intent = new Intent(SplashActivity.this, DriverTrackingActivity.class);
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
                            bundle.putInt(Constants.IntentParameters.TYPE, Constants.Extras.FROM_SPLASH);
                            bundle.putString(Constants.Extras.TYPE,data.getString("type"));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();

                        }
                        else if(type.equalsIgnoreCase("rideStarted")){
                            Intent intent = new Intent(SplashActivity.this, DriverTrackingActivity.class);
                            NewBookingModel newBookingModel = new NewBookingModel();
                            newBookingModel.setBookingId(data.getString("bookingId"));
                            sessionManager.setKeyBookingId(data.getString("bookingId"));
                            newBookingModel.setCurrentlat(data.getString("slat"));
                            newBookingModel.setCurrentlng(data.getString("slng"));
                            newBookingModel.setDeslat(data.getString("dlat"));
                            newBookingModel.setDeslng(data.getString("dlng"));
                            newBookingModel.setName(data.getString("name"));
                            newBookingModel.setMobile(data.getString("mobile"));
                            String json = new Gson().toJson(newBookingModel, NewBookingModel.class);
                            sessionManager.setKeyBookingJson(json);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.Extras.TYPE,data.getString("type"));
                            bundle.putInt(Constants.IntentParameters.TYPE, Constants.Extras.FROM_SPLASH);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        else if(type.equalsIgnoreCase("bookingStopped")){
                            Intent intent = new Intent(SplashActivity.this, DriverTrackingActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(Constants.IntentParameters.TYPE, Constants.Extras.FROM_SPLASH);
                            bundle.putString(Constants.Extras.TYPE,data.getString("type"));;
                            intent.putExtras(bundle);
                            sessionManager.setFareData(data.toString());
                            Log.e("data.to show",data.toString());
                            startActivity(intent);
                            finish();
                                                    }
                        else{
                            startService(new Intent(this, SendService.class));
                            Intent intent = new Intent(SplashActivity.this, DriverActivity.class);
                            startActivity(intent);
                            Log.e(TAG, String.valueOf(sessionManager.getKeyRestore()));
                            sessionManager.setKeyBookingStatus(0);
                            finish();
                        }

                    }else if (status.equalsIgnoreCase("300")){
                        startService(new Intent(this, SendService.class));
                        Intent intent = new Intent(SplashActivity.this, DriverActivity.class);
                        startActivity(intent);
                        Log.e(TAG, String.valueOf(sessionManager.getKeyRestore()));
                        sessionManager.setKeyBookingStatus(0);
                        finish();
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                show_snakBar();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLongToast(SplashActivity.this, getString(R.string.try_again_later));
                        }
                    });
                }
                break;


        }
    }
    @Override
    public void onHttpRequestFailure(String requestType, Request request, String errorMessage) {

    }
    void show_snakBar() {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.TimeOutError), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry_large), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDataFromServer();
                    }
                });
        snackbar.setActionTextColor(getResources().getColor(R.color.snackbar_button_text));

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.snackbar_text));
        snackbar.show();
    }
    private void getDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);

        okHttpAPICalls.run(Constants.RequestTags.RESTORE_STATE, null);
    }

}


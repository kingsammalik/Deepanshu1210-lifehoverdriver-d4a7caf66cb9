package com.medulance.driver.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.medulance.driver.App.Constants;
import com.medulance.driver.App.GcmBroadcastReceiver;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.App.SendService;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.AppUtils;
import com.medulance.driver.Utils.DialogUtil;
import com.medulance.driver.Utils.LogUtils;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.DividerItemDecoration;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.models.LoginModel;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.madx.updatechecker.lib.UpdateRunnable;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DriverActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, IOkHttpNotify {

    private static final String TAG = DriverActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout ll_progress;
    private Toolbar toolbar;
    private ImageView iv_menu;
    private TextView tv_title;
    private LinearLayout ll_pickup_location;
    private TextView tv_pickup_location;
    private TextView tv_driver_name, tv_ambulance_type, tv_ambulance_num;
    private SessionManager sessionManager;
    private Receiver receiver;
    boolean registered = false;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private OKHttpAPICalls okHttpAPICalls;
    private BitmapDescriptor bitmapDescriptor;
    private int i = 0;
    private Switch sw_active;
    private Bundle mBundle;
    SharedPreferences sharedPreferences;
    CoordinatorLayout relative;
    private Handler hand1 = new Handler();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_driver);
        new UpdateRunnable(this, new Handler()).start();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MyApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open")
                .setLabel("settings")
                .build());
        if(AppUtils.isServiceRunning(this, SendService.class)){

        }
        Log.e("Update Is running ","Update Complete");
        sessionManager = MyApplication.getInstance().getSession();
        mBundle = new Bundle();
        setOkHttpAPICalls();
        initializeViews();
        getMineMarker();
        sharedPreferences=getSharedPreferences("Lat_Lng",Context.MODE_PRIVATE);
        setSupportActionBar(toolbar);

        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(this));
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        setUpNavigationView(view);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        iv_menu.setOnClickListener(this);

        String name = sessionManager.getKeyName();
        if (name != null) {
            tv_driver_name.setText(name);
            tv_driver_name.setVisibility(View.VISIBLE);
        } else {
            tv_driver_name.setVisibility(View.GONE);
        }

        String ambulance_type = sessionManager.getKeyAmbulanceType();
        if (ambulance_type != null) {
            tv_ambulance_type.setText(ambulance_type + " Ambulance");
            tv_ambulance_type.setVisibility(View.VISIBLE);
        } else {
            tv_ambulance_type.setVisibility(View.GONE);
        }

        String loginResponse = sessionManager.getKeyLoginResponse();
        if (loginResponse != null) {
            LoginModel loginModel = new Gson().fromJson(loginResponse, LoginModel.class);
            tv_ambulance_num.setText("Ambulance Number -  " + loginModel.getData().getAmbulance_number());
            tv_ambulance_num.setVisibility(View.VISIBLE);
        } else {
            tv_ambulance_num.setVisibility(View.GONE);
        }

        receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.Extras.LOCATION_BROADCAST);
        registerReceiver(receiver, intentFilter);
        registered = true;
    }
    Runnable run1 = new Runnable() {
        public void run() {
            if(AppUtils.isServiceRunning(getApplicationContext(), SendService.class)){
                checkGps();
                hand1.postDelayed(run1, Constants.Extras.GET_DATA_INTERVAL);
            }


        }
    };
    @Override
    public void onResume() {
        checkGps();
        hand1.postDelayed(run1, Constants.Extras.GET_DATA_INTERVAL);
        super.onResume();

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
            startService(new Intent(this, SendService.class));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.Splash.RESPONSE_CODE) {
            checkGps();
        }
    }

    private void setUpNavigationView(View view) {
        if (view != null) {
            relative=(CoordinatorLayout) findViewById(R.id.container);
            TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
            TextView tv_mobile = (TextView) view.findViewById(R.id.tv_user_phone);
            TextView tv_ambulance_type = (TextView) view.findViewById(R.id.tv_ambulance_type);
            LinearLayout ll_active = (LinearLayout) view.findViewById(R.id.ll_active);
            sw_active = (Switch) view.findViewById(R.id.sw_active);
            tv_username.setText(sessionManager.getKeyName());
            tv_mobile.setText(sessionManager.getKeyMobile());
            tv_ambulance_type.setText(sessionManager.getKeyAmbulanceType() + " Ambulance");
            ll_active.setOnClickListener(this);
            sw_active.setChecked(sessionManager.getKeyIsActive());
        }
    }

    private void initializeViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
        ll_pickup_location = (LinearLayout) findViewById(R.id.ll_pickup_location);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_pickup_location = (TextView) findViewById(R.id.tv_pickup_location);
        tv_driver_name = (TextView) findViewById(R.id.tv_driver_name);
        tv_ambulance_type = (TextView) findViewById(R.id.tv_ambulance_type);
        tv_ambulance_num = (TextView) findViewById(R.id.tv_ambulance_num);
    }

    public void setToolbarTitle(int stringResourceId) {
        tv_title.setText(getString(stringResourceId));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    public void showLoader() {
        ll_progress.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        ll_progress.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                LogUtils.d(TAG, "adkjadkjda");
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.ll_active:
                boolean isActive = sessionManager.getKeyIsActive();
                showLoader();
                if (isActive) {
                    mBundle.putString(Constants.Extras.STATUS, "0");
                } else {
                    mBundle.putString(Constants.Extras.STATUS, "1");
                }
                okHttpAPICalls.run(Constants.RequestTags.DRIVER_STATUS, mBundle);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goTo(item.getItemId());
            }
        }, 200);
        return false;
    }

    private void goTo(int id) {
        Intent intent = new Intent(DriverActivity.this, ExtraActivity.class);
        switch (id) {
            case R.id.nav_bookings:
                intent.putExtra(Constants.IntentParameters.WHICH, Constants.NavigationDrawer.BOOKING_HISTORY);
                startActivity(intent);
                break;
            case R.id.nav_account:
                intent.putExtra(Constants.IntentParameters.WHICH, Constants.NavigationDrawer.ACCOUNT);
                startActivity(intent);
                break;
            case R.id.nav_call_base:
                intent.putExtra(Constants.IntentParameters.WHICH, Constants.NavigationDrawer.CALL_BASE);
                startActivity(intent);
                break;
            case R.id.nav_fare_chart:
                intent.putExtra(Constants.IntentParameters.WHICH, Constants.NavigationDrawer.FARE_CHART);
                startActivity(intent);
                break;
            case R.id.nav_about:
                intent.putExtra(Constants.IntentParameters.WHICH, Constants.NavigationDrawer.ABOUT);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cManager.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    DialogUtil.showPopUp(this, null, getString(R.string.sure_logout), getString(R.string.yes_large), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getLogout();
                        }
                    }, getString(R.string.no_large), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                } else {
                    Snackbar snack = Snackbar.make(relative, "Not connected to internet", Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();
                }
                break;
            case R.id.nav_update:
                new UpdateRunnable(DriverActivity.this, new Handler()).force(true).start();
                break;
        }
    }

    private void getLogout() {
        showLoader();
        Intent myService = new Intent(DriverActivity.this, SendService.class);
        stopService(myService);
        okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGOUT, null);
    }

    /*private void getLogout() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.show();
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.APIParameters.ID, MyApplication.getInstance().getSession().getKeyId());
        map.put(Constants.APIParameters.MANAGER, Constants.Extras.MANAGER_ID);
        JSONObject jsonObject = new JSONObject(map);
        LogUtils.d(TAG, jsonObject.toString());
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(AppConfig.URL_USER_LOGOUT, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.d(TAG, response.toString());
                pDialog.cancel();
                MyApplication.getInstance().getSession().clear();
                Intent intent1 = new Intent(UserMainActivity.this, Login.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();
            }
        });
        MyApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }*/

    @Override
    protected void onPause() {
        Intent intent = new Intent("restartApps");
        sendBroadcast(intent);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Intent intent = new Intent("restartApps");
        sendBroadcast(intent);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent("restartApps");
        sendBroadcast(intent);
        if (registered) {
            unregisterReceiver(receiver);
            registered = false;
        }
        super.onDestroy();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setBuildingsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        /*View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);*/

    }

    public void setOkHttpAPICalls() {
        this.okHttpAPICalls = new OKHttpAPICalls();
        this.okHttpAPICalls.setOnOkHttpNotifyListener(this);
    }

    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        String jsonResponse = response.body().string();
        switch (requestType) {
            case Constants.RequestTags.DRIVER_LOGOUT:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    stopService(new Intent(DriverActivity.this, SendService.class));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                ToastUtils.showLongToast(DriverActivity.this, getString(R.string.success));
                                sessionManager.clear();
                                Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        final String message = data.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverActivity.this, message);
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLongToast(DriverActivity.this, getString(R.string.try_again_later));
                            hideLoader();
                        }
                    });
                }
                break;
            case Constants.RequestTags.DRIVER_STATUS:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean isActive = sessionManager.getKeyIsActive();
                                sw_active.setChecked(!isActive);
                                sessionManager.setKeyIsActive(!isActive);
                                ToastUtils.showLongToast(DriverActivity.this, getString(R.string.success));
                            }
                        });
                    } else {
                        final JSONObject data = jsonObject.getJSONObject("data");
                        final String message = data.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverActivity.this, message);
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader();
                            ToastUtils.showLongToast(DriverActivity.this, getString(R.string.try_again_later));
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onHttpRequestFailure(String requestType, Request request, String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideLoader();
                    ToastUtils.showLongToast(DriverActivity.this, getString(R.string.TimeOutError));
                }
            });
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getIntExtra(Constants.IntentParameters.TYPE, 0) == Constants.Extras.LOCATION_BROADCAST_TYPE) {
                    double lat = intent.getDoubleExtra(Constants.IntentParameters.LATITUDE, 0.0);
                    double lng = intent.getDoubleExtra(Constants.IntentParameters.LONGITUDE, 0.0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.Extras.LATITUDE, String.valueOf(lat));
                    editor.putString(Constants.Extras.LONGITUDE, String.valueOf(lng));
                    editor.commit();
                    MyApplication.getInstance().setLatLng(lat,lng);
                    GcmBroadcastReceiver.completeWakefulIntent(intent);
                    drawMarker(lat, lng);
                }else if (intent.getIntExtra(Constants.IntentParameters.TYPE,0) == Constants.Extras.LOGOUT_BY_MANAGER){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ToastUtils.showLongToast(DriverActivity.this,"Session Expired!! Please Login Again.");
                                stopService(new Intent(DriverActivity.this, SendService.class));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            sessionManager.clear();
                            Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                }
            }
        }
    }

    private void drawMarker(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        googleMap.clear();
        if (i == 0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
            googleMap.animateCamera(cameraUpdate);
            i = 1;
        }
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title("My Location");
        options.icon(bitmapDescriptor);
        googleMap.addMarker(options);
    }

    public void getMineMarker() {
        String ambulanceType = sessionManager.getKeyAmbulanceType();
        if (ambulanceType != null) {
            if (ambulanceType.equalsIgnoreCase("ALS")) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_als);
            } else if (ambulanceType.equalsIgnoreCase("BLS")) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_bls);
            } else if (ambulanceType.equalsIgnoreCase("PATIENT TRANSPORT")) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_pt);
            } else {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_mortuary);
            }
        }
    }
}

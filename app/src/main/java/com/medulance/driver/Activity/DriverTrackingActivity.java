package com.medulance.driver.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;

import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.medulance.driver.App.Constants;
import com.medulance.driver.App.DirectionsJSONParser;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.App.SendService;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.DialogUtil;
import com.medulance.driver.Utils.LogUtils;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.DividerItemDecoration;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.models.NewBookingModel;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DriverTrackingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener, IOkHttpNotify, RatingBar.OnRatingBarChangeListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout ll_progress;
    private Toolbar toolbar;
    private ImageView iv_menu;
    private TextView tv_title;
    private Receiver receiver;
    boolean registered = false;
    private SupportMapFragment mapFragment;
    private SessionManager sessionManager;
    private LinearLayout ll_booking_tracking;
    private ImageView iv_tracking,iv_timer;
    private TextView tv_user_details;
    private LinearLayout ll_before_start;
    private LinearLayout ll_call_user;
    private LinearLayout ll_start_ride;
    private LinearLayout ll_stop_ride;
    private LinearLayout ll_fare_layout;
    private String TAG = DriverTrackingActivity.class.getSimpleName();
    private double currentLat, currentLng;
    private double destinationLat, destinationLng;
    private NewBookingModel newBookingModel;
    List<Marker> markers = new ArrayList<>();
    private BitmapDescriptor destinationMarkerIcon;
    private Bundle mBundle;
    private OKHttpAPICalls okHttpAPICalls;
    private TextView tv_amount_large, tv_ambulance_type, tv_total_fare, tv_total_distance;
    private String amount;
    private String distance;
    private Button btn_done;
    private AlertDialog alertDialog,alertDialog1;
    private LinearLayout ll_rating;
    private RatingBar rb_rating;
    private TextView tv_submit,tv_btn_start;
    private Button btn_paytm;
    private int whichState = 0;
    private JSONObject data;
    String Payment_Type;
    ProgressBar progressBar;
    String Device;
    Button accept_cash;
    private String bookingId;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    String type;
    int from;
    String Tag="arrive";
    private GoogleMap googleMap;
    PolylineOptions polylineOptions;
    LatLng sourcePosition,destPosition;
    RelativeLayout timer_relative;
    TextView timer_text;
    Button timer_start,timer_stop;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int start_count=0;
    int stop_count=0;
    int secs;
    ImageView tic_toc;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_driver_tracking);
        sharedPreferences = getSharedPreferences("Lat_Lng", Context.MODE_PRIVATE);
        mBundle = getIntent().getExtras();
        from=mBundle.getInt(Constants.IntentParameters.TYPE);
        type=mBundle.getString(Constants.Extras.TYPE);
        Log.e("Type i am getting",type+from);
        sessionManager = MyApplication.getInstance().getSession();
        bookingId=sessionManager.getKeyBookingId();
        newBookingModel = new Gson().fromJson(sessionManager.getKeyBookingJson(), NewBookingModel.class);
        destinationMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.pharmacy_hospital);
       // progressBar=new ProgressBar(this);

        initializeViews();
        setSupportActionBar(toolbar);
        setOkHttpAPICalls();
        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(this));
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        if (view != null) {
            TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
            TextView tv_mobile = (TextView) view.findViewById(R.id.tv_user_phone);
            TextView tv_ambulance_type = (TextView) view.findViewById(R.id.tv_ambulance_type);
            tv_username.setText(sessionManager.getKeyName());
            tv_mobile.setText(sessionManager.getKeyMobile());
            tv_ambulance_type.setText(sessionManager.getKeyAmbulanceType() + " Ambulance");
        }

        mapFragment.getMapAsync(this);

        receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.Extras.LOCATION_BROADCAST);
        registerReceiver(receiver, intentFilter);
        registered = true;

        if (from == Constants.Extras.FROM_SPLASH){
                if(type.equalsIgnoreCase("startRide")){
                    destinationLat = Double.parseDouble(newBookingModel.getCurrentlat());
                    destinationLng = Double.parseDouble(newBookingModel.getCurrentlng());
                    String user_details = newBookingModel.getName() + " - " + newBookingModel.getMobile();
                    tv_user_details.setText(user_details);
                    showBeforeStart();
                    if(sessionManager.getKeyWaitingTime()!=0){
                        updatedTime = sessionManager.getKeyWaitingTime();
                        int sec = (int) (updatedTime / 1000);
                        int mins = sec / 60;
                        int hours = mins / 60;
                        sec = sec % 60;
                        mins= mins % 60;
                        int milliseconds = (int) (updatedTime % 1000);
                        String time = String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", sec);
                        timer_text.setText(time);
                    }
                }
                else if(type.equalsIgnoreCase("rideStarted")){
                    iv_timer.setVisibility(View.VISIBLE);
                    destinationLat = Double.parseDouble(newBookingModel.getDeslat());
                    destinationLng = Double.parseDouble(newBookingModel.getDeslng());
                    String user_details = newBookingModel.getName() + " - " + newBookingModel.getMobile();
                    tv_user_details.setText(user_details);
                    showRideStartedLayout();
                    if(sessionManager.getKeyWaitingTime()!=0){
                        updatedTime = sessionManager.getKeyWaitingTime();
                        int sec = (int) (updatedTime / 1000);
                        int mins = sec / 60;
                        int hours = mins / 60;
                        sec = sec % 60;
                        mins= mins % 60;
                        int milliseconds = (int) (updatedTime % 1000);
                        String time = String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", sec);
                        timer_text.setText(time);
                    }
                } else if(type.equalsIgnoreCase("bookingStopped")){
                    try {
                        if(sessionManager.getKeyFareData()!=null) {
                            JSONObject jsonObject = new JSONObject(sessionManager.getKeyFareData());
                            showFareLayout(jsonObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    completeride();
                }

            }
        else{
            destinationLat = Double.parseDouble(newBookingModel.getCurrentlat());
            destinationLng = Double.parseDouble(newBookingModel.getCurrentlng());
            String user_details = newBookingModel.getName() + " - " + newBookingModel.getMobile();
            tv_user_details.setText(user_details);
            showBeforeStart();
        }

        checkGps();
        if(Tag.equalsIgnoreCase("arrive")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ambulance);
            playAudio();
            mediaPlayer.start();
        }
    }

    private void playAudio() {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run(){
                mediaPlayer.stop();
            }
        }, 10 * 1000);
    }

    private void initializeViews() {
        tv_btn_start=(TextView)findViewById(R.id.tv_btn_start);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ll_booking_tracking = (LinearLayout) findViewById(R.id.ll_booking_tracking);
        iv_tracking = (ImageView) findViewById(R.id.iv_tracking);
        iv_timer=(ImageView) findViewById(R.id.iv_timer);
        tv_user_details = (TextView) findViewById(R.id.tv_user_details);
        ll_before_start = (LinearLayout) findViewById(R.id.ll_before_start);
        ll_call_user = (LinearLayout) findViewById(R.id.ll_call_user);
        ll_start_ride = (LinearLayout) findViewById(R.id.ll_start_ride);
        ll_stop_ride = (LinearLayout) findViewById(R.id.ll_stop_ride);
        ll_fare_layout = (LinearLayout) findViewById(R.id.ll_fare_layout);
        accept_cash=(Button)findViewById(R.id.accept_cash);
        tv_amount_large = (TextView) findViewById(R.id.tv_amount_large);
        tv_ambulance_type = (TextView) findViewById(R.id.tv_ambulance_type);
        tv_total_fare = (TextView) findViewById(R.id.tv_total_fare);
        tv_total_distance = (TextView) findViewById(R.id.tv_total_distance);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        ll_rating = (LinearLayout) findViewById(R.id.ll_rating);
        rb_rating = (RatingBar) findViewById(R.id.rb_rating);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        timer_relative=(RelativeLayout)findViewById(R.id.timer_relative);
        timer_text=(TextView)findViewById(R.id.tv_timer);
        timer_start=(Button)findViewById(R.id.timer_start);
        timer_stop=(Button)findViewById(R.id.timer_stop);
        ll_progress.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
        ll_booking_tracking.setOnClickListener(this);
        iv_tracking.setOnClickListener(this);
        ll_before_start.setOnClickListener(this);
        ll_call_user.setOnClickListener(this);
        ll_start_ride.setOnClickListener(this);
        ll_stop_ride.setOnClickListener(this);
        ll_fare_layout.setOnClickListener(this);
        iv_timer.setOnClickListener(this);
        //btn_done.setOnClickListener(this);
        //btn_paytm.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

        rb_rating.setOnRatingBarChangeListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goTo(item.getItemId());
            }
        }, 300);
        return false;
    }

    private void goTo(int id) {
        Intent intent = new Intent(DriverTrackingActivity.this, ExtraActivity.class);
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
                    ToastUtils.showShortToast(DriverTrackingActivity.this, getString(R.string.no_connection));
                }
                break;
        }

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        tv_submit.setVisibility(View.VISIBLE);
    }

    private void getLogout() {
        showLoader();
        okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGOUT, null);
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

        if (MyApplication.getInstance().getLat() != 0.0 && MyApplication.getInstance().getLng() != 0.0) {
            currentLat = MyApplication.getInstance().getLat();
            currentLng = MyApplication.getInstance().getLng();
            drawMarker();
            sourcePosition=new LatLng(currentLat,currentLng);
            destPosition=new LatLng(destinationLat,destinationLng);
            String url = getDirectionsUrl(sourcePosition, destPosition);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }else{
            String lat=sharedPreferences.getString(Constants.Extras.LATITUDE,null);
            String lng=sharedPreferences.getString(Constants.Extras.LONGITUDE,null);
            currentLat= Double.parseDouble(lat);
            currentLng= Double.parseDouble(lng);
            drawMarker();
            sourcePosition=new LatLng(currentLat,currentLng);
            destPosition=new LatLng(destinationLat,destinationLng);
            String url = getDirectionsUrl(sourcePosition, destPosition);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
               // drawMarker();
                return true;
            }
        });
    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){


        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&mode=driving&key=AIzaSyBgfdcAKt2CzWbtEgRCBxfl_10Bki-7X-c";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while ", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            if(result!=null) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(getResources().getColor(R.color.blue));
                }
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions!=null) {
                googleMap.addPolyline(lineOptions);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_progress:
                break;
            case R.id.iv_menu:
                LogUtils.d(TAG, "adkjadkjda");
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.ll_booking_tracking:
                break;
            case R.id.iv_timer:
                setButtonOffAndON(false);
                Log.e("Your Time","timer nahi chal rha");
                showTimer();
                break;
            case R.id.iv_tracking:
                if (currentLat != 0.0 && currentLng != 0.0 && destinationLat != 0.0 && destinationLng != 0.0) {
                    try {
                       /* Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=" + currentLat + "," + currentLng + "&daddr=" + destinationLat + "," + destinationLng);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
                        startActivity(intent);*/
                        Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" +destinationLat+ ","+destinationLng+""));
                        this.startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.device_not_supported));
                    }
                }
                break;
            case R.id.ll_before_start:
                break;
            case R.id.ll_call_user:
                Intent intent = new Intent("android.intent.action.CALL");
                intent.setData(Uri.parse("tel:" + newBookingModel.getMobile()));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showLongToast(this, getString(R.string.give_permission));
                    return;
                }
                startActivity(intent);
                break;
            case R.id.ll_start_ride:
                if(Tag.equalsIgnoreCase("start")) {
                    showLoader();
                    mBundle.putString(Constants.Extras.LATITUDE, String.valueOf(currentLat));
                    mBundle.putString(Constants.Extras.LONGITUDE, String.valueOf(currentLng));
                    if (bookingId != null) {
                        mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                    } else {
                        bookingId = newBookingModel.getBookingId();
                        sessionManager.setKeyBookingId(newBookingModel.getBookingId());
                        bookingId = sessionManager.getKeyBookingId();
                        mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                    }
                    okHttpAPICalls.run(Constants.RequestTags.START_RIDE, mBundle);
                }else if(Tag.equalsIgnoreCase("arrive")){

                    if (bookingId != null) {
                        showLoader();
                        mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                        okHttpAPICalls.run(Constants.RequestTags.DRIVER_ARRIVE,mBundle);
                    } else {
                        showLoader();
                        bookingId = newBookingModel.getBookingId();
                        mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                        okHttpAPICalls.run(Constants.RequestTags.DRIVER_ARRIVE,mBundle);
                    }


                }
                break;
            case R.id.ll_stop_ride:
                showLoader();
                mBundle.putString(Constants.Extras.LATITUDE, String.valueOf(currentLat));
                mBundle.putString(Constants.Extras.LONGITUDE, String.valueOf(currentLng));
                mBundle.putString(Constants.Extras.WAITING_TIME,String.valueOf(secs));
                if(bookingId!=null) {
                    mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                }else{
                    bookingId=newBookingModel.getBookingId();
                    sessionManager.setKeyBookingId(newBookingModel.getBookingId());
                    bookingId=sessionManager.getKeyBookingId();
                    mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                }
                okHttpAPICalls.run(Constants.RequestTags.STOP_RIDE, mBundle);
                break;
        }
    }

    public void setButtonOffAndON(Boolean set){
        ll_call_user.setClickable(set);
        ll_start_ride.setClickable(set);
        ll_stop_ride.setClickable(set);
        iv_menu.setClickable(set);
        iv_tracking.setClickable(set);
        iv_timer.setClickable(set);
    }

    private void showTimer(){
        stop_count=1;
        timer_stop.setText("Exit");
        timer_relative.setVisibility(View.VISIBLE);
        timer_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_count==0) {
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                    start_count++;
                    stop_count=0;
                    timer_stop.setText("Stop");
                    setButtonOffAndON(false);
                }
            }
        });
        timer_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stop_count==0) {
                    if(sessionManager.getKeyWaitingTime()==0) {
                        timeSwapBuff += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimerThread);
                        timer_relative.setVisibility(View.GONE);
                        secs = (int) (timeSwapBuff / 1000);
                        Log.e("Total time in secs", "" + secs);
                        sessionManager.setKeyWaitingTime(timeSwapBuff);
                        stop_count++;
                        start_count = 0;
                        setButtonOffAndON(true);
                    }else{
                        timeSwapBuff=sessionManager.getKeyWaitingTime();
                        timeSwapBuff += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimerThread);
                        timer_relative.setVisibility(View.GONE);
                        secs = (int) (timeSwapBuff / 1000);
                        Log.e("Total time in secs", "" + secs);
                        sessionManager.setKeyWaitingTime(timeSwapBuff);
                        stop_count++;
                        start_count = 0;
                        setButtonOffAndON(true);
                    }
                }else if(stop_count==1){
                    if(sessionManager.getKeyWaitingTime()==0) {
                        secs = (int) (timeSwapBuff / 1000);
                        Log.e("Total time in secs", "" + secs);
                        timer_relative.setVisibility(View.GONE);
                        start_count=0;
                        setButtonOffAndON(true);
                    }else{
                        secs = (int) (sessionManager.getKeyWaitingTime() / 1000);
                        Log.e("Total time in secs", "" + secs);
                        timer_relative.setVisibility(View.GONE);
                        start_count=0;
                        setButtonOffAndON(true);
                    }
                }
            }
        });
    }
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            if(sessionManager.getKeyWaitingTime()==0) {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updatedTime = timeSwapBuff + timeInMilliseconds;
                int sec = (int) (updatedTime / 1000);
                int mins = sec / 60;
                sec = sec % 60;
                int hours = mins / 60;
                RotateAnimation rotateAnimation = new RotateAnimation(
                        (sec) * 6, sec * 6,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                rotateAnimation.setInterpolator(new LinearInterpolator());
                rotateAnimation.setDuration(1000);
                rotateAnimation.setFillAfter(true);

                int milliseconds = (int) (updatedTime % 1000);
                //sessionManager.setKeyWaitingTime(updatedTime);
                String time = String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", sec);
                timer_text.setText(time);
                customHandler.postDelayed(this, 0);
            }else{
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updatedTime = sessionManager.getKeyWaitingTime() + timeInMilliseconds;
                int sec = (int) (updatedTime / 1000);
                int mins = sec / 60;
                int hours = mins / 60;
                sec = sec % 60;
                mins= mins % 60;
                int milliseconds = (int) (updatedTime % 1000);
                String time = String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", sec);
                timer_text.setText(time);
               // sessionManager.setKeyWaitingTime(updatedTime);
                customHandler.postDelayed(this, 0);
            }
        }

    };

    private void showLoader() {
        ll_progress.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        ll_progress.setVisibility(View.GONE);
    }

    public void setToolbarTitle(int stringResourceId) {
        tv_title.setText(getString(stringResourceId));
    }

    public void setOkHttpAPICalls() {
        this.okHttpAPICalls = new OKHttpAPICalls();
        this.okHttpAPICalls.setOnOkHttpNotifyListener(this);
    }


    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        String jsonResponse = response.body().string();
        switch (requestType) {
            case Constants.RequestTags.START_RIDE:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (status.equalsIgnoreCase("200")) {
                        destinationLat = Double.parseDouble(data.getString("dlat"));
                        destinationLng = Double.parseDouble(data.getString("dlng"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showRideStartedLayout();
                                drawMarker();
                                sourcePosition=new LatLng(currentLat,currentLng);
                                destPosition=new LatLng(destinationLat,destinationLng);
                                String url = getDirectionsUrl(sourcePosition, destPosition);

                                DownloadTask downloadTask = new DownloadTask();

                                downloadTask.execute(url);

                            }
                        });
                    } else {
                        final String message = data.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sessionManager.setKeyBookingStatus(0);
                                reset();
                                startActivity(new Intent(DriverTrackingActivity.this, DriverActivity.class));
                                finish();
                                ToastUtils.showLongToast(DriverTrackingActivity.this, message);

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
                            ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
                        }
                    });
                }
                break;
            case Constants.RequestTags.STOP_RIDE:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    final JSONObject data = jsonObject.getJSONObject("data");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showFareLayout(data);
                                sessionManager.setKeyBookingStatus(1);
                                sessionManager.setKeyWaitingTime(0);
                            }
                        });
                    } else {
                        final String message = data.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverTrackingActivity.this, message);
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
                            ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
                        }
                    });
                }
                break;
            case Constants.RequestTags.DRIVER_LOGOUT:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    stopService(new Intent(DriverTrackingActivity.this, SendService.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.success));
                                sessionManager.clear();
                                Intent intent = new Intent(DriverTrackingActivity.this, LoginActivity.class);
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
                                ToastUtils.showLongToast(DriverTrackingActivity.this, message);
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
                            ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
                            hideLoader();
                        }
                    });
                }
                break;
            case Constants.RequestTags.PAYMENT_TYPE:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.success));
                                completeride();
                               /* startActivity(new Intent(DriverTrackingActivity.this, DriverActivity.class));*/
                                sessionManager.setKeyBookingStatus(0);
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
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
                            ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
                            hideLoader();
                        }
                    });
                }
                break;
            case Constants.RequestTags.RIDE_RATING:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.success));
                                completeride();
                                try {
                                    alertDialog.cancel();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                startActivity(new Intent(DriverTrackingActivity.this, DriverActivity.class));
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
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
                            ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
                            hideLoader();
                        }
                    });
                }
                break;
            case Constants.RequestTags.DRIVER_ARRIVE:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_btn_start.setText(R.string.start_ride);
                                iv_timer.setVisibility(View.VISIBLE);
                                Tag="start";
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
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
                            ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
                            hideLoader();
                        }
                    });
                }
                break;
            case Constants.RequestTags.CONFIRM_PAYMENT:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.success));
                                completeride();
                                try {
                                    //hideLoader();
                                    progressDialog.cancel();
                                    if(alertDialog.isShowing()) {
                                        alertDialog.dismiss();
                                    }
                                    completeride();
                                    //hideLoader();
                                    /*reset();
                                    startActivity(new Intent(DriverTrackingActivity.this, DriverActivity.class));
                                    finish();*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                /*startActivity(new Intent(DriverTrackingActivity.this, DriverActivity.class));
                                finish();*/
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.try_again_later));
                            hideLoader();
                        }
                    });
                }
                break;

        }
    }

    private void showBeforeStart() {
        //sessionManager.setKeyBookingStatus(1);
        //whichState = Constants.Extras.BEFORESTARTEDLAYOUT;
        ll_before_start.setVisibility(View.VISIBLE);
        ll_stop_ride.setVisibility(View.GONE);
        ll_fare_layout.setVisibility(View.GONE);
        ll_booking_tracking.setVisibility(View.VISIBLE);
    }

    private void completeride(){
        whichState = Constants.Extras.AFTERFARELAYOUT;
        Intent intent = new Intent(DriverTrackingActivity.this,DriverActivity.class);
        startActivity(intent);

    }
    private void showRideStartedLayout() {
        whichState = Constants.Extras.STARTEDLAYOUT;
        ll_before_start.setVisibility(View.GONE);
        ll_stop_ride.setVisibility(View.VISIBLE);
        ll_fare_layout.setVisibility(View.GONE);
        ll_booking_tracking.setVisibility(View.VISIBLE);
    }

    private void showFareLayout(JSONObject data) {
        this.data = data;
        try {
            Device=data.getString("device");
            whichState = Constants.Extras.FARELAYOUT;
            setToolbarTitle(R.string.amount_payable);
            amount = data.getString("amount");
            distance = data.getString("distance");
            bookingId = data.getString("booking_id");
            // TODO: 24/9/16
            tv_amount_large.setText("Rs " + amount);
            tv_ambulance_type.setText("Type: " + sessionManager.getKeyAmbulanceType() + " Ambulance");
            tv_total_fare.setText("Total Fare: Rs " + amount);
            tv_total_distance.setText("Total Distance: " + distance + " km");
            ll_fare_layout.setVisibility(View.VISIBLE);
            ll_booking_tracking.setVisibility(View.GONE);
            if(Device.equalsIgnoreCase("A")){
                accept_cash.setVisibility(View.VISIBLE);
                accept_cash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog = new ProgressDialog(DriverTrackingActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.please_wait));
                        progressDialog.show();
                        //bookingId=newBookingModel.getBookingId();
                        mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                        mBundle.putString(Constants.Extras.CASH, "cash");
                        okHttpAPICalls.run(Constants.RequestTags.CONFIRM_PAYMENT,mBundle);
                        //completeride();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHttpRequestFailure(String requestType, Request request, String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoader();
                ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.TimeOutError));
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
                    if (lat != 0.0 && lng != 0.0) {
                        currentLat = lat;
                        currentLng = lng;
                        drawMarker();
                        sourcePosition=new LatLng(currentLat,currentLng);
                        destPosition=new LatLng(destinationLat,destinationLng);
                        String url = getDirectionsUrl(sourcePosition, destPosition);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                } else if (intent.getIntExtra(Constants.IntentParameters.TYPE, 0) == Constants.Extras.DRIVER_RIDE_CANCELLED) {
                    showCancelledDialog();
                } else if (intent.getIntExtra(Constants.IntentParameters.TYPE, 0) == Constants.Extras.PAYING_BY_CASH) {
                    hideLoader();
                    //String cash=getIntent().getStringExtra("amount");
                    showPayingByCashDialog();
                }else if(intent.getIntExtra(Constants.IntentParameters.TYPE,0)==Constants.Extras.PAYING_WITH_PAYTM){
                   showPayingWithPaytmDialog();
                } else if(intent.getIntExtra(Constants.IntentParameters.TYPE,0)==Constants.Extras.PAID_WITH_PAYTM){
                    hideLoader();
                    alertDialog1.cancel();
                    showPaidWithPatymDialog();
                }/*else if(intent.getIntExtra(Constants.IntentParameters.TYPE,0)==Constants.Extras.PAYMENT_FAILED);{
                    hideLoader();
                    Toast.makeText(DriverTrackingActivity.this, "Transaction Unsuccessful", Toast.LENGTH_LONG).show();
                }*/
            }
        }
    }

    private void showPayingWithPaytmDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.paying_by_paytm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(view);
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    private void showPayingByCashDialog() {
        //alertDialog1.dismiss();
        View view = LayoutInflater.from(this).inflate(R.layout.paid_by_cash, null);
        Button collect_cash=(Button)view.findViewById(R.id.btn_collect_cash);
        TextView fare=(TextView)view.findViewById(R.id.fare);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(view);
        alertDialog = builder.create();
        fare.setText("Rs. "+amount);
        collect_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showLoader();
                progressDialog = new ProgressDialog(DriverTrackingActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.show();
                //bookingId=newBookingModel.getBookingId();
                mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                mBundle.putString(Constants.Extras.CASH, "cash");
                okHttpAPICalls.run(Constants.RequestTags.CONFIRM_PAYMENT,mBundle);

            }
        });
        alertDialog.show();
    }
    private void showPaidWithPatymDialog() {
       // alertDialog1.dismiss();
        View view = LayoutInflater.from(this).inflate(R.layout.ride_paid_with_patym, null);
        Button collect_cash=(Button)view.findViewById(R.id.btndone);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(view);
        alertDialog = builder.create();
        collect_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoader();
                //bookingId=newBookingModel.getBookingId();
                mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                mBundle.putString(Constants.Extras.CASH, "paytm");
                okHttpAPICalls.run(Constants.RequestTags.CONFIRM_PAYMENT,mBundle);

            }
        });
        alertDialog.show();

    }
    private void showCancelledDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.ride_cancel_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                alertDialog.dismiss();
                reset();
                startActivity(new Intent(DriverTrackingActivity.this, DriverActivity.class));
                finish();
            }
        }, 4000);
    }
    private void drawMarker() {
        googleMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions currentMarker = new MarkerOptions();
        currentMarker.position(new LatLng(currentLat, currentLng));
        currentMarker.icon(getMineMarker());
        builder.include(googleMap.addMarker(currentMarker).getPosition());

        MarkerOptions destinationMarker = new MarkerOptions();
        destinationMarker.position(new LatLng(destinationLat, destinationLng));
        destinationMarker.icon(destinationMarkerIcon);
        builder.include(googleMap.addMarker(destinationMarker).getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 40; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        try {
            googleMap.animateCamera(cu);
        } catch (Exception e) {
            e.printStackTrace();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker.getPosition(), 10.0f));
        }
    }

    public BitmapDescriptor getMineMarker() {
        String ambulanceType = sessionManager.getKeyAmbulanceType();
        if (ambulanceType != null) {
            if (ambulanceType.equalsIgnoreCase("ALS")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.marker_als);
            } else if (ambulanceType.equalsIgnoreCase("BLS")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.marker_bls);
            } else if (ambulanceType.equalsIgnoreCase("PATIENT TRANSPORT")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.marker_pt);
            } else {
                return BitmapDescriptorFactory.fromResource(R.drawable.marker_mortuary);
            }
        } else {
            return BitmapDescriptorFactory.fromResource(R.drawable.marker_bls);
        }
    }





    private void showRating() {
        reset();
        btn_done.setVisibility(View.GONE);
        btn_paytm.setVisibility(View.GONE);
        ll_rating.setVisibility(View.VISIBLE);
        /*View view = LayoutInflater.from(this).inflate(R.layout.rating_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(view);

        final RatingBar rb_rating = (RatingBar) view.findViewById(R.id.rb_rating);
        TextView tv_submit = (TextView) view.findViewById(R.id.tv_submit);

        alertDialog = builder.create();
        alertDialog.show();
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = rb_rating.getRating();
                if (rating != 0.0f) {
                    showLoader();
                    mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
                    mBundle.putString(Constants.Extras.RATING, String.valueOf(rating));
                    okHttpAPICalls.run(Constants.RequestTags.RIDE_RATING, mBundle);
                } else {
                    ToastUtils.showLongToast(DriverTrackingActivity.this, getString(R.string.rate_first));
                }
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (whichState == Constants.Extras.BEFORESTARTEDLAYOUT) {
            Log.e(TAG, "beforelayut" );
            sessionManager.setKeyRestore(Constants.Extras.BEFORESTARTEDLAYOUT);
        } else if (whichState == Constants.Extras.STARTEDLAYOUT) {
            Log.e(TAG, "startlayout" );
            sessionManager.setKeyRestore(Constants.Extras.STARTEDLAYOUT);
        } else if (whichState == Constants.Extras.FARELAYOUT) {
            Log.e(TAG, "farelayout" );
            sessionManager.setFareData(data.toString());
            sessionManager.setKeyRestore(Constants.Extras.FARELAYOUT);
        } else if (whichState == Constants.Extras.AFTERFARELAYOUT){
            sessionManager.setKeyRestore(Constants.Extras.AFTERFARELAYOUT);

        }
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        if (registered) {
            unregisterReceiver(receiver);
            registered = false;
        }
        super.onDestroy();
    }

    private void reset() {
        whichState = -1;  //RESET
        sessionManager.setKeyRestore(-1);
    }

    void checkGps() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getString(R.string.enable_gps));
            dialog.setCancelable(false);
            dialog.setPositiveButton(getString(R.string.enable_large), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent, Constants.Splash.RESPONSE_CODE);
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.Splash.RESPONSE_CODE) {
            checkGps();
        }
    }
}

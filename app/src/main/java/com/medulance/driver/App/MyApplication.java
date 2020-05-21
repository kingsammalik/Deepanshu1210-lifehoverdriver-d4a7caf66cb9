package com.medulance.driver.App;

import android.app.Application;
import android.content.Intent;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.medulance.driver.helper.SessionManager;

/**
 * Created by sahil on 13/9/16.
 */
public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication mInstance;
    private SessionManager sessionManager;
    private double lat;
    private double lng;
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        //FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        mInstance = this;
        sessionManager = new SessionManager(this);
        analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker("UA-89386581-2");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        if (sessionManager.getKeyIsLoggedIn()) {
            startService(new Intent(this, SendService.class));
        }
        /*FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:348669827980:android:78e9c02775ee41a3e254a9") // Required for Analytics.
                .setProjectId("cats-e97d6") // Required for Firebase Installations.
                .setApiKey("AIzaSyBrUaRrLRldVL6idcaCNCvzz1dqCpRUj0U") // Required for Auth.
                .build();
        FirebaseApp.initializeApp(this, options, "CATS");*/
    }
    public static GoogleAnalytics analytics() {
        return analytics;
    }
    public static Tracker tracker() {
        return tracker;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public SessionManager getSession()   {
        return sessionManager;
    }

    public double getLat() {
        return lat;
    }

    public void setLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLng() {
        return lng;
    }

    /*public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(10 * 1024 * 1024); // 10 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }*/
}

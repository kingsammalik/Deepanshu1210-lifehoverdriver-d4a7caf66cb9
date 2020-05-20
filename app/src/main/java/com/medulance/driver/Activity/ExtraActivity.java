package com.medulance.driver.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medulance.driver.App.Constants;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.App.SendService;
import com.medulance.driver.Fragment.Account;
import com.medulance.driver.Fragment.Bookings;
import com.medulance.driver.Fragment.CallBase;
import com.medulance.driver.Fragment.FareChart;
import com.medulance.driver.Fragment.MiscFragment;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.DialogUtil;
import com.medulance.driver.Utils.LogUtils;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.DividerItemDecoration;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.madx.updatechecker.lib.UpdateRunnable;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ExtraActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, IOkHttpNotify {

    private static final String TAG = ExtraActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ImageView iv_menu;
    private TextView tv_title;
    private FrameLayout container;
    private NavigationView navigation_view;
    private LinearLayout ll_progress;
    private Bookings bookings;
    private CallBase callBase;
    private Account account;
    private OKHttpAPICalls okHttpAPICalls;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        initalizeViews();
        setOkHttpAPICalls();

        sessionManager = MyApplication.getInstance().getSession();

        setSupportActionBar(this.toolbar);

        NavigationMenuView navMenuView = (NavigationMenuView) navigation_view.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(this));
        navigation_view.setNavigationItemSelectedListener(this);

        View view = navigation_view.getHeaderView(0);
        if (view != null) {
            TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
            TextView tv_mobile = (TextView) view.findViewById(R.id.tv_user_phone);
            TextView tv_ambulance_type = (TextView) view.findViewById(R.id.tv_ambulance_type);
            tv_username.setText(sessionManager.getKeyName());
            tv_mobile.setText(sessionManager.getKeyMobile());
            tv_ambulance_type.setText(sessionManager.getKeyAmbulanceType() + " Ambulance");
        }

        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "adkjadkjda");
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        ll_progress.setOnClickListener(this);

        setFragment(getIntent().getIntExtra(Constants.IntentParameters.WHICH, 1));
    }

    private void initalizeViews() {
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        tv_title = (TextView) findViewById(R.id.tv_title);
        container = (FrameLayout) findViewById(R.id.container);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);

        bookings = new Bookings();
        callBase = new CallBase();
        account = new Account();
    }

    public void showLoader() {
        ll_progress.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        ll_progress.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawer.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goTo(item.getItemId());
            }
        }, 300);
        return false;
    }

    private void goTo(int itemId) {
        switch (itemId) {
            case R.id.nav_home:
                finish();
                break;
            case R.id.nav_bookings:
                setFragment(Constants.NavigationDrawer.BOOKING_HISTORY);
                break;
            case R.id.nav_account:
                setFragment(Constants.NavigationDrawer.ACCOUNT);
                break;
            case R.id.nav_call_base:
                setFragment(Constants.NavigationDrawer.CALL_BASE);
                break;
            case R.id.nav_fare_chart:
                setFragment(Constants.NavigationDrawer.FARE_CHART);
                break;
            case R.id.nav_about:
                setFragment(Constants.NavigationDrawer.ABOUT);
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
                    ToastUtils.showShortToast(ExtraActivity.this, getString(R.string.no_connection));
                }
                break;
            case R.id.nav_update:
                new UpdateRunnable(ExtraActivity.this, new Handler()).force(true).start();
                break;
        }
    }

    private void getLogout() {
        showLoader();
        Intent myService = new Intent(ExtraActivity.this, SendService.class);
        stopService(myService);
        okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGOUT, null);
    }

    private void setFragment(int which) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        switch (which) {
            case Constants.NavigationDrawer.BOOKING_HISTORY:
                tv_title.setText("Bookings");
                localFragmentTransaction.replace(R.id.container, bookings, "bookings");
                localFragmentTransaction.commit();
                break;
            case Constants.NavigationDrawer.ACCOUNT:
                tv_title.setText("Account");
                localFragmentTransaction.replace(R.id.container, account, "account");
                localFragmentTransaction.commit();
                break;
            case Constants.NavigationDrawer.CALL_BASE:
                tv_title.setText("Call Base");
                localFragmentTransaction.replace(R.id.container, callBase, "callbase");
                localFragmentTransaction.commit();
                break;
            case Constants.NavigationDrawer.FARE_CHART:
                tv_title.setText("Fare chart");
                FareChart miscFragment = new FareChart();
                miscFragment.setArguments(bundle);
                localFragmentTransaction.replace(R.id.container, miscFragment, "fare");
                localFragmentTransaction.commit();
                break;
            case Constants.NavigationDrawer.ABOUT:
                tv_title.setText("About");
                bundle.putInt(Constants.IntentParameters.WHICH, 1);
                MiscFragment miscFragment2 = new MiscFragment();
                miscFragment2.setArguments(bundle);
                localFragmentTransaction.replace(R.id.container, miscFragment2, "about");
                localFragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_progress:
                break;
        }
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
                                    stopService(new Intent(ExtraActivity.this, SendService.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ToastUtils.showLongToast(ExtraActivity.this, getString(R.string.success));
                                sessionManager.clear();
                                Intent intent = new Intent(ExtraActivity.this, LoginActivity.class);
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
                                ToastUtils.showLongToast(ExtraActivity.this, message);
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
                            ToastUtils.showLongToast(ExtraActivity.this, getString(R.string.try_again_later));
                            hideLoader();
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
                ToastUtils.showLongToast(ExtraActivity.this, getString(R.string.TimeOutError));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

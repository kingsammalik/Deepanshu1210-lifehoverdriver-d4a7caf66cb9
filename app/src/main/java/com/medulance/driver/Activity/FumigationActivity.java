package com.medulance.driver.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.medulance.driver.App.Constants;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FumigationActivity extends AppCompatActivity implements IOkHttpNotify {

    Button stop_fumigation;
    ProgressDialog progressDialog;
    private Bundle mBundle;
    private SessionManager sessionManager;
    private OKHttpAPICalls okHttpAPICalls;
    private int whichState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fumigation);
        setOkHttpAPICalls();
        mBundle = new Bundle();
        sessionManager = MyApplication.getInstance().getSession();
        stop_fumigation = findViewById(R.id.stop_fumigation);
        stop_fumigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(FumigationActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.show();
                //bookingId=newBookingModel.getBookingId();
                mBundle.putString(Constants.Extras.BOOKINGID, sessionManager.getKeyBookingId());
                mBundle.putString(Constants.Extras.DRIVER_ID, sessionManager.getKeyUserId());
                mBundle.putString(Constants.Extras.STATUS, "4");
                okHttpAPICalls.run(Constants.RequestTags.UPDATE_BOOKING_STATUS,mBundle);
            }
        });
    }

    public void setOkHttpAPICalls() {
        this.okHttpAPICalls = new OKHttpAPICalls();
        this.okHttpAPICalls.setOnOkHttpNotifyListener(this);
    }

    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        String jsonResponse = response.body().string();
        Log.d("FumigationActivity",jsonResponse);
        switch (requestType) {
            case Constants.RequestTags.UPDATE_BOOKING_STATUS:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(FumigationActivity.this, getString(R.string.success));
                                completeride();
                                try {
                                    //hideLoader();

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
                                ToastUtils.showLongToast(FumigationActivity.this, getString(R.string.try_again_later));
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
                            ToastUtils.showLongToast(FumigationActivity.this, getString(R.string.try_again_later));

                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onHttpRequestFailure(String requestType, Request request, String errorMessage) {

    }

    private void completeride(){
        whichState = Constants.Extras.AFTERFARELAYOUT;
        Intent intent = new Intent(FumigationActivity.this,DriverActivity.class);
        startActivity(intent);

    }


}

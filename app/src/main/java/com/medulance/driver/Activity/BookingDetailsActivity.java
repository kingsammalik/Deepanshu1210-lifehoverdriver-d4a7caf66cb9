package com.medulance.driver.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medulance.driver.App.Constants;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.models.NewBookingModel;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class BookingDetailsActivity extends AppCompatActivity implements View.OnClickListener, IOkHttpNotify {

    private TextView tv_dateTime;
    private TextView tv_type;
    private TextView tv_from;
    private TextView tv_to;
    private TextView tv_msg;
    private TextView tv_title;
    private ImageView iv_back;
    private View view_vertical_line;
    private View view_line;
    private LinearLayout ll_progress;

    private OKHttpAPICalls okHttpAPICalls;
    private String driverMobile;
    private String bookingId;
    private Bundle mBundle;
    private String type;
    private String Tag="";
    private String des_lat;
    private String des_lng;
    private SessionManager session;
    private TextView Bookid,tv_call,tv_cancel,fare,book;
    private NewBookingModel newBookingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        mBundle = new Bundle();
        initializeViews();
        setOkHttpAPICalls();

        tv_title.setText(getString(R.string.booking_details));

        bookingId = getIntent().getStringExtra(Constants.IntentParameters.BOOKINGID);
        type = getIntent().getStringExtra(Constants.IntentParameters.TYPE);
        showLoader();
        mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
        okHttpAPICalls.run(Constants.RequestTags.BOOKING_DETAILS, mBundle);
    }

    private void initializeViews() {
        tv_dateTime = (TextView) findViewById(R.id.tv_dateTime);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_from = (TextView) findViewById(R.id.tv_from);
        tv_to = (TextView) findViewById(R.id.tv_to);
        tv_msg = (TextView) findViewById(R.id.tv_message);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_cancel = (TextView) findViewById(R.id.tv_btn_cancel);
        tv_call = (TextView) findViewById(R.id.tv_btn_call);
        Bookid=(TextView)findViewById(R.id.tv_bookid);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        fare=(TextView)findViewById(R.id.tv_fair);
        view_vertical_line = findViewById(R.id.view_vertical_line);
        view_line = findViewById(R.id.view_line);
        book=(TextView)findViewById(R.id.tv_book);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);

        ll_progress.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void rideRunning() {
        tv_call.setVisibility(View.VISIBLE);
        tv_cancel.setVisibility(View.VISIBLE);
        view_vertical_line.setVisibility(View.VISIBLE);
        view_line.setVisibility(View.VISIBLE);
    }

    private void rideStarted() {
        tv_call.setVisibility(View.GONE);
        tv_cancel.setVisibility(View.VISIBLE);
        view_vertical_line.setVisibility(View.GONE);
        view_line.setVisibility(View.VISIBLE);
    }
    private void cancelledRide() {
        tv_cancel.setVisibility(View.GONE);
        tv_call.setVisibility(View.GONE);
        view_vertical_line.setVisibility(View.GONE);
        view_line.setVisibility(View.GONE);
    }

    private void rideStopped() {
        tv_cancel.setVisibility(View.GONE);
        tv_call.setVisibility(View.GONE);
        view_vertical_line.setVisibility(View.GONE);
        view_line.setVisibility(View.GONE);
    }

    public void setOkHttpAPICalls() {
        this.okHttpAPICalls = new OKHttpAPICalls();
        this.okHttpAPICalls.setOnOkHttpNotifyListener(this);
    }

    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        final String responseJson = response.body().string();
        switch (requestType) {
            case Constants.RequestTags.BOOKING_DETAILS:
                try {
                    JSONObject jsonObject = new JSONObject(responseJson);
                    Log.e("Response : ",responseJson);
                    String status = jsonObject.getString("status");
                    Log.e("Status Ride=",status);
                    final JSONObject data = jsonObject.getJSONObject("data");
                    if (status.equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setData(data);
                            }
                        });
                    } else {
                        final String message = jsonObject.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(BookingDetailsActivity.this, message);
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
                            ToastUtils.showLongToast(BookingDetailsActivity.this, getString(R.string.error));
                            hideLoader();
                        }
                    });

                }
                break;
        }
    }

    private void setData(JSONObject data) {
        try {
            driverMobile = data.getString("mobile");
            String dateTime = data.getString("pickup_date");
            String ambulanceType = type;
            String from = "FROM : " + data.getString("pickup_area");
            String to = "TO : " + data.getString("drop_area");
            String status = data.getString("status");
            String bookid = data.getString("unique_id");
            book.setText("Booking Id :");
            Bookid.setText(bookid);
            tv_dateTime.setText(dateTime);
            tv_type.setText(ambulanceType);
            tv_from.setText(from);
            tv_to.setText(to);
            fare.setText("Rs."+data.getString("amount"));
            des_lat=data.getString("des_lat");
            des_lng=data.getString("des_lng");
            if (status.equalsIgnoreCase("-1")) {
                cancelledRide();
            } else if (status.equalsIgnoreCase("3") || status.equalsIgnoreCase("4")) {
                rideStopped();
            } else if (status.equalsIgnoreCase("1")) {
                Tag="started";
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                tv_cancel.setLayoutParams(param);
                 tv_cancel.setText("START RIDE");
                rideStarted();
            }
            else if(status.equalsIgnoreCase("2")){
                Tag="stopped";
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                tv_cancel.setLayoutParams(param);
                tv_cancel.setText("STOP RIDE");
                rideStarted();
            }
            else {
                tv_call.setText("ACCEPT");
                tv_cancel.setText("CANCEL");
                Tag="CANCEL";
                rideRunning();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHttpRequestFailure(String requestType, Request request, String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showLongToast(BookingDetailsActivity.this, getString(R.string.TimeOutError));
                hideLoader();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_call:
                showLoader();
                sendBookingResponse(bookingId, 1);
                break;
            case R.id.tv_btn_cancel:
                if(Tag.equalsIgnoreCase("started")){
                   /*Intent intent=new Intent(BookingDetailsActivity.this,DriverTrackingActivity.class);
                    intent.putExtra("des_Lat",des_lat);
                    intent.putExtra("des_lng",des_lng);
                    intent.putExtra("tag","booking_details");
                    startActivity(intent);*/
                }
                else if(Tag.equalsIgnoreCase("stopped")){
                   //sendBookingResponse(bookingId,3);
                }
                else if(Tag.equalsIgnoreCase("CANCEL")) {
                    showLoader();
                    sendBookingResponse(bookingId, 0);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void showLoader() {
        ll_progress.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        ll_progress.setVisibility(View.GONE);
    }

    private void sendBookingResponse(String bookingId, final int status) {
        showLoader();
        OKHttpAPICalls okHttpAPICalls = new OKHttpAPICalls();
        mBundle.putString(Constants.Extras.BOOKINGID, bookingId);
        mBundle.putString(Constants.Extras.STATUS, String.valueOf(status));
        okHttpAPICalls.run(Constants.RequestTags.BOOKCAB_RESPONSE, mBundle);
        okHttpAPICalls.setOnOkHttpNotifyListener(new IOkHttpNotify() {
            @Override
            public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
                String responseJson = response.body().string();
                switch (requestType) {
                    case Constants.RequestTags.BOOKCAB_RESPONSE:
                        try {
                            JSONObject jsonObject = new JSONObject(responseJson);
                            String statusCode = jsonObject.getString("status");
                            if (statusCode.equalsIgnoreCase("200")) {
                                if (status == 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //hideLoader();
                                            ToastUtils.showLongToast(BookingDetailsActivity.this, getString(R.string.success));
                                            Intent intent = new Intent(BookingDetailsActivity.this, DriverActivity.class);
                                            intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.FROM_DIALOG);
                                            startActivity(intent);
                                            finish();

                                        }
                                    });
                                } else {
                                    //hideLoader();
                                    Intent intent = new Intent(BookingDetailsActivity.this, DriverTrackingActivity.class);
                                    intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.FROM_DIALOG);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                if (statusCode.equalsIgnoreCase("300")){
                                    //hideLoader();
                                    Intent intent = new Intent(BookingDetailsActivity.this, DriverActivity.class);
                                    intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.FROM_DIALOG);
                                    startActivity(intent);
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    final String msg = data.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.showLongToast(BookingDetailsActivity.this, msg);
                                        }
                                    });
                                    finish();
                                } else{
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    final String msg = data.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.showLongToast(BookingDetailsActivity.this, msg);
                                        }
                                    });

                                }
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
                                    ToastUtils.showLongToast(BookingDetailsActivity.this, getString(R.string.TimeOutError));
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
                        ToastUtils.showLongToast(BookingDetailsActivity.this, getString(R.string.TimeOutError));
                    }
                });
            }
        });
    }

}

package com.medulance.driver.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.medulance.driver.App.Constants;
import com.medulance.driver.App.MyApplication;
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

public class DialogActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private Bundle mBundle;
    private SessionManager session;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private String bookingJson;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        session = MyApplication.getInstance().getSession();
        mBundle = new Bundle();

        session.setKeyRestore(-1);

        bookingJson = session.getKeyBookingJson();
        if (bookingJson != null) {
            start();

            checkGps();
        } else {
            finish();
        }
    }

    /*private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            start();
        } else {
            ToastUtils.showLongToast(this, getString(R.string.permission_toast));
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.Splash.PERMISSION_REQUEST_CODE);
        }
    }*/

    private void start() {
        mediaPlayer = MediaPlayer.create(this, R.raw.ambulance);
        mediaPlayer.start();
        NewBookingModel newBookingModel = new Gson().fromJson(bookingJson, NewBookingModel.class);
        bookingId = newBookingModel.getBookingId();
        handler.postDelayed(runnable, 30000);
        showDialog(newBookingModel.getBookingId(), newBookingModel.getName(), newBookingModel.getCurrent(), newBookingModel.getDes(), newBookingModel.getBase_price(), newBookingModel.getBase_km(), newBookingModel.getPrice_per_km(), newBookingModel.getBooking_type());
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (bookingId != null) {
                sendBookingResponse(bookingId, 0);
            }
        }
    };

    private void showDialog(final String bookingId, String username, String pickup_location, String drop_location, String base_price, String base_km, String price_per_km, String booking_type) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_new_booking_layout, null);
        TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
        TextView tv_pickup_location = (TextView) view.findViewById(R.id.tv_pickup_location);
        TextView tv_drop_location = (TextView) view.findViewById(R.id.tv_drop_location);
        LinearLayout ll_accept = (LinearLayout) view.findViewById(R.id.ll_accept);
        LinearLayout ll_cancel = (LinearLayout) view.findViewById(R.id.ll_cancel);
        TextView tv_price = (TextView) view.findViewById(R.id.tv_price);

        tv_username.setText(username);
        tv_pickup_location.setText(Html.fromHtml("<b>From: </b>" + pickup_location));
        tv_drop_location.setText(Html.fromHtml("<b>To: </b>" + drop_location));

        String price_text = "";
        if (base_price != null && base_price.trim().length() > 0) {
            price_text = price_text + "Base Price : " + base_price + "\n";
        }
        if (base_km != null && base_km.trim().length() > 0) {
            price_text = price_text + "Base Km : " + base_km + "\n";
        }
        if (price_per_km != null && price_per_km.trim().length() > 0) {
            price_text = price_text + "Price Per Km : " + price_per_km + "\n";
        }
        if (booking_type != null && booking_type.trim().length() > 0) {
            price_text = price_text + "\nBy : " + booking_type;
        }

        if (price_text.trim().length() > 0) {
            tv_price.setText(price_text);
        } else {
            tv_price.setVisibility(View.GONE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(view);

        alertDialog = builder.create();

        ll_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBookingResponse(bookingId, 1);
                try {
                    handler.removeCallbacks(runnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBookingResponse(bookingId, 0);
                try {
                    handler.removeCallbacks(runnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialog.show();
    }


    private void sendBookingResponse(String bookingId, final int status) {
        cancelNotification();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
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
                                            progressDialog.cancel();
                                            ToastUtils.showLongToast(DialogActivity.this, getString(R.string.success));
                                            session.setKeyBookingJson(null);
                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                            progressDialog.cancel();
                                            Intent intent = new Intent(DialogActivity.this, DriverTrackingActivity.class);
                                            intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.FROM_DIALOG);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                }
                            } else {
                                if (statusCode.equalsIgnoreCase("300")){
                                    alertDialog.dismiss();
                                    Intent intent = new Intent(DialogActivity.this, DriverActivity.class);
                                    intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.FROM_DIALOG);
                                    startActivity(intent);

                                    JSONObject data = jsonObject.getJSONObject("data");
                                    final String msg = data.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.showLongToast(DialogActivity.this, msg);
                                        }
                                    });
                                    finish();
                                } else{
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    final String msg = data.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.showLongToast(DialogActivity.this, msg);
                                        }
                                    });

                                }

                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                                    progressDialog.cancel();
                                    alertDialog.cancel();
                                    Intent intent = new Intent(DialogActivity.this, DriverActivity.class);
                                    intent.putExtra(Constants.IntentParameters.TYPE, Constants.Extras.FROM_DIALOG);
                                    startActivity(intent);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showLongToast(DialogActivity.this, "Out of India");
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
                        progressDialog.cancel();
                        ToastUtils.showLongToast(DialogActivity.this, getString(R.string.TimeOutError));
                    }
                });
            }
        });
    }

    private void cancelNotification() {
        try {
            mediaPlayer.stop();
            /*NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            mediaPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.Splash.PERMISSION_REQUEST_CODE) {
            //checkPermission();
        }
    }

    void checkGps() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // notify user
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
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

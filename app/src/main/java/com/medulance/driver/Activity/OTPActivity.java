package com.medulance.driver.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.provider.Settings;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.medulance.driver.App.Constants;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.App.SendService;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.LogUtils;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OTPActivity extends AppCompatActivity implements IOkHttpNotify {

    private TextInputLayout input_layout_otp;
    private EditText et_otp;
    private TextView tv_resend_otp, tv_go_back;
    private ImageView iv_submit;
    private SmsReceiver smsReceiver;
    private SessionManager sessionManager;
    private String TAG = OTPActivity.class.getSimpleName();
    private Bundle mBundle;
    private Bundle dataBundle;
    private boolean fromType;
    private OKHttpAPICalls okHttpAPICalls;
    private ProgressDialog progressDialog;
    private String gcmToken = null;
    private String OTP = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mBundle = new Bundle();

        sessionManager = MyApplication.getInstance().getSession();

        smsReceiver = new SmsReceiver();

        initializeView();

        initiateOkHttp();

        dataBundle = getIntent().getBundleExtra(Constants.IntentParameters.DATA);
        OTP = dataBundle.getString(Constants.IntentParameters.OTP);

        LogUtils.d("OTP", OTP);
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
       /* new GetGCMToken(this, new GetGCMTokenInterface() {
            @Override
            public void getResult(String result) {
                gcmToken = result;
            }
        }).execute();*/

        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                mBundle.putString(Constants.Extras.MOBILE, dataBundle.getString(Constants.IntentParameters.MOBILE));
                okHttpAPICalls.run(Constants.RequestTags.RESEND_OTP_REQUEST, mBundle);
            }
        });

        tv_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entered_otp = et_otp.getText().toString().trim();
                if (entered_otp.length() > 0) {
                    input_layout_otp.setErrorEnabled(false);
                    if (OTP.equalsIgnoreCase(entered_otp)) {
                        progressDialog.show();
                        mBundle.putString(Constants.Extras.DRIVER, dataBundle.getString(Constants.IntentParameters.ID));
                        if (gcmToken != null) {
                            mBundle.putString(Constants.Extras.DEVICE_ID, gcmToken);
                            okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGIN, mBundle);
                        } else {
                           /* new GetGCMToken(OTPActivity.this, new GetGCMTokenInterface() {
                                @Override
                                public void getResult(String result) {
                                    gcmToken = result;
                                    mBundle.putString(Constants.Extras.DEVICE_ID, result);
                                    okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGIN, mBundle);
                                }
                            }).execute();*/
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
                            mBundle.putString(Constants.Extras.DEVICE_ID, gcmToken);
                            okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGIN, mBundle);
                        }
                    } else {
                        ToastUtils.showShortToast(OTPActivity.this, getString(R.string.invalid_otp));
                    }
                } else {
                    input_layout_otp.setErrorEnabled(true);
                    input_layout_otp.setError(getString(R.string.please_enter_otp));
                }
            }
        });

        this.registerReceiver();
    }

    private void initiateOkHttp() {
        this.okHttpAPICalls = new OKHttpAPICalls();
        this.okHttpAPICalls.setOnOkHttpNotifyListener(this);
    }

    private void initializeView() {
        input_layout_otp = (TextInputLayout) findViewById(R.id.input_layout_otp);
        et_otp = (EditText) findViewById(R.id.et_otp);
        tv_resend_otp = (TextView) findViewById(R.id.tv_resend_otp);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        tv_go_back = (TextView) findViewById(R.id.tv_go_back);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
    }

    public class SmsReceiver extends BroadcastReceiver {
        private static final String SMS_EXTRA_NAME = "pdus";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);
                if (smsExtra != null) {
                    for (int i = 0; i < smsExtra.length; ++i) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                        String body = sms.getMessageBody() != null ? sms.getMessageBody() : "";
                        String address = sms.getOriginatingAddress() != null ? sms.getOriginatingAddress() : "";
                        Log.e(TAG, "Received SMS: " + body + ", Sender: " + address);

                        if (address.equalsIgnoreCase(Constants.SMS_ORIGIN)) {
                            String otp = split(body);
                            et_otp.setText(otp);
                            String entered_otp = et_otp.getText().toString().trim();
                            if (entered_otp.length() > 0) {
                                input_layout_otp.setErrorEnabled(false);
                                if (OTP.equalsIgnoreCase(entered_otp)) {
                                    progressDialog.show();
                                    mBundle.putString(Constants.Extras.DRIVER, dataBundle.getString(Constants.IntentParameters.ID));
                                    if (gcmToken != null) {
                                        mBundle.putString(Constants.Extras.DEVICE_ID, gcmToken);
                                        okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGIN, mBundle);
                                    } else {
                                       /* new GetGCMToken(OTPActivity.this, new GetGCMTokenInterface() {
                                            @Override
                                            public void getResult(String result) {
                                                gcmToken = result;
                                                mBundle.putString(Constants.Extras.DEVICE_ID, result);
                                                okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGIN, mBundle);
                                            }
                                        }).execute();*/
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
                                        mBundle.putString(Constants.Extras.DEVICE_ID, gcmToken);
                                        okHttpAPICalls.run(Constants.RequestTags.DRIVER_LOGIN, mBundle);
                                    }
                                } else {
                                    ToastUtils.showShortToast(OTPActivity.this, getString(R.string.invalid_otp));
                                }
                            } else {
                                input_layout_otp.setErrorEnabled(true);
                                input_layout_otp.setError(getString(R.string.please_enter_otp));
                            }
                        }
                    }
                }
            }
        }
    }

    private String split(String msg) {
        String otp = null;
        int i =msg.indexOf(Constants.OTP_DELIMITER);
        if ( i != -1){
            int start = i + 2;
            int length = 4;
            otp = msg.substring(start, start+length);
            Log.e("Split OTP",otp);
            return otp;
        }
        return otp;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(smsReceiver);
        super.onDestroy();
    }

    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        String jsonResponse = response.body().string();
        switch (requestType) {
            case Constants.RequestTags.DRIVER_LOGIN:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (status.equalsIgnoreCase("200")) {
                        sessionManager.setKeyIsLoggedIn(true);
                        sessionManager.setKeyUserId(data.getString("driver_id"));
                        sessionManager.setKeyName(data.getString("name"));
                        sessionManager.setKeyMobile(data.getString("phone"));
                        sessionManager.setKeyAmbulanceType(data.getString("ambulance_type"));
                        sessionManager.setKeyLoginResponse(jsonResponse);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startService(new Intent(OTPActivity.this, SendService.class));
                                ToastUtils.showLongToast(OTPActivity.this, getString(R.string.success));
                                checkGps();
                            }
                        });
                    } else {
                        final String msg = data.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(OTPActivity.this, msg);
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing()) {
                                progressDialog.cancel();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLongToast(OTPActivity.this, getString(R.string.try_again_later));
                            if (progressDialog.isShowing()) {
                                progressDialog.cancel();
                            }
                        }
                    });
                }
                break;
            case Constants.RequestTags.RESEND_OTP_REQUEST:
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String status = jsonObject.getString("status");
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (status.equalsIgnoreCase("200")) {
                        String OTP = data.getString("OTP");
                        OTPActivity.this.OTP = OTP;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                OTPActivity.this.registerReceiver();
                                ToastUtils.showLongToast(OTPActivity.this, getString(R.string.otp_soon));
                            }
                        });
                    } else {
                        final String message = data.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(OTPActivity.this, message);
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing()) {
                                progressDialog.cancel();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing()) {
                                progressDialog.cancel();
                            }
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
                if (progressDialog.isShowing()) {
                    progressDialog.cancel();
                }
                ToastUtils.showLongToast(OTPActivity.this, getString(R.string.TimeOutError));
            }
        });
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
            dialog.setNegativeButton(getString(R.string.close_large), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        } else {
            Intent intent = new Intent(OTPActivity.this, DriverActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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

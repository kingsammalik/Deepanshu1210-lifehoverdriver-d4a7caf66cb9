package com.medulance.driver.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.medulance.driver.App.Constants;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.madx.updatechecker.lib.UpdateRunnable;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements IOkHttpNotify {

    private TextInputLayout input_layout_mobile;
    private EditText et_mobile;
    private Button btn_sumbit;
    private TextView tv_forgot_password;
    private OKHttpAPICalls okHttpCalls;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private String gcmToken;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new UpdateRunnable(this, new Handler()).start();
        mBundle = new Bundle();

        initalizeLayouts();

        initiateOkHttp();

        sessionManager = MyApplication.getInstance().getSession();
        //gcmToken= FirebaseInstanceId.getInstance().getToken();
        /*new GetGCMToken(this, new GetGCMTokenInterface() {
            @Override
            public void getResult(String result) {
                gcmToken = result;
            }
        }).execute();*/

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    private void initiateOkHttp() {
        this.okHttpCalls = new OKHttpAPICalls();
        this.okHttpCalls.setOnOkHttpNotifyListener(this);
    }

    private void initalizeLayouts() {
        input_layout_mobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        btn_sumbit = (Button) findViewById(R.id.btn_submit);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                String mobile = et_mobile.getText().toString().trim();
                if (mobile.length() > 0) {
                    progressDialog.show();
                    input_layout_mobile.setErrorEnabled(false);
                    input_layout_mobile.setError(null);

                    mBundle.putString(Constants.Extras.MOBILE, mobile);
                    okHttpCalls.run(Constants.RequestTags.CHECK_DRIVER, mBundle);

                } else {
                    input_layout_mobile.setErrorEnabled(true);
                    input_layout_mobile.setError(getString(R.string.enter_mobile));
                }
                break;
        }
    }

    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        String responseJson = response.body().string();
        switch (requestType) {
            case Constants.RequestTags.CHECK_DRIVER:
                try {
                    JSONObject jsonObject = new JSONObject(responseJson);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (status.equalsIgnoreCase("200")) {
                        final Bundle bundle = new Bundle();
                        bundle.putString(Constants.IntentParameters.ID, data.getString("id"));
                        bundle.putString(Constants.IntentParameters.OTP, data.getString("OTP"));
                        Log.e("Otp Lelo",data.getString("OTP"));
                        bundle.putString(Constants.IntentParameters.MOBILE, data.getString("phone"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(LoginActivity.this, getString(R.string.otp_soon));
                                Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                                intent.putExtra(Constants.IntentParameters.DATA, bundle);
                                startActivity(intent);
                            }
                        });
                    } else {
                        final String msg = data.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(LoginActivity.this, msg);
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
                            ToastUtils.showLongToast(LoginActivity.this, getString(R.string.try_again_later));
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
                ToastUtils.showLongToast(LoginActivity.this, getString(R.string.TimeOutError));
            }
        });
    }
}

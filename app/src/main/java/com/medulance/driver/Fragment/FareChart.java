package com.medulance.driver.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.medulance.driver.Activity.ExtraActivity;
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

/**
 * Created by LifeHover on 12/15/2016.
 */

public class FareChart extends BaseFragment implements IOkHttpNotify {


    private ExtraActivity mActivity;
    private OKHttpAPICalls okHttpAPICalls;
    private SessionManager sessionManager;
    private TextView ambulance,base_price,base_km,per_km;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ExtraActivity) context;
    }

    @Override
    protected Activity getFragmentActivity() {
        return mActivity;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        initializeLayout();

        setOkHttpAPICalls();
        ((ExtraActivity) getFragmentActivity()).showLoader();
        okHttpAPICalls.run(Constants.RequestTags.GET_FARE,null);

    }

    private void setOkHttpAPICalls() {
        this.okHttpAPICalls = new OKHttpAPICalls();
        this.okHttpAPICalls.setOnOkHttpNotifyListener(this);
    }

    private void initializeLayout() {
        sessionManager = MyApplication.getInstance().getSession();
        ambulance=(TextView)getFragmentActivity().findViewById(R.id.tv_table_name);
        base_price=(TextView)getFragmentActivity().findViewById(R.id.tv_base_price);
        base_km=(TextView)getFragmentActivity().findViewById(R.id.tv_base_km);
        per_km=(TextView)getFragmentActivity().findViewById(R.id.tv_per_km);
        ambulance.setText(sessionManager.getKeyAmbulanceType());

    }

    @Override
    protected int initializeLayoutId() {
        return R.layout.fragment_fare_chart;
    }



    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        String responseJson = response.body().string();
        switch (requestType) {
            case Constants.RequestTags.GET_FARE:
                try {
                    JSONObject jsonObject = new JSONObject(responseJson);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    final JSONObject data = jsonObject.getJSONObject("data");
                    if (status.equalsIgnoreCase("200")) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    base_price.setText(data.getString("base_price"));
                                    base_km.setText(data.getString("base_km"));
                                    per_km.setText(data.getString("price_per_km"));
                                    ((ExtraActivity) getFragmentActivity()).hideLoader();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } else {
                        final String msg = data.getString("message");
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ExtraActivity) getFragmentActivity()).hideLoader();
                            }
                        });
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ExtraActivity) getFragmentActivity()).hideLoader();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ExtraActivity) getFragmentActivity()).hideLoader();
                            ToastUtils.showLongToast(getFragmentActivity(), getString(R.string.try_again_later));

                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onHttpRequestFailure(String requestType, Request request, String errorMessage) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ExtraActivity) getFragmentActivity()).hideLoader();
                ToastUtils.showLongToast(getFragmentActivity(), getString(R.string.TimeOutError));
            }
        });
    }
}

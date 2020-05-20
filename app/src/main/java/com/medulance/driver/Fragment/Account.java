package com.medulance.driver.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.medulance.driver.Activity.ExtraActivity;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.R;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.models.LoginModel;

/**
 * Created by sahil on 17/9/16.
 */
public class Account extends BaseFragment {

    private ExtraActivity mActivity;
    private EditText et_mobile;
    private EditText et_name;
    private TextView tv_ambulance_service_provider;
    private TextView tv_ambulance_type;
    private SessionManager sessionManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ExtraActivity) context;
    }

    @Override
    protected ExtraActivity getFragmentActivity() {
        return mActivity;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        sessionManager = MyApplication.getInstance().getSession();
        initializeLayouts();

        et_mobile.setFocusable(false);
        et_name.setFocusable(false);

        et_mobile.setText(sessionManager.getKeyMobile());
        et_name.setText(sessionManager.getKeyName());

        tv_ambulance_service_provider.setText(new Gson().fromJson(sessionManager.getKeyLoginResponse(), LoginModel.class).getData().getManager_name());
        tv_ambulance_type.setText(sessionManager.getKeyAmbulanceType());
    }

    private void initializeLayouts() {
        et_mobile = (EditText) getFragmentActivity().findViewById(R.id.et_mobile);
        et_name = (EditText) getFragmentActivity().findViewById(R.id.et_name);
        tv_ambulance_service_provider = (TextView) getFragmentActivity().findViewById(R.id.tv_ambulance_service_provider);
        tv_ambulance_type = (TextView) getFragmentActivity().findViewById(R.id.tv_ambulance_type);
    }

    @Override
    protected int initializeLayoutId() {
        return R.layout.fragment_account;
    }
}

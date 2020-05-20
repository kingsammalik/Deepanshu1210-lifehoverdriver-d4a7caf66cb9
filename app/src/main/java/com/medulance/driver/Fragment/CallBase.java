package com.medulance.driver.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.R;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.models.LoginModel;

/**
 * Created by sahil on 16/9/16.
 */
public class CallBase extends BaseFragment implements View.OnClickListener {

    SessionManager sessionManager;
    private Button call_base;
    private Activity mActivity;

    @Override
    protected Activity getFragmentActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity=(Activity)context;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        sessionManager = MyApplication.getInstance().getSession();
        call_base = (Button) getFragmentActivity().findViewById(R.id.call_base);
        call_base.setOnClickListener(this);
    }

    @Override
    protected int initializeLayoutId() {
        return R.layout.fragment_call_base;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call_base:
                String base_num = new Gson().fromJson(sessionManager.getKeyLoginResponse(), LoginModel.class).getData().getManager_phone();
                if (base_num != null) {
                    Intent intent = new Intent("android.intent.action.CALL");
                    intent.setData(Uri.parse("tel:" + base_num));
                    if (ActivityCompat.checkSelfPermission(getFragmentActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ToastUtils.showLongToast(getFragmentActivity(), getString(R.string.give_permission));
                        return;
                    }
                    startActivity(intent);
                }
                break;
        }
    }
}

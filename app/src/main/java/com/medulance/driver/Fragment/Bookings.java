package com.medulance.driver.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.medulance.driver.Activity.BookingDetailsActivity;
import com.medulance.driver.Activity.ExtraActivity;
import com.medulance.driver.Adapters.BookingHistoryAdapter;
import com.medulance.driver.App.Constants;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.Interfaces.BookingHistoryInterface;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.R;
import com.medulance.driver.Utils.ToastUtils;
import com.medulance.driver.helper.SessionManager;
import com.medulance.driver.models.BookingModel;
import com.medulance.driver.okhttp.OKHttpAPICalls;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil on 17/9/16.
 */
public class Bookings extends BaseFragment implements IOkHttpNotify {

    List<BookingModel.Data> list = new ArrayList<>();
    private ExtraActivity mActivity;
    private RecyclerView booking_recycler;
    private OKHttpAPICalls okHttpAPICalls;
    private BookingHistoryAdapter bookingHistoryAdapter;
    private TextView tv_record;
    private SessionManager sessionManager;

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
        okHttpAPICalls.run(Constants.RequestTags.DRIVER_BOOKINGS, null);
    }

    private void setOkHttpAPICalls() {
        this.okHttpAPICalls = new OKHttpAPICalls();
        this.okHttpAPICalls.setOnOkHttpNotifyListener(this);
    }

    private void initializeLayout() {
        sessionManager = MyApplication.getInstance().getSession();
        booking_recycler = (RecyclerView) getFragmentActivity().findViewById(R.id.booking_recycler);
        tv_record=(TextView)getFragmentActivity().findViewById(R.id.tv_record);
    }

    @Override
    protected int initializeLayoutId() {
        return R.layout.fragment_bookings;
    }

    private void setUprecyclerView() {
        booking_recycler.setLayoutManager(new LinearLayoutManager(getFragmentActivity(), LinearLayoutManager.VERTICAL, false));
        booking_recycler.setHasFixedSize(true);
        bookingHistoryAdapter = new BookingHistoryAdapter(getFragmentActivity(), list);
        booking_recycler.setAdapter(bookingHistoryAdapter);
        bookingHistoryAdapter.setOnClickItem(new BookingHistoryInterface() {
            @Override
            public void onClick(int pos) {
                BookingModel.Data data = list.get(pos);
                Intent i = new Intent(getActivity(), BookingDetailsActivity.class);
                i.putExtra(Constants.IntentParameters.BOOKINGID, data.getId());
                sessionManager.setKeyBookingId(data.getId());
                i.putExtra(Constants.IntentParameters.TYPE, data.getAmbulance_type());
                startActivity(i);
            }
        });
    }

    @Override
    public void onHttpRequestSuccess(String requestType, Response response) throws IOException {
        String jsonresponse = response.body().string();
        switch (requestType) {
            case Constants.RequestTags.DRIVER_BOOKINGS:
                Gson gson = new Gson();
                final BookingModel bookingModel;
                try {
                    bookingModel = gson.fromJson(jsonresponse, BookingModel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ExtraActivity) getFragmentActivity()).hideLoader();
                            ToastUtils.showLongToast(getFragmentActivity(), getString(R.string.TimeOutError));
                        }
                    });
                    return;
                }
                if (bookingModel != null) {
                    if (bookingModel.getStatus().equalsIgnoreCase("200")) {
                        if (bookingModel.getData() != null) {
                            if (bookingModel.getData().size() != 0) {
                                list.clear();
                                list.addAll(bookingModel.getData());
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setUprecyclerView();
                                    }
                                });
                            } else {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_record.setVisibility(View.VISIBLE);
                                        tv_record.setText(R.string.no_booking);
                                        //ToastUtils.showLongToast(getFragmentActivity(), getString(R.string.no_booking));
                                    }
                                });
                            }
                        }
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showLongToast(getFragmentActivity(), bookingModel.getMessage());
                            }
                        });
                    }
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ExtraActivity) getFragmentActivity()).hideLoader();
                    }
                });
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

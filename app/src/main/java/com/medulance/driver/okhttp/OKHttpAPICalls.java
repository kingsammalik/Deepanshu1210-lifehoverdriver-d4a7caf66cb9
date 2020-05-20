package com.medulance.driver.okhttp;

import android.os.Bundle;

import com.medulance.driver.App.Constants;
import com.medulance.driver.App.MyApplication;
import com.medulance.driver.Interfaces.IOkHttpNotify;
import com.medulance.driver.Utils.LogUtils;
import com.medulance.driver.helper.SessionManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OKHttpAPICalls {

    private final String TAG = getClass().getName();
    private OkHttpClient mClient;
    private IOkHttpNotify mIOkHttpNotify;
    private String mUrl;
    private FormEncodingBuilder mFormEncodingBuilder;
    private MultipartBuilder mMultipartBuilder;
    private RequestBody mRequestBody;
    private SessionManager sessionManager;

    public OKHttpAPICalls() {
        sessionManager = MyApplication.getInstance().getSession();
        setOkHttpClient();
    }

    /**
     * Set Ok http client
     */
    private void setOkHttpClient() {
        mClient = new OkHttpClient();
        mClient.setConnectTimeout(Constants.OKHTTP.CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        mClient.setReadTimeout(Constants.OKHTTP.READ_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * Set Ok http notify click listener
     *
     * @param iOkHttpNotify OkHttpNotify Listsner
     */
    public void setOnOkHttpNotifyListener(IOkHttpNotify iOkHttpNotify) {
        this.mIOkHttpNotify = iOkHttpNotify;
    }


    public void run(String requestType, Bundle bundle) {
        switch (requestType) {
            case Constants.RequestTags.CHECK_DRIVER:
                mUrl = Constants.Urls.URL_CHECK_DRIVER;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("mobile", bundle.getString(Constants.Extras.MOBILE));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.DRIVER_LOGIN:
                mUrl = Constants.Urls.URL_DRIVER_LOGIN;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("device_id", "");
                mFormEncodingBuilder.add("driver_id", bundle.getString(Constants.Extras.DRIVER));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.RESEND_OTP_REQUEST:
                mUrl = Constants.Urls.URL_RESEND_OTP;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("mobile", bundle.getString(Constants.Extras.MOBILE));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.SEND_LOCATION:
                mUrl = Constants.Urls.URL_SEND_LOCATION;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("slat", String.valueOf(bundle.getDouble(Constants.Extras.LATITUDE)));
                mFormEncodingBuilder.add("slng", String.valueOf(bundle.getDouble(Constants.Extras.LONGITUDE)));
                mFormEncodingBuilder.add("driver_id", bundle.getString(Constants.Extras.DRIVER));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.DRIVER_BOOKINGS:
                mUrl = Constants.Urls.URL_DRIVER_BOOKINGS;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.BOOKCAB_RESPONSE:
                mUrl = Constants.Urls.URL_BOOKING_RESPONSE;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driverId", sessionManager.getKeyUserId());
                mFormEncodingBuilder.add("bookingId", bundle.getString(Constants.Extras.BOOKINGID));
                mFormEncodingBuilder.add("status", bundle.getString(Constants.Extras.STATUS));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.START_RIDE:
                mUrl = Constants.Urls.URL_START_RIDE;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("slat", bundle.getString(Constants.Extras.LATITUDE));
                mFormEncodingBuilder.add("slng", bundle.getString(Constants.Extras.LONGITUDE));
                mFormEncodingBuilder.add("booking_id", bundle.getString(Constants.Extras.BOOKINGID));
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.STOP_RIDE:
                mUrl = Constants.Urls.URL_STOP_RIDE;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("slat", bundle.getString(Constants.Extras.LATITUDE));
                mFormEncodingBuilder.add("slng", bundle.getString(Constants.Extras.LONGITUDE));
                mFormEncodingBuilder.add("booking_id", bundle.getString(Constants.Extras.BOOKINGID));
                mFormEncodingBuilder.add("waiting_time", bundle.getString(Constants.Extras.WAITING_TIME));
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.DRIVER_LOGOUT:
                mUrl = Constants.Urls.URL_DRIVER_LOGOUT;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.DRIVER_STATUS:
                mUrl = Constants.Urls.URL_DRIVER_STATUS;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("status", bundle.getString(Constants.Extras.STATUS));
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.PAYMENT_TYPE:
                mUrl = Constants.Urls.URL_PAYMENT_TYPE;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("booking_id", bundle.getString(Constants.Extras.BOOKINGID));
                mFormEncodingBuilder.add("payment_type", bundle.getString(Constants.Extras.PAYMENT_TYPE));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.RIDE_RATING:
                mUrl = Constants.Urls.URL_RIDE_RATING;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("booking_id", bundle.getString(Constants.Extras.BOOKINGID));
                mFormEncodingBuilder.add("rating", bundle.getString(Constants.Extras.RATING));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPost(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.BOOKING_DETAILS:
                mUrl = Constants.Urls.URL_BOOKING_DETAILS;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("booking_id", bundle.getString(Constants.Extras.BOOKINGID));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPostWithToken(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.RESTORE_STATE:
                mUrl = Constants.Urls.URL_STATE_STORE;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPostWithToken(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.UPDATE_TOKEN:
                mUrl=Constants.Urls.URL_UPDATE_TOKEN;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mFormEncodingBuilder.add("device_token", "abcd");
                mFormEncodingBuilder.add("version", bundle.getString(Constants.Extras.VERSION_NAME));
                mFormEncodingBuilder.add("device_type", "A");
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPostWithToken(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.CONFIRM_PAYMENT:
                mUrl=Constants.Urls.URL_CONFIRM_PAYMENT;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mFormEncodingBuilder.add("booking_id", bundle.getString(Constants.Extras.BOOKINGID));
                mFormEncodingBuilder.add("payment_type", bundle.getString(Constants.Extras.CASH));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPostWithToken(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.GET_FARE:
                mUrl=Constants.Urls.URL_GET_FARE;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPostWithToken(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.DRIVER_ARRIVE:
                mUrl=Constants.Urls.URL_DRIVER_ARRIVED;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mFormEncodingBuilder.add("booking_id", bundle.getString(Constants.Extras.BOOKINGID));
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPostWithToken(requestType, mUrl, mRequestBody);
                break;
            case Constants.RequestTags.UPDATE_BOOKING_STATUS:
                mUrl=Constants.Urls.URL_UPDTATE_BOOKING_STATUS;
                mFormEncodingBuilder = new FormEncodingBuilder();
                mFormEncodingBuilder.add("driver_id", sessionManager.getKeyUserId());
                mFormEncodingBuilder.add("booking_id", bundle.getString(Constants.Extras.BOOKINGID));
                mFormEncodingBuilder.add("status", bundle.getString(Constants.Extras.STATUS) );
                mRequestBody = mFormEncodingBuilder.build();
                LogUtils.d(Constants.Extras.API_LOG, mRequestBody.toString());
                doOkHttpPostWithToken(requestType, mUrl, mRequestBody);
                break;


        }
    }

    /**
     * Do Http post call
     *
     * @param requestType request type of
     * @param url         url, where request needs to be send
     * @param requestBody request body
     */
    private void doOkHttpPost(final String requestType, String url, RequestBody requestBody) {
        //> http request
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtils.e(TAG, requestType + " Error : " + e.getMessage());
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestFailure(requestType, request, e.getMessage());
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                LogUtils.e(TAG, requestType + " Response : " + response);
                //LogUtils.d(TAG, requestType + " Response : " + response.body().string());
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestSuccess(requestType, response);
                }
            }
        });
    }

    private void doOkHttpPostWithToken(final String requestType, String url, RequestBody requestBody) {
        //> http request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer {" + sessionManager.getKeyAuth() + "}")
                .post(requestBody)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtils.e(TAG, requestType + " Error : " + e.getMessage());
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestFailure(requestType, request, e.getMessage());
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                LogUtils.e(TAG, requestType + " Response : " + response);
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestSuccess(requestType, response);
                }
            }
        });
    }

    /**
     * do http delete with token
     *
     * @param requestType request type
     * @param url         url
     * @param requestBody request body
     */
    private void doOkHttpDeleteWithToken(final String requestType, String url, RequestBody requestBody) {
        //> http request
        Request request = new Request.Builder()
                .url(url)
                //.addHeader("Authorization", mPrefs.getAuthorizationToken())
                .delete(requestBody)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtils.e(TAG, requestType + " Error : " + e.getMessage());
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestFailure(requestType, request, e.getMessage());
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                LogUtils.e(TAG, requestType + " Response : " + response);
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestSuccess(requestType, response);
                }
            }
        });
    }


    /**
     * Do Http get call
     *
     * @param requestType request type of
     * @param url         url, where request needs to be send
     *                    body
     */
    private void doOkHttpGet(final String requestType, String url) {
        //> http request
        Request request = new Request.Builder()
                .url(url)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtils.e(TAG, requestType + " Error : " + e.getMessage());
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestFailure(requestType, request, e.getMessage());
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                LogUtils.e(TAG, requestType + " Response : " + response);
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestSuccess(requestType, response);
                }
            }
        });
    }

    /**
     * Do Http get call
     *
     * @param requestType request type of
     * @param url         url, where request needs to be send
     *                    body
     */
    private void doOkHttpGetWithToken(final String requestType, String url) {
        //> http request
        Request request = new Request.Builder()
                .url(url)
                //.addHeader("Authorization", mPrefs.getAuthorizationToken())
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtils.e(TAG, requestType + " Error : " + e.getMessage());
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestFailure(requestType, request, e.getMessage());
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                LogUtils.e(TAG, requestType + " Response : " + response);
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestSuccess(requestType, response);
                }
            }
        });
    }


    /**
     * Do Http get call
     *
     * @param requestType request type of
     * @param url         url, where request needs to be send
     *                    body
     */
    private void doOkHttpPutWithToken(final String requestType, String url, RequestBody requestBody) {
        //> http PUT request
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                //.addHeader("Authorization", mPrefs.getAuthorizationToken())
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtils.e(TAG, requestType + " Error : " + e.getMessage());
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestFailure(requestType, request, e.getMessage());
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                LogUtils.e(TAG, requestType + " Response : " + response);
                if (mIOkHttpNotify != null) {
                    mIOkHttpNotify.onHttpRequestSuccess(requestType, response);
                }
            }
        });
    }

    private String getAmbulance(String type) {
        switch (type) {
            case "1":
                return "ALS";
            case "2":
                return "BLS";
            case "3":
                return "PATIENT TRANSPORT";
            case "4":
                return "MORTUARY";
            default:
                return "ALS";
        }
    }


}

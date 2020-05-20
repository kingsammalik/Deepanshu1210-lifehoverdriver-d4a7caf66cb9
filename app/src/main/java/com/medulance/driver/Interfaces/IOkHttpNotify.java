package com.medulance.driver.Interfaces;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by sahil on 13/9/16.
 */
public interface IOkHttpNotify {
    void onHttpRequestSuccess(String requestType, Response response) throws IOException;
    void onHttpRequestFailure(String requestType, Request request, String errorMessage);
}

package com.medulance.driver.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sahil on 13/9/16.
 */
public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();
    int PRIVATE_MODE = 0;
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    private static final String PREF_NAME = "LifeHoverDriver";

    private static final String BOOKING_ID="booking_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged";
    private static final String KEY_AUTH = "auth_key";
    private static final String KEY_AMBULANCE_NO = "ambulances_no_key";
    private static final String KEY_AMBULANCE_ID = "ambulance_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_BASE_NUMBER = "base_num";
    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_BOOKING_JSON = "booking_json";
    private static final String KEY_LOGIN_RESPONSE = "login_response";
    private static final String KEY_AMBULANCE_TYPE = "ambulance_type";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_RESTORE = "restore";
    private static final String KEY_FARE_DATA = "fare_data";
    private static final String KEY_BOOKING_STATUS = "booking_status";
    private static final String RESTORE_DRIVER_STATUS = "restore_status";
    private static final String KEY_WAITING_TIME="key_wiating_time";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public void setKeyIsLoggedIn(boolean flag) {
        editor.putBoolean(KEY_IS_LOGGED_IN, flag);
        editor.commit();
    }

    public void setKeyWaitingTime(long time){
        editor.putLong(KEY_WAITING_TIME,time);
        editor.commit();
    }

    public void setKeyAuth(String keyAuth) {
        editor.putString(KEY_AUTH, keyAuth);
        editor.commit();
    }

    public void setAmbulanceID(String id) {
        editor.putString(KEY_AMBULANCE_ID, id);
        editor.commit();
    }

    public void setAmbulanceNO(String id) {
        editor.putString(KEY_AMBULANCE_NO, id);
        editor.commit();
    }

    public void setKeyBookingStatus(int status) {
        editor.putInt(KEY_BOOKING_STATUS, status);
        editor.commit();
    }
    public void setKeyBookingId(String booking_id){
        editor.putString(BOOKING_ID, booking_id);
        editor.commit();
    }

    public void setKeyUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    public void setKeyBaseNumber(String baseNumber) {
        editor.putString(KEY_BASE_NUMBER, baseNumber);
        editor.commit();
    }

    public void setKeyName(String keyName) {
        editor.putString(KEY_NAME, keyName);
        editor.commit();
    }

    public void setKeyMobile(String mobile) {
        editor.putString(KEY_MOBILE, mobile);
        editor.commit();
    }

    public void setKeyBookingJson(String bookingJson) {
        editor.putString(KEY_BOOKING_JSON, bookingJson);
        editor.commit();
    }

    public void setKeyLoginResponse(String loginResponse) {
        editor.putString(KEY_LOGIN_RESPONSE, loginResponse);
        editor.commit();
    }

    public void setKeyAmbulanceType(String ambulanceType) {
        editor.putString(KEY_AMBULANCE_TYPE, ambulanceType);
        editor.commit();
    }

    public void setKeyIsActive(boolean flag) {
        editor.putBoolean(KEY_IS_ACTIVE, flag);
        editor.commit();
    }

    public void setKeyRestore(int restore) {
        editor.putInt(KEY_RESTORE, restore);
        editor.commit();
    }

    public void setFareData(String fareData) {
        editor.putString(KEY_FARE_DATA, fareData);
        editor.commit();
    }

    public long getKeyWaitingTime(){
        return pref.getLong(KEY_WAITING_TIME, 1);
    }

    public String getAmbulanceID(){
        return pref.getString(KEY_AMBULANCE_ID, "0");
    }

    public int getKeyBookingStatus() {
        return pref.getInt(KEY_BOOKING_STATUS, -1);
    }

    public boolean getKeyIsLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getKeyAuth() {
        return pref.getString(KEY_AUTH, null);
    }

    public String getKeyAmbulanceNo() {
        return pref.getString(KEY_AMBULANCE_NO, null);
    }

    public String getKeyUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getKeyBookingId() {
        return pref.getString(BOOKING_ID, null);
    }

    public String getKeyBaseNumber() {
        return pref.getString(KEY_BASE_NUMBER, null);
    }

    public String getKeyName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getKeyMobile() {
        return pref.getString(KEY_MOBILE, null);
    }

    public String getKeyBookingJson() {
        return pref.getString(KEY_BOOKING_JSON, null);
    }

    public String getKeyLoginResponse() {
        return pref.getString(KEY_LOGIN_RESPONSE, null);
    }

    public String getKeyAmbulanceType() {
        return pref.getString(KEY_AMBULANCE_TYPE, null);
    }

    public boolean getKeyIsActive() {
        return pref.getBoolean(KEY_IS_ACTIVE, true);
    }

    public int getKeyRestore() {
        return pref.getInt(KEY_RESTORE, -1);
    }

    public String getKeyFareData() {
        return pref.getString(KEY_FARE_DATA, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}

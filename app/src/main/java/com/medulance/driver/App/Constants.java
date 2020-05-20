package com.medulance.driver.App;

import android.Manifest;

import com.squareup.okhttp.MediaType;

/**
 * Created by sahil on 13/9/16.
 */
public class Constants {

    final public static String SENDER = "MD-LYFHVR";
    public static final String SMS_ORIGIN = "MM-MEDULA";
    public static final String OTP_DELIMITER = ":";

    public static class Splash {
        public static final int PERMISSION_REQUEST_CODE = 1;
        public static final String[] PERMISSION_ARRAY = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.RECEIVE_SMS};
        public static final int RESPONSE_CODE = 1;
    }

    public static class IntentParameters {
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String TYPE = "type";
        public static final String ID = "id";
        public static final String OTP = "otp";
        public static final String MOBILE = "mobile";
        public static final String DATA = "data";
        public static final java.lang.String WHICH = "which";
        public static final String BOOKINGID = "bookingId";
        public static final String STATUS="status";
        public static final String USERID ="userid" ;
    }

    public static class RequestTags {
        public static final String CHECK_DRIVER = "check_driver";
        public static final String DRIVER_LOGIN = "driver_login";
        public static final String SEND_LOCATION = "send_location";
        public static final String DRIVER_BOOKINGS = "driver_bookings";
        public static final String BOOKCAB_RESPONSE = "bookcab_response";
        public static final String RESEND_OTP_REQUEST = "resend_otp_request";
        public static final String START_RIDE = "start_ride";
        public static final String STOP_RIDE = "stop_ride";
        public static final String DRIVER_LOGOUT = "driver_logout";
        public static final String DRIVER_STATUS = "driver_status";
        public static final String PAYMENT_TYPE = "payment_type";
        public static final String RIDE_RATING = "ride_rating";
        public static final String BOOKING_DETAILS = "booking_details";
        public static final String RESTORE_STATE= "restore_state";
        public static final String UPDATE_TOKEN= "update_token";
        public static final String CONFIRM_PAYMENT= "confirmPayment";
        public static final String GET_FARE= "get_fare";
        public static final String DRIVER_ARRIVE = "driver_arrive";
        public static final String DRIVER_ARRIVED = "driver_arrived";
        public static final String UPDATE_BOOKING_STATUS = "update_booking_status";

    }

    public static class Extras {
        public static final long GET_DATA_INTERVAL = 5000;
        public static final String DRIVER = "driver";
        public static final String DRIVER_ID = "driver_id";
        public static final String APP_TOPIC = "lifehover";
        public static final String MOBILE = "mobile";
        public static final String DEVICE_ID = "device_id";
        public static final String VERSION_NAME="version_name";
        public static final String MANAGER_ID = "";
        public static final String API_LOG = "api_log";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String WAITING_TIME="waiting_time";
        public static final String LOCATION_BROADCAST = "location_broadcast";
        public static final int LOCATION_BROADCAST_TYPE = 1;
        public static final String BOOKINGID = "bookingId";
        public static final String STATUS = "status";
        public static final int DRIVER_RIDE_CANCELLED = 2;
        public static final int LOGOUT_BY_MANAGER = 4;
        public static final int PAYING_WITH_PAYTM = 3;
        public static final int PAYING_BY_CASH=5;
        public static final int PAID_WITH_PAYTM=6;
        public static final int PAYMENT_FAILED=8;
        public static final String RATING = "rating";
        public static final java.lang.String PAYMENT_TYPE = "payment_type";
        public static final String CASH = "cash";
        public static final String PAYTM = "paytm";
        public static final int FROM_DIALOG = 1;
        public static final int FROM_SPLASH = 2;
        public static final int FROM_BOOKING_ACTIVITY = 3;
        public static final String TYPE = "from";
        public static final int BEFORESTARTEDLAYOUT = 0;
        public static final int STARTEDLAYOUT = 1;
        public static final int FARELAYOUT = 2;
        public  static final int AFTERFARELAYOUT = 3;
    }

    public static class OKHTTP {
        public static final long CONNECTION_TIMEOUT = 120;//>90 seconds
        public static final long READ_TIMEOUT = 120;//>60 seconds
        public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        public static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
        public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        public static final MediaType MEDIA_TYPE_MP3 = MediaType.parse("audio/mpeg3");
    }

    public static class Urls {
        //------------------Production Server URl-----------------//
        private static final String BASE_URL = "http://medulance.com/";

        //------------------Testing server---------------//
        //private static final String BASE_URL = "http://lifehover.com.cp-48.bigrockservers.com/";

        //---------------LocalHost-----------------------------------------//
        //private static final String BASE_URL = "http://192.168.1.3/";

        private static final String PATH = "CatsApi/api/v3/";

        public static final String URL_CHECK_DRIVER = BASE_URL + PATH + "checkDriver";
        public static final String URL_DRIVER_LOGIN = BASE_URL + PATH + "loginDriver ";
        public static final String URL_RESEND_OTP = BASE_URL + PATH + "resendOTP";
        public static final String URL_SEND_LOCATION = BASE_URL + PATH + "updateDriverLocation";
        public static final String URL_DRIVER_BOOKINGS = BASE_URL + PATH + "getDriverBookings";
        public static final String URL_BOOKING_RESPONSE = BASE_URL + PATH + "bookCabresponse";
        public static final String URL_START_RIDE = BASE_URL + PATH + "startRequest";
        public static final String URL_STOP_RIDE = BASE_URL + PATH + "stopRequest";
        public static final String URL_DRIVER_LOGOUT = BASE_URL + PATH + "logOutDriver";
        public static final String URL_DRIVER_STATUS = BASE_URL + PATH + "selfDriverStatus";
        public static final String URL_PAYMENT_TYPE = BASE_URL + PATH + "payment_type";
        public static final String URL_RIDE_RATING = BASE_URL + PATH + "userRating";
        public static final String URL_BOOKING_DETAILS = BASE_URL + PATH + "getDriverBookingDetail";
        public static final String URL_STATE_STORE= BASE_URL + PATH + "restoreDriverStatus";
        public static final String URL_UPDATE_TOKEN=BASE_URL + PATH + "updateDriverDeviceToken";
        public static final String URL_CONFIRM_PAYMENT= BASE_URL + PATH + "confirmPayment";
        public static final String URL_GET_FARE = BASE_URL + PATH + "getFare";
        public static final String URL_DRIVER_ARRIVED = BASE_URL + PATH + "driverArrived";
        public static final String URL_UPDTATE_BOOKING_STATUS = BASE_URL + PATH + "updateBookingStatusById";
    }

    public static class NavigationDrawer {
        public static final int BOOKING_HISTORY = 1;
        public static final int ACCOUNT = 2;
        public static final int CALL_BASE = 3;
        public static final int FARE_CHART = 4;
        public static final int LOGOUT = 5;
        public static final int ABOUT = 6;
    }

    public static class WebViewUrls {
        private static final String BASE_URL = "http://medulance.com";

        public static final String FARE_CHART = BASE_URL + "/fareChart?type=1";
        public static final String ABOUT = BASE_URL + "/about?type=1";
        public static final String FAQ = BASE_URL + "/faq?type=1";
    }
}

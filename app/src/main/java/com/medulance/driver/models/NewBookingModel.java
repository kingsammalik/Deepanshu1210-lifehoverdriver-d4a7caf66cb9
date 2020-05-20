package com.medulance.driver.models;

/**
 * Created by sahil on 18/9/16.
 */
public class NewBookingModel {

    private String bookingId;
    private String currentlat;
    private String currentlng;
    private String deslat;
    private String deslng;
    private String current;
    private String des;
    private String floor;
    private String lift;
    private String name;
    private String mobile;
    private String base_price;
    private String base_km;
    private String price_per_km;
    private String booking_type;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCurrentlat() {
        return currentlat;
    }

    public void setCurrentlat(String currentlat) {
        this.currentlat = currentlat;
    }

    public String getCurrentlng() {
        return currentlng;
    }

    public void setCurrentlng(String currentlng) {
        this.currentlng = currentlng;
    }

    public String getDeslat() {
        return deslat;
    }

    public void setDeslat(String deslat) {
        this.deslat = deslat;
    }

    public String getDeslng() {
        return deslng;
    }

    public void setDeslng(String deslng) {
        this.deslng = deslng;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getLift() {
        return lift;
    }

    public void setLift(String lift) {
        this.lift = lift;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBase_price() {
        return base_price;
    }

    public void setBase_price(String base_price) {
        this.base_price = base_price;
    }

    public String getBase_km() {
        return base_km;
    }

    public void setBase_km(String base_km) {
        this.base_km = base_km;
    }

    public String getPrice_per_km() {
        return price_per_km;
    }

    public void setPrice_per_km(String price_per_km) {
        this.price_per_km = price_per_km;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    /*map.put("bookingId", data.getString("bookingId"));
                    map.put("currentlat", data.getString("currentlat"));
                    map.put("currentlng", data.getString("currentlng"));
                    map.put("deslat", data.getString("deslat"));
                    map.put("deslng", data.getString("deslng"));
                    map.put("current", data.getString("current"));
                    map.put("des", data.getString("des"));
                    map.put("floor", data.getString("floor"));
                    map.put("lift", data.getString("lift"));
                    map.put("name", data.getString("name"));
                    map.put("mobile", data.getString("mobile"));
                    map.put("bookingId", data.getString("bookingId"));
                    map.put("base_price", data.getString("base_price"));
                    map.put("base_km", data.getString("base_km"));
                    map.put("price_per_km", data.getString("price_per_km"));
                    map.put("booking_type", data.getString("booking_type"));*/

}

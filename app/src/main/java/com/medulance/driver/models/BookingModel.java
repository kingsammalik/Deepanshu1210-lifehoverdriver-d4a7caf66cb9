package com.medulance.driver.models;

import java.util.List;

/**
 * Created by sahil on 10/9/16.
 */
public class BookingModel {
    private String status;
    private String message;
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Data {
        private String id;
        private String unique_id;
        private String pickup_area;
        private String drop_area;
        private String pickup_date;
        private String pickup_time;
        private String amount;
        private String status;
        private String ambulance_type;

        public String getAmbulance_type() {
            return ambulance_type;
        }

        public void setAmbulance_type(String ambulance_type) {
            this.ambulance_type = ambulance_type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getPickup_time() {
            return pickup_time;
        }

        public void setPickup_time(String pickup_time) {
            this.pickup_time = pickup_time;
        }

        public String getPickup_date() {
            return pickup_date;
        }

        public void setPickup_date(String pickup_date) {
            this.pickup_date = pickup_date;
        }

        public String getDrop_area() {
            return drop_area;
        }

        public void setDrop_area(String drop_area) {
            this.drop_area = drop_area;
        }

        public String getPickup_area() {
            return pickup_area;
        }

        public void setPickup_area(String pickup_area) {
            this.pickup_area = pickup_area;
        }

        public String getUnique_id() {
            return unique_id;
        }

        public void setUnique_id(String unique_id) {
            this.unique_id = unique_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

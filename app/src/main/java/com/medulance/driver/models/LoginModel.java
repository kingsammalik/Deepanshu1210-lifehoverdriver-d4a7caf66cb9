package com.medulance.driver.models;

/**
 * Created by sahil on 18/9/16.
 */
public class LoginModel {
    private String status;
    private String message;
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String driver_id;
        private String name;
        private String phone;
        private String manager_id;
        private String ambulance_type;
        private String ambulance_number;
        private String driver_gcm;
        private String manager_name;
        private String manager_phone;
        private String company_name;

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getManager_id() {
            return manager_id;
        }

        public void setManager_id(String manager_id) {
            this.manager_id = manager_id;
        }

        public String getAmbulance_type() {
            return ambulance_type;
        }

        public void setAmbulance_type(String ambulance_type) {
            this.ambulance_type = ambulance_type;
        }

        public String getAmbulance_number() {
            return ambulance_number;
        }

        public void setAmbulance_number(String ambulance_number) {
            this.ambulance_number = ambulance_number;
        }

        public String getDriver_gcm() {
            return driver_gcm;
        }

        public void setDriver_gcm(String driver_gcm) {
            this.driver_gcm = driver_gcm;
        }

        public String getManager_name() {
            return manager_name;
        }

        public void setManager_name(String manager_name) {
            this.manager_name = manager_name;
        }

        public String getManager_phone() {
            return manager_phone;
        }

        public void setManager_phone(String manager_phone) {
            this.manager_phone = manager_phone;
        }

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }
    }
}

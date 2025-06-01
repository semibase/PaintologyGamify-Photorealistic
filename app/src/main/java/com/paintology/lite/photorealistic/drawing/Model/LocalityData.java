package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LocalityData {


    @SerializedName("country")
    public String country;

    @SerializedName("region")
    public String region;

    @SerializedName("city")
    public String city;

    @SerializedName("cityLatLong")
    public String cityLatLong;

    @SerializedName("userIP")
    public String userIP;

    @SerializedName("cityData")
    public ArrayList<cityData> cityData;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityLatLong() {
        return cityLatLong;
    }

    public void setCityLatLong(String cityLatLong) {
        this.cityLatLong = cityLatLong;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public ArrayList<LocalityData.cityData> getCityData() {
        return cityData;
    }

    public void setCityData(ArrayList<LocalityData.cityData> cityData) {
        this.cityData = cityData;
    }

    public class cityData {

        @SerializedName("city")
        public String city;

        @SerializedName("city_ascii")
        public String city_ascii;


        @SerializedName("lat")
        public double lat;

        @SerializedName("lng")
        public double lng;

        @SerializedName("pop")
        public Float pop;

        @SerializedName("country")
        public String country;

        @SerializedName("iso2")
        public String iso2;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity_ascii() {
            return city_ascii;
        }

        public void setCity_ascii(String city_ascii) {
            this.city_ascii = city_ascii;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public Float getPop() {
            return pop;
        }

        public void setPop(Float pop) {
            this.pop = pop;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getIso2() {
            return iso2;
        }

        public void setIso2(String iso2) {
            this.iso2 = iso2;
        }

        public String getIso3() {
            return iso3;
        }

        public void setIso3(String iso3) {
            this.iso3 = iso3;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        @SerializedName("iso3")
        public String iso3;

        @SerializedName("province")
        public String province;


        @SerializedName("timezone")
        public String timezone;

    }
}

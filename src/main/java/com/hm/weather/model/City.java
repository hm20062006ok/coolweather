package com.hm.weather.model;

/**
 * Created by 淼 on 2015-09-29.
 */
public class City {
    public String cityName;
    public String cityCode;
    public int id;
    public int provinceId;

    public City(String cityName, String cityCode, int provinceId) {
        this.cityName = cityName;
        this.cityCode = cityCode;
        this.provinceId = provinceId;
    }

    public City() {

    }
}

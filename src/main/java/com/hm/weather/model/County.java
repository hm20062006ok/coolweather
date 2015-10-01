package com.hm.weather.model;

/**
 * Created by 淼 on 2015-09-29.
 */
public class County {

    public int id;
    public int cityId;
    public String countyName;
    public String countyCode;

    public County( String countyName, String countyCode,int cityId) {
        this.cityId = cityId;
        this.countyName = countyName;
        this.countyCode = countyCode;
    }

    public County() {
    }
}

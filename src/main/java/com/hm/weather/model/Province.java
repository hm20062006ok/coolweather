package com.hm.weather.model;

/**
 * Created by 淼 on 2015-09-29.
 */
public class Province {
    public String provinceName;
    public int id;
    public String provinceCode;


    public Province(String provinceName,  String provinceCode) {
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
    }

    public Province() {
    }
}

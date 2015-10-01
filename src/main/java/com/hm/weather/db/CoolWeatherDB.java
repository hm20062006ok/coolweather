package com.hm.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hm.weather.model.City;
import com.hm.weather.model.County;
import com.hm.weather.model.Province;
import com.hm.weather.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 淼 on 2015-09-29.
 */
public class CoolWeatherDB {

    private static CoolWeatherDB coolWeatherDB = null;
    public static final String DB_NAME = "cool_weather";
    public static final int DB_VERSION = 1;
    private SQLiteDatabase db;


    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }


    public static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            synchronized (CoolWeatherDB.class) {
                if (null == coolWeatherDB) {
                    coolWeatherDB = new CoolWeatherDB(context);
                }
            }
        }
        return coolWeatherDB;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put(Constants.PROVINCE_NAME, province.provinceName);
            values.put(Constants.PROVINCE_CODE, province.provinceCode);
            db.insert(Constants.PROVINCE, null, values);
        }
    }

    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query(Constants.PROVINCE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.id = cursor.getInt(cursor.getColumnIndex(Constants.ID));
                province.provinceName = cursor.getString(cursor.getColumnIndex(Constants.PROVINCE_NAME));
                province.provinceCode = cursor.getString(cursor.getColumnIndex(Constants.PROVINCE_CODE));
                list.add(province);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put(Constants.CITY_NAME, city.cityName);
            values.put(Constants.CITY_CODE, city.cityCode);
            values.put(Constants.PROVINCE_ID, city.provinceId);
            db.insert(Constants.CITY, null, values);
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();

        Cursor cursor = db.query(Constants.CITY, null, "province_id = ? ", new String[]{String.valueOf(provinceId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                City city = new City();

                city.cityName = cursor.getString(cursor.getColumnIndex(Constants.CITY_NAME));
                city.cityCode = cursor.getString(cursor.getColumnIndex(Constants.CITY_CODE));
                city.id = cursor.getInt(cursor.getColumnIndex(Constants.ID));
                city.provinceId = provinceId;
                list.add(city);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void saveCounty(County county){
        if (county != null){
            ContentValues values = new  ContentValues();
            values.put(Constants.COUNTY_CODE,county.countyCode);
            values.put(Constants.COUNTY_NAME,county.countyName);
            values.put(Constants.CITY_ID, county.cityId);
            db.insert(Constants.COUNTY, null, values);
        }
    }

    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();

        Cursor cursor = db.query(Constants.COUNTY, null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);

        if (cursor.moveToFirst()){
            do {
                County county = new County();
                county.id = cursor.getInt(cursor.getColumnIndex(Constants.ID));
                county.countyName = cursor.getString(cursor.getColumnIndex(Constants.COUNTY_NAME));
                county.countyCode = cursor.getString(cursor.getColumnIndex(Constants.COUNTY_CODE));
                county.cityId = cityId;
                list.add(county);

            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }


        return list;

    }
}

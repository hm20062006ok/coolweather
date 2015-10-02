package com.hm.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.hm.weather.db.CoolWeatherDB;
import com.hm.weather.model.City;
import com.hm.weather.model.County;
import com.hm.weather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 淼 on 2015-09-30.
 */
public class Utility {

    private static final  String TAG = "Utility";

    /**
     * 解析服务器返回数据， 并存java.lang.String入到数据库
     *
     * @param coolWeatherDB 数据库操作对象
     * @param response      服务器返回数据
     * @return 是否存储成功
     */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            // TODO: 2015-09-30  分离解析json数据

            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");

                    Province province = new Province();
                    province.provinceCode = array[0];
                    province.provinceName = array[1];

                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }

        }

        return false;
    }


    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] cities = response.split(",");

            if (cities != null && cities.length > 0) {

                for (String c : cities
                        ) {
                    String[] array = c.split("\\|");

                    City city = new City();

                    city.cityCode = array[0];
                    city.cityName = array[1];
                    city.provinceId = provinceId;

                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }

        return false;
    }

    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] cities = response.split(",");
            if (cities != null && cities.length > 0) {
                for (String c : cities
                        ) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.countyCode = array[0];
                    county.countyName = array[1];
                    county.cityId = cityId;
                    coolWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");

            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putBoolean("city_seleted", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));

        editor.commit();
    }
}

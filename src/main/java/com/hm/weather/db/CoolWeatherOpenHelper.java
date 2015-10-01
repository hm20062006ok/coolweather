package com.hm.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hm.weather.util.Constants;

/**
 * Created by 淼 on 2015-09-29.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{
    public static final String TAG = "CoolWeatherOpenHeler";

    public static final String CREATE_PROVINCE = "create table "+ Constants.PROVINCE+" (" +
            "id integer primary key autoincrement, " + Constants.PROVINCE_NAME +
            " text, " + Constants.PROVINCE_CODE +
            " text)";

    public static final String CREATE_CITY = "create table "+Constants.CITY+" (" +
            "id integer primary key autoincrement, " +Constants.CITY_NAME+
            " text, " + Constants.CITY_CODE+
            " text, " +  Constants.PROVINCE_ID +
            " integer)";
    public static final String CREATE_COUNTY = "create table "+Constants.COUNTY+" (" +
            "id integer primary key autoincrement, " +Constants.COUNTY_NAME+
            " text, " +Constants.COUNTY_CODE+
            " text, " + Constants.CITY_ID+
            " integer)";


    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

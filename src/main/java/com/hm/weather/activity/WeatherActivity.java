package com.hm.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hm.weather.R;
import com.hm.weather.service.AutoUpdateService;
import com.hm.weather.util.Constants;
import com.hm.weather.util.HttpCallBackListener;
import com.hm.weather.util.HttpUtil;
import com.hm.weather.util.Utility;

/**
 * Created by 淼 on 2015-10-01.
 */
public class WeatherActivity  extends Activity implements View.OnClickListener {
    private static final String TAG = "WeatherActivity";
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);

        cityNameText= (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);

        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

        currentDateText = (TextView) findViewById(R.id.current_date);
        String countyCode = getIntent().getStringExtra("county_code");

        if ( !TextUtils.isEmpty(countyCode)){
            publishText.setText("同步中");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeather(countyCode);
        }else{
            showWeather();
        }


    }

    private void queryWeather(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode +".xml";
        queryFromServer(address, Constants.COUNTY_CODE);

    }

    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if (Constants.COUNTY_CODE.equals(type)) {

                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");

                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if ("weatherCode".equals(type)){
                    Log.d(TAG, type +"天气代码:"+ response);
                    Utility.handleWeatherResponse(WeatherActivity.this, response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" +weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }

    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天：" + prefs.getString("publish_time","") +"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        startService(new Intent(this, AutoUpdateService.class));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(WeatherActivity.this, ClooseAreaActivity.class);
                intent.putExtra("from_weahter_activity", true);

                startActivity(intent);
                finish();

                break;

            case R.id.refresh_weather:
                publishText.setText("同步中");

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

                String weather_code = prefs.getString("weather_code", "");

                if (!TextUtils.isEmpty(weather_code)){
                    queryWeatherInfo(weather_code);
                }
                break;
            default:
                break;
        }
    }
}

package com.hm.weather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.weather.R;
import com.hm.weather.db.CoolWeatherDB;
import com.hm.weather.model.City;
import com.hm.weather.model.County;
import com.hm.weather.model.Province;
import com.hm.weather.util.Constants;
import com.hm.weather.util.HttpCallBackListener;
import com.hm.weather.util.HttpUtil;
import com.hm.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ClooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ListView listview;
    private TextView titleText;

    private ArrayAdapter<String> adapter;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private List<String> data_List = new ArrayList<String>();

    private CoolWeatherDB coolWeatherDB;

    //用于缓存数据：从服务器或者数据库拿到数据， 传递到listView adapter的dataList
    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private int currentLevel;

    private ProgressDialog progressDialog;



    private boolean isFromWeatherActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);



        setContentView(R.layout.cloose_area);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weahter_activity", false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean("city_selected", false) && isFromWeatherActivity){
            startActivity(new Intent(this, WeatherActivity.class));
            finish();
            return;
        }

        listview = (ListView) findViewById(R.id.list_view);

        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data_List);
        listview.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 根据当前条目层次级别，加载对应数据

                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){

                    String countyCode = countyList.get(position).countyCode;
                    Intent intent = new Intent(ClooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //默认加载省级数据
        queryProvinces();
    }

    private void queryCounties() {
        countyList = coolWeatherDB.loadCounties(selectedCity.id);
        if (countyList.size() > 0) {
            data_List.clear();

            for (County county : countyList) {
                data_List.add(county.countyName);
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            titleText.setText(selectedCity.cityName);
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCounty.countyCode, "county");
        }
    }

    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.id);
        if (cityList.size() > 0) {
            data_List.clear();
            for (City city : cityList) {
                data_List.add(city.cityName);
            }
            adapter.notifyDataSetChanged();

            listview.setSelection(0);
            titleText.setText(selectedCity.cityName);
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedCity.cityCode, "city");
        }

    }

    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();

        if (provinceList.size() > 0) {
            data_List.clear();
            for (Province province : provinceList) {
                //将名称作为listView的item 的部分数据
                data_List.add(province.provinceName);
            }

            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;


        } else {
            //   从服务器查询数据
            queryFromServer(null, Constants.PROVINCE);
        }
    }

    /**
     * 根据传入的省或市或县代码， 和类型从服务器查询数据并存入数据库
     *
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        //从服务器查询数据
        String address = "";
        //定义请求地址
        if (!TextUtils.isEmpty(type)) {


            //address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            //address = "http://www.weather.com.cn/data/list3/city.xml";
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();


        //发送请求
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if(Constants.PROVINCE.equals(type)){
                    result = Utility.handleProvinceResponse(coolWeatherDB,response);
                }else if(Constants.CITY.equals(type)){
                   result =  Utility.handleCitiesResponse(coolWeatherDB , response,selectedProvince.id);
                }else if (Constants.COUNTY.equals(type)){
                    result = Utility.handleCountiesResponse(coolWeatherDB, response,selectedCity.id);
                }

                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();

                            if(Constants.PROVINCE.equals(type)){
                                queryProvinces();
                            }else if(Constants.CITY.equals(type)){
                                queryCities();
                            }else if (Constants.COUNTY.equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ClooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //


    }

    private void closeProgressDialog() {
        //关闭进度
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


    //复写返回键， 再次查询数据


    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_CITY){
            queryCities();
        }else if (currentLevel == LEVEL_COUNTY){
            queryCounties();
        }else{

            if (isFromWeatherActivity){
                startActivity(new Intent(ClooseAreaActivity.this, WeatherActivity.class));
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloose_area, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

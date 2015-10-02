package com.hm.weather.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 淼 on 2015-09-30.
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";

    public static void sendHttpRequest(final String address, final HttpCallBackListener listener) {

        new Thread(new Runnable() {
            HttpURLConnection connection;

            @Override
            public void run() {
                try {
                    URL url = new URL(address);

                    connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    InputStream inputStream = connection.getInputStream();
                    Log.d(TAG, "connection success");
                    StringBuilder response = new StringBuilder();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";

                    while((line = reader.readLine()) != null){
                        response.append(line);
                        Log.d(TAG, "read success");
                    }

                    if (listener != null){
                        Log.d(TAG, "callBack onFinish()");
                        listener.onFinish(response.toString());
                    }

                } catch (Exception e) {

                    if (listener != null){
                        listener.onError(e);
                    }
                    e.printStackTrace();
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();


    }



}

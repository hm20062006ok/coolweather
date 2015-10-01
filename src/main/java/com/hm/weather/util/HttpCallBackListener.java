package com.hm.weather.util;

/**
 * Created by 淼 on 2015-09-30.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}

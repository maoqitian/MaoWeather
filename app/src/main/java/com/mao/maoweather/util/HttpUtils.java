package com.mao.maoweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 毛麒添 on 2017/4/28 0028.
 * 请求网络工具类
 */

public class HttpUtils {

    /**
     * 发送网络请求数据的方法
     * @param address 地址
     * @param callback 回调对象
     */
    public static void sendOkHttpRquest(String address ,okhttp3.Callback callback){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}

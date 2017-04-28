package com.mao.maoweather.util;

import android.text.TextUtils;
import com.mao.maoweather.db.City;
import com.mao.maoweather.db.County;
import com.mao.maoweather.db.Province;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 毛麒添 on 2017/4/28 0028.
 * 项目工具类
 */

public class MyUtils {
    /**
     * 处理县省级数据
     * @param response 需要处理的JSON数据
     * @return 是否处理成功
     */
    public static boolean handleProvinceResponse(String response){

        if(!TextUtils.isEmpty(response)){//如果获取的JSON数据对象不为空
            try {
                JSONArray provincejsonArray=new JSONArray(response);
                for (int i = 0; i < provincejsonArray.length(); i++) {
                    JSONObject provinceJSONObject=provincejsonArray.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceJSONObject.getString("name"));
                    province.setProvinceCode(provinceJSONObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理市级数据
     * @param response JSON数据
     * @param provinceid 省份ID
     * @return 是否处理成功
     */
    public static boolean handleCityResponse(String response,int provinceid){

        if(!TextUtils.isEmpty(response)){//如果获取的JSON数据对象不为空
            try {
                JSONArray cityjsonArray=new JSONArray(response);
                for (int i = 0; i < cityjsonArray.length(); i++) {
                    JSONObject cityJSONObject=cityjsonArray.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityJSONObject.getString("name"));
                    city.setCityCode(cityJSONObject.getInt("id"));
                    city.setProvinceId(provinceid);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理县级数据
     * @param response JSON数据
     * @param cityid  城市id
     * @return 是否处理成功
     */
    public static boolean handleCountyResponse(String response,int cityid){

        if(!TextUtils.isEmpty(response)){//如果获取的JSON数据对象不为空
            try {
                JSONArray countyjsonArray=new JSONArray(response);
                for (int i = 0; i < countyjsonArray.length(); i++) {
                    JSONObject countyJSONObject=countyjsonArray.getJSONObject(i);
                    County county =new County();
                    county.setCountyName(countyJSONObject.getString("name"));
                    county.setWeatherID(countyJSONObject.getString("weather_id"));
                    county.setCityId(cityid);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

package com.mao.maoweather.gsonbean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 毛麒添 on 2017/5/1 0001.
 * 天气总体实体类
 */

public class Weather {

    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}

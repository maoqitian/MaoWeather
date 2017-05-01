package com.mao.maoweather.gsonbean;

/**
 * Created by 毛麒添 on 2017/5/1 0001.
 * 空气质量指数实体类
 */

public class AQI {

    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}

package com.mao.maoweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 毛麒添 on 2017/4/27 0027.
 * 国家
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private String weatherID;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}

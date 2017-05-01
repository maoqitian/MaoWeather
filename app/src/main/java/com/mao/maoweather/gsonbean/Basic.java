package com.mao.maoweather.gsonbean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 毛麒添 on 2017/5/1 0001.
 * 基础天气信息实体类
 */

public class Basic {

    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}

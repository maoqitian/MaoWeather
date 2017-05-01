package com.mao.maoweather.gsonbean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 毛麒添 on 2017/5/1 0001.
 *现在温度实体类
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}



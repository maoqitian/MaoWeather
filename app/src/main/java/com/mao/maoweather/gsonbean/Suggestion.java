package com.mao.maoweather.gsonbean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 毛麒添 on 2017/5/1 0001.
 * 天气建议实体类
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;//舒适程度
    @SerializedName("cw")
    public CarWash carWash;//洗车

    public Sport sport;//运动
    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}

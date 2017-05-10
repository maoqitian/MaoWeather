package com.mao.maoweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mao.maoweather.gsonbean.Forecast;
import com.mao.maoweather.gsonbean.Weather;
import com.mao.maoweather.util.HttpUtils;
import com.mao.maoweather.util.MyUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 毛麒添 on 2017/5/1 0001.
 * 显示天气详细信息页面
 */

public class WeatherActivity extends AppCompatActivity {

    private static final String WEATHER_SERVER_URL="http://guolin.tech/api/weather?cityid=";
    private static final String WEATHER_SERVER_KEY="&key=396c11bdaab347988bb0b79d1797e27c";
    private static final String BING_IMAGE_URL="http://guolin.tech/api/bing_pic";
    private ScrollView sv_weather_layout;
    //天气标题
    private TextView tv_title_city;
    private TextView tv_title_update_time;
    //温度，天气情况（晴、雨）
    private TextView tv_degree_text;
    private TextView tv_weather_info;
    //天气预报模块
    private LinearLayout ll_forecast_layout;
    //空气质量模块
    private TextView tv_aqi_text;
    private TextView tv_pm25_text;
    //生活建议模块
    private TextView tv_suggestion_comfort;
    private TextView tv_suggestion_carwash;
    private TextView tv_suggestion_sport;

    private ImageView iv_bing_pic;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
           /* View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().setStatusBarColor(Color.TRANSPARENT);*/
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
        //获取SharedPreferences中的数据，如果有，则直接加载，没有则请求服务器获取数据
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        String bing_pic = sp.getString("bing_pic", null);
        String weatherStr = sp.getString("weather", null);
        if(bing_pic!=null){
            Glide.with(this).load(bing_pic).into(iv_bing_pic);//有缓存直接显示图片
        }else {
            loadbingImage();//没有缓存则网络加载图片
        }
        if(weatherStr!=null){
            //直接解析
            Weather weather = MyUtils.handleWeatherResponse(weatherStr);
            //显示天气信息
            ShowWeatherInfo(weather);
        }else {
            //请求服务器获取数据
            String weatherId = getIntent().getStringExtra("weather_id");
            sv_weather_layout.setVisibility(View.INVISIBLE);
            //从服务器获取数据
            requestWeatherServer(weatherId);
        }

    }

    private void loadbingImage() {
            HttpUtils.sendOkHttpRquest(BING_IMAGE_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"加载背景图失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String bingImage = response.body().string();
                    //缓存图片
                    SharedPreferences.Editor editor=PreferenceManager.
                            getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("bing_pic",bingImage);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //显示加载的图片
                            Glide.with(WeatherActivity.this).load(bingImage).into(iv_bing_pic);
                        }
                    });
                }
            });
    }

    //请求服务器获取天气数据
    private void requestWeatherServer(String weatherId) {
        //请求地址
        String URL=WEATHER_SERVER_URL+weatherId+WEATHER_SERVER_KEY;
        HttpUtils.sendOkHttpRquest(URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(getApplicationContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                     }
                 });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = MyUtils.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            //缓存数据
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            ShowWeatherInfo(weather);
                        }
                    }
                });
            }
        });
        loadbingImage();//加载图片
    }

    //显示Weather实体类中的天气信息
    private void ShowWeatherInfo(Weather weather) {
        tv_title_city.setText(weather.basic.cityName);
        tv_title_update_time.setText(weather.basic.update.updateTime.split(" ")[1]);
        tv_degree_text.setText(weather.now.temperature+"℃");
        tv_weather_info.setText(weather.now.more.info);
        ll_forecast_layout.removeAllViews();
        for (Forecast forecast:weather.forecastList) {
            View view=View.inflate(getApplicationContext(),R.layout.forecast_item,null);
            TextView tv_forecast_date_text= (TextView) view.findViewById(R.id.tv_forecast_date_text);
            TextView tv_info_text= (TextView) view.findViewById(R.id.tv_info_text);
            TextView tv_max_text= (TextView) view.findViewById(R.id.tv_max_text);
            TextView tv_min_text= (TextView) view.findViewById(R.id.tv_min_text);
            tv_forecast_date_text.setText(forecast.date);
            tv_info_text.setText(forecast.more.info);
            tv_max_text.setText(forecast.temperature.max);
            tv_min_text.setText(forecast.temperature.min);
            ll_forecast_layout.addView(view);
        }
        if(weather.aqi!=null){
            tv_aqi_text.setText(weather.aqi.city.aqi);
            tv_pm25_text.setText(weather.aqi.city.pm25);
        }
        tv_suggestion_comfort.setText("舒适度："+weather.suggestion.comfort.info);
        tv_suggestion_carwash.setText("洗车指数："+weather.suggestion.carWash.info);
        tv_suggestion_sport.setText("运动建议："+weather.suggestion.sport.info);
        ll_forecast_layout.setVisibility(View.VISIBLE);
    }

    //初始化各种控件
    private void initView() {
        sv_weather_layout= (ScrollView) findViewById(R.id.sv_weather_layout);
        ll_forecast_layout= (LinearLayout) findViewById(R.id.ll_forecast_layout);
        tv_title_city= (TextView) findViewById(R.id.tv_title_city);
        tv_title_update_time= (TextView) findViewById(R.id.tv_title_update_time);
        tv_degree_text= (TextView) findViewById(R.id.tv_degree_text);
        tv_weather_info= (TextView) findViewById(R.id.tv_weather_info);
        tv_aqi_text= (TextView) findViewById(R.id.tv_aqi_text);
        tv_pm25_text= (TextView) findViewById(R.id.tv_pm25_text);
        tv_suggestion_comfort= (TextView) findViewById(R.id.tv_suggestion_comfort);
        tv_suggestion_carwash= (TextView) findViewById(R.id.tv_suggestion_carwash);
        tv_suggestion_sport= (TextView) findViewById(R.id.tv_suggestion_sport);
        iv_bing_pic= (ImageView) findViewById(R.id.iv_bing_pic);
    }
}

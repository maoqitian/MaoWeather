package com.mao.maoweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String WEATHER_SERVER_KEY="396c11bdaab347988bb0b79d1797e27c";
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        //获取SharedPreferences中的数据，如果有，则直接加载，没有则请求服务器获取数据
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = sp.getString("weather", null);
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
    }

    //显示Weather实体类中的天气信息
    private void ShowWeatherInfo(Weather weather) {

    }

    //初始化各种控件
    private void initView() {
        sv_weather_layout= (ScrollView) findViewById(R.id.sv_weather_layout);
        ll_forecast_layout= (LinearLayout) findViewById(R.id.ll_forecast_layout);
        tv_title_city= (TextView) findViewById(R.id.tv_title_city);
        tv_degree_text= (TextView) findViewById(R.id.tv_degree_text);
        tv_weather_info= (TextView) findViewById(R.id.tv_weather_info);
        tv_aqi_text= (TextView) findViewById(R.id.tv_aqi_text);
        tv_pm25_text= (TextView) findViewById(R.id.tv_pm25_text);
        tv_suggestion_comfort= (TextView) findViewById(R.id.tv_suggestion_comfort);
        tv_suggestion_carwash= (TextView) findViewById(R.id.tv_suggestion_carwash);
        tv_suggestion_sport= (TextView) findViewById(R.id.tv_suggestion_sport);
    }
}

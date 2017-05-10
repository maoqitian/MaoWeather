package com.mao.maoweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.mao.maoweather.gsonbean.Weather;
import com.mao.maoweather.util.HttpUtils;
import com.mao.maoweather.util.MyUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 毛麒添 on 2017/5/10 0010.
 * 自动更新天气的服务
 */

public class AutoUpdateService extends Service {

    private static final String WEATHER_SERVER_URL="http://guolin.tech/api/weather?cityid=";
    private static final String WEATHER_SERVER_KEY="&key=396c11bdaab347988bb0b79d1797e27c";
    private static final String BING_IMAGE_URL="http://guolin.tech/api/bing_pic";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //更新天气信息
        updateWeather();
        //更新图片
        updateBingpic();
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
        long triggerAtime = SystemClock.elapsedRealtime() + anHour;
        Intent intent1=new Intent(this,AutoUpdateService.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,intent1,0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingpic() {
         HttpUtils.sendOkHttpRquest(BING_IMAGE_URL, new Callback() {
             @Override
             public void onFailure(Call call, IOException e) {
                 e.printStackTrace();
             }

             @Override
             public void onResponse(Call call, Response response) throws IOException {
                 final String bingImage = response.body().string();
                 //缓存图片
                 SharedPreferences.Editor editor=PreferenceManager.
                         getDefaultSharedPreferences(AutoUpdateService.this).edit();
                 editor.putString("bing_pic",bingImage);
                 editor.apply();
             }
         });
    }

    private void updateWeather() {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = sharedPreferences.getString("weather", null);
        if(weatherStr!=null){
            //直接解析
            Weather weather = MyUtils.handleWeatherResponse(weatherStr);
            String weatherId=weather.basic.weatherId;
            //请求地址
            String URL=WEATHER_SERVER_URL+weatherId+WEATHER_SERVER_KEY;
            HttpUtils.sendOkHttpRquest(URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    final Weather weather = MyUtils.handleWeatherResponse(responseText);
                    if(weather!=null&&"ok".equals(weather.status)){
                        //缓存数据
                        SharedPreferences.Editor editor=PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

}

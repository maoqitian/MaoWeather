package com.mao.maoweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getString("weather",null)!=null){
            Intent intent=new Intent(getApplicationContext(), WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

package com.example.a15978.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 15978 on 2018/5/31.
 */

public class Weather
{
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    //daily_forecast will be worked or not?
    //without @SerializedName("daily_forecast")
}

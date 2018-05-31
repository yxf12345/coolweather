package com.example.a15978.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 15978 on 2018/5/31.
 */

public class Basic
{
    @SerializedName("city")
    public String cityName;

    @SerializedName( "id")
    public String weatherId;

    public Update update;
    public class Update
    {
        @SerializedName("loc")
        public String updateTime;
    }
}

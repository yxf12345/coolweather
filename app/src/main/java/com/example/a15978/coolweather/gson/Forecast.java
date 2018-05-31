package com.example.a15978.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 15978 on 2018/5/31.
 */

public class Forecast
{
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature
    {
        public String max;
        public String min;
    }
    public class More
    {
        @SerializedName("txt_d")
        public String info;
    }
}

package com.example.a15978.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 15978 on 2018/5/31.
 */

public class Now
{
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More
    {
        @SerializedName("txt")
        public String info;
    }

}

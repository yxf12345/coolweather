package com.example.a15978.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 15978 on 2018/5/29.
 */

public class HttpUtil
{
    public static void sendOkHttpRequest( String address, okhttp3.Callback callback )
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url( address ).build();
        client.newCall( request ).enqueue( callback );
    }
}

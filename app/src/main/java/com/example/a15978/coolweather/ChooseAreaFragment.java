package com.example.a15978.coolweather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.example.a15978.coolweather.db.City;
import com.example.a15978.coolweather.db.County;
import com.example.a15978.coolweather.db.GetName;
import com.example.a15978.coolweather.db.Province;
import com.example.a15978.coolweather.util.HttpUtil;
import com.example.a15978.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 15978 on 2018/5/30.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0, LEVEL_CITY = 1, LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    public static boolean locate = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                dataList);
        listView.setAdapter(adapter);
        return view;
    }

    public LocationClient mLocationClient;
    private void locate()
    {
        initLocation();
        mLocationClient.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if( mLocationClient != null )
            this.mLocationClient.stop();
    }
    public class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation( BDLocation location )
        {
            locate = false;
            Log.d(TAG, location.toString() );
            String province = location.getProvince();
            String city = location.getCity();
            String county = location.getDistrict();
            if( province == null || city == null || county == null )
            {
                Log.d(TAG, province );
                Log.d(TAG, city );
                Log.d(TAG, county);
                return;
            }
            province = province.split("省")[0].trim();
            city = city.split( "市")[0].trim();
            county = county.split( "区")[0].trim();
            Log.d(TAG, "province is " + province );
            Log.d(TAG, "city is " + city);
            Log.d(TAG, "county is " + county );
            for( Province p : provinceList )
            {
                Log.d(TAG, p.getName());
                if( p.getName().equals( province.trim() ) )
                {
                    selectedProvince = p;
                    queryCities();
                    for( City c : cityList )
                    {
                        Log.d(TAG, c.getName().trim());
                        if( city.equals( c.getName().trim() ) )
                        {
                            selectedCity = c;
                            queryCounties();
                            for( County county1 : countyList )
                            {
                                Log.d(TAG, county1.getName());
                                if( county.equals( county1.getName().trim() ) )
                                {
                                    String weatherId = county1.getWeatherId();
                                    Intent intent = new Intent( getActivity(), WeatherActivity.class );
                                    intent.putExtra( "weather_id", weatherId );
                                    startActivity( intent );
                                    getActivity().finish();
                                    return;
                                }
                            }
                            for( County county1 : countyList )
                            {
                                if( city.equals( county1.getName().trim() ) )
                                {
                                    String weatherId = county1.getWeatherId();
                                    Intent intent = new Intent( getActivity(), WeatherActivity.class );
                                    intent.putExtra( "weather_id", weatherId );
                                    startActivity( intent );
                                    getActivity().finish();
                                    return;
                                }
                            }
                        }
                    }

                }
            }

        }
    }

    private void initLocation()
    {
        SDKInitializer.initialize( getActivity().getApplicationContext() );
        mLocationClient =new LocationClient( getActivity().getApplicationContext() );
        mLocationClient.registerLocationListener( new MyLocationListener() );
        LocationClientOption option = new LocationClientOption();
        option.setLocationNotify( true );
        option.setIsNeedAddress( true );
        mLocationClient.setLocOption( option );
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED )
        {
            permissionList.add( Manifest.permission.ACCESS_FINE_LOCATION );
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED )
        {
            permissionList.add( Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED )
        {
            permissionList.add( Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if( !permissionList.isEmpty() )
        {
            String[] permissions = permissionList.toArray( new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1 );
        }
        else
        {
            mLocationClient.start();
        }
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
    {
        switch( requestCode )
        {
            case 1:
                if( grantResults.length > 0 ) {
                    for (int result : grantResults)
                    {
                        if( result != PackageManager.PERMISSION_GRANTED )
                        {
                            Toast.makeText(getActivity(), "拒绝的话将不能进行自动定位", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    mLocationClient.start();
                }
                else
                {
                    Toast.makeText(getActivity(), "unknown error happened", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener((parent, view, position, id) ->
        {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(position);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryCounties();
            }
            else if( currentLevel == LEVEL_COUNTY )
            {
                String weatherId = countyList.get( position ).getWeatherId();
                if( getActivity() instanceof MainActivity )
                {
                    Intent intent = new Intent( getActivity(), WeatherActivity.class );
                    intent.putExtra( "weather_id", weatherId );
                    startActivity( intent );
                    getActivity().finish();
                }
                else if( getActivity() instanceof WeatherActivity )
                {
                    WeatherActivity activity = (WeatherActivity)getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefresh.setRefreshing( true );
                    activity.requestWeather( weatherId );
                }
            }
        });
        backButton.setOnClickListener((v) ->
        {
            if (currentLevel == LEVEL_COUNTY) {
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces();
            }
        });
        queryProvinces();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if( locate )
        {
            locate();
        }
    }

    private void queryProvinces() {
        titleText.setText( "中国" );
        backButton.setVisibility(View.GONE);
        if (provinceList == null || provinceList.isEmpty()) {
            provinceList = DataSupport.findAll(Province.class);
            if (provinceList.size() == 0) {
                String address = "http://guolin.tech/api/china";
                queryFromServer(address, "province");
                return;
            }
        }
        clearAndSet(provinceList);
        currentLevel = LEVEL_PROVINCE;
    }

    private <T extends GetName> void clearAndSet(List<T> list) {
        dataList.clear();
        for (T t : list)
            dataList.add(t.getName());
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
    }

    private static final String TAG = "tag";
    private void queryCities()
    {
        titleText.setText( selectedProvince.getProvinceName() );
        backButton.setVisibility(View.VISIBLE );
        cityList = DataSupport.where( "provinceid = ?", String.valueOf(
                selectedProvince.getId() ) ).find( City.class );
        if( cityList.size() > 0 )
        {
            clearAndSet( cityList );
            currentLevel = LEVEL_CITY;
        }
        else
        {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer( address, "city" );
        }
    }


    private void queryCounties()
    {
        titleText.setText( selectedCity.getCityName() );
        backButton.setVisibility(View.VISIBLE );
        countyList =DataSupport.where( "cityid = ?", String.valueOf(
                selectedCity.getId()
        )).find( County.class );
        if( countyList.size() > 0 )
        {
            clearAndSet(countyList);
            currentLevel = LEVEL_COUNTY;
        }
        else
        {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" +
                    provinceCode + "/" + cityCode;
            queryFromServer( address, "county" );
        }
    }

    private void queryFromServer( String address, final String type )
    {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest( address, new Callback()
        {
           @Override
            public void onResponse(okhttp3.Call call, Response response ) throws IOException
           {
               String responseText = response.body().string();
               boolean result = false;
               if( "province".equals( type ) )
               {
                   result = Utility.handleProvinceResponse( responseText );
               }
               else if( type.equals( "city" ) )
               {
                   result = Utility.handleCityResponse(responseText,
                           selectedProvince.getId() );
               }
               else if( type.equals( "county" ) )
               {
                   result = Utility.handleCountyResponse( responseText,
                           selectedCity.getId() );
               }
               final boolean r = result;
               getActivity().runOnUiThread( new Runnable()
               {
                   public void run()
                   {
                       closeProgressDialog();
                       if( r )
                       {
                           if( type.equals( "province" ) )
                           {
                               queryProvinces();
                           }
                           else if( type.equals( "city" ) )
                           {
                               queryCities();
                           }
                           else if( type.equals( "county" ) )
                           {
                               queryCounties();
                           }
                       }
                       else
                       {
                           Toast.makeText(getContext(), "抱歉，加载失败", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }

            @Override
            public void onFailure(okhttp3.Call call, IOException e )
            {
                getActivity().runOnUiThread( () ->
                {
                   closeProgressDialog();
                    Toast.makeText(getContext(), "抱歉，加载失败", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showProgressDialog()
    {
        if( progressDialog == null )
        {
            progressDialog = new ProgressDialog(( getActivity() ) );
            progressDialog.setMessage( "loading..." );
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog()
    {
        if( progressDialog != null )
            progressDialog.dismiss();
    }

}

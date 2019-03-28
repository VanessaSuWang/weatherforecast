package com.example.a60570.weatherforecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HeFetchr {
    private static final String TAG="HeFetchr:";
    private String loc;
    private String unit;
    private String uni;
    private static final String API_KEY="e653b2df54cc4520936c3a63b760f0da";

    public  byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url=new URL(urlSpec);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        try{
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream in=connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    public List<WeatherF> fetchItems(Context c) {

        List<WeatherF> items = new ArrayList<>();
        SharedPreferences pref=c.getSharedPreferences("file",0);
        loc=pref.getString("location","Changsha");
        unit=pref.getString("units","Metric");
        //metric公制  inch英制 温度°C 华氏度° F  风速km/h   mile/h
        if (unit.equals("Metric")){
            uni="m";
        }else {
            uni="i";
        }
        Log.d(TAG,loc);
        Log.d(TAG,unit);
        try {
            String url = Uri.parse("https://free-api.heweather.com/s6/weather/forecast")
                    .buildUpon()
                    .appendQueryParameter("location", loc)
                    .appendQueryParameter("key", API_KEY)
                    .appendQueryParameter("lang", "en")
                    .appendQueryParameter("unit", uni)
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody,c);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return items;
    }

    private void parseItems(List<WeatherF> items, JSONObject jsonBody,Context c)
            throws JSONException {
        SharedPreferences pref=c.getSharedPreferences("file",0);
        SharedPreferences.Editor editor=pref.edit();
        JSONArray weatherJsonArray;
        weatherJsonArray = jsonBody.getJSONArray("HeWeather6");
        JSONObject outsideJsonObject = weatherJsonArray.getJSONObject(0);
        JSONObject basicJsonObject=outsideJsonObject.getJSONObject("basic");
        Log.d(TAG,basicJsonObject.toString());
        String location=basicJsonObject.getString("location");
        //getLong得到了整数  解决是先getString
        String lat=basicJsonObject.getString("lat");
        String lon=basicJsonObject.getString("lon");
        Log.d(TAG,"lat:"+String.valueOf(lat));
        Log.d(TAG,"lon:"+String.valueOf(lon));
        editor.putString("lat",lat);
        editor.putString("lon",lon);

        JSONArray dailyforecastJsonArray=outsideJsonObject.getJSONArray("daily_forecast");
        for(int i=0;i<7;i++){

            WeatherF w=new WeatherF();
            JSONObject o=dailyforecastJsonArray.getJSONObject(i);
            w.setLocation(location);
            w.setdate(o.getString("date"));
           // Log.d(TAG,String.valueOf(w.getdate()));
            w.setHumidity(o.getInt("hum"));
           // Log.d(TAG,String.valueOf(w.getHumidity()));
            w.setPressure(o.getInt("pres"));
           // Log.d(TAG,String.valueOf(w.getPressure()));
            w.setWind(o.getInt("wind_spd"));
           // Log.d(TAG,String.valueOf(w.getWind()));
            w.setMax_temp(o.getInt("tmp_max"));
           // Log.d(TAG,String.valueOf(w.getMax_temp()));
            w.setMin_temp(o.getInt("tmp_min"));
           // Log.d(TAG,String.valueOf(w.getMin_temp()));
            w.setWeather(o.getString("cond_txt_d"));
          //  Log.d(TAG,w.getWeather());
            w.setWeatherId(o.getInt("cond_code_d"));
            //Log.d(TAG,String.valueOf(w.getWeatherId()));
            if (i==0){
                editor.putString("latest_weather",w.getWeather());
                editor.putInt("latest_min",w.getMin_temp());
                editor.putInt("latest_max",w.getMax_temp());
                editor.putString("latest_location",w.getLocation());
                editor.apply();
            }
            items.add(w);
        }
    }



    public String fetchLocation(String loc) {
        String status=null;
        try {
            String url = Uri.parse("https://search.heweather.com/find")
                    .buildUpon()
                    .appendQueryParameter("location", loc)
                    .appendQueryParameter("key", API_KEY)
                    .appendQueryParameter("lang", "en")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            status= parseItems(jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return status;

    }


    private String parseItems(JSONObject jsonBody)
            throws JSONException {
        JSONArray weatherJsonArray;
        weatherJsonArray = jsonBody.getJSONArray("HeWeather6");
        JSONObject outsideJsonObject = weatherJsonArray.getJSONObject(0);
        String status=outsideJsonObject.getString("status");
        Log.d(TAG,status);
       return status;
        }
    }




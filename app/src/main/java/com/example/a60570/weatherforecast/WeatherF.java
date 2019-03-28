package com.example.a60570.weatherforecast;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class WeatherF implements Serializable {
    private String date;
    private int min_temp;
    private int max_temp;
    private String weather;
    private int weatherId;
    private int humidity;
    private int pressure;
    private int wind;
    private String location;

    WeatherF(){

    }

    public static String parseDate(Date d){
        SimpleDateFormat exm=new SimpleDateFormat("E");
        return exm.format(d);
        //书上推荐DateFormat
        //final static CharSequence format(CharSequence inFormat, Date inDate) //传入Date对象
        //
        //final static CharSequence format(CharSequence inFormat, Calendar inDate) //Calendar对象
        //
        //final static CharSequence format(CharSequence inFormat, long inTimeInMillis) //long对象
    }
    public  static String  parseDateToFormat(Date d){
        SimpleDateFormat exm=new SimpleDateFormat("MMMM dd");
        return exm.format(d);
    }
    public static Date parseServerTime(String serverTime,String format){
        if(format==null||format.isEmpty()){
            format="yyyy-MM-dd";
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format,Locale.CHINESE);
        Date date=null;
        try{
            date=sdf.parse(serverTime);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public double getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String c) {
        this.date = c;
    }

    public int getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(int min_temp) {
        this.min_temp = min_temp;
    }

    public int getMax_temp() {
        return max_temp;
    }

    public void setMax_temp(int max_temp) {
        this.max_temp = max_temp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }


    }



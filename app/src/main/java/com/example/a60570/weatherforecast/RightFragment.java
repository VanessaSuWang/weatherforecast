package com.example.a60570.weatherforecast;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class RightFragment extends Fragment {
    private View view;
    public static final String TAG="RightFragment";
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d(TAG,"onCreateView");
        view=inflater.inflate(R.layout.right_fragment,container,false);
        WeatherF w=(WeatherF) getArguments().getSerializable("weather");//debug好久本来用的savedInstance
        refresh(w);
        return view;
    }

    //10.2
    //    要附加argument bundle给fragment，需调用Fragment.setArguments(Bundle)方法。而且，
    //    还必须在fragment创建后、添加给activity前完成。
    //为满足以上要求，Android开发人员采取的习惯做法是：添加名为newInstance()的静态方
    //法给Fragment类。使用该方法，完成fragment实例及Dundle对象的创建，然后将argument放入
    //bundle中，最后再附加给fragment。
    //托管activity需要fragment实例时，转而调用newInstance()方法，而非直接调用其构造方法。
    //而且，为满足fragment创建argument的要求，activity可给newInstance()方法传入任何需要的参数。
    public static  RightFragment newInstance(WeatherF w) {
        Bundle args = new Bundle();
        args.putSerializable("weather", w);
        RightFragment fragment = new RightFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public void refresh(WeatherF w){
        View layoutview=view.findViewById(R.id.content_layout);
        layoutview.setVisibility(View.VISIBLE);
        TextView week=(TextView)layoutview.findViewById(R.id.weekday);
        TextView date=(TextView)layoutview.findViewById(R.id.date);
        TextView max=(TextView)layoutview.findViewById(R.id.max_tem);
        TextView min=(TextView)layoutview.findViewById(R.id.min_tem);
        TextView weather=(TextView)layoutview.findViewById(R.id.weather);
        TextView humidity=(TextView)layoutview.findViewById(R.id.humidity);
        TextView pressure=(TextView)layoutview.findViewById(R.id.pressure);
        TextView wind=(TextView)layoutview.findViewById(R.id.wind);
        ImageView weather_image=(ImageView) layoutview.findViewById(R.id.weather_image);
        Date d=w.parseServerTime(w.getdate(),null);
        week.setText(w.parseDate(d));
        date.setText(w.parseDateToFormat(d));//此处格式化

        SharedPreferences pref=getContext().getSharedPreferences("file",0);
        String unit=pref.getString("units","Metric");
        if(unit.equals("Metric")) {
            max.setText(w.getMax_temp() + "°C");
            min.setText(w.getMin_temp() + "°C");
            wind.setText("Wind:"+w.getWind()+"km/h SE");
        }else {
            max.setText(w.getMax_temp() + "°F");
            min.setText(w.getMin_temp() + "°F");
            wind.setText("Wind:"+w.getWind()+"mile/h SE");
        }
        weather.setText(w.getWeather());
        humidity.setText("Humidity:"+w.getHumidity()+"％");
        pressure.setText("Pressure:"+w.getPressure()+"hPa");

        int ImageId=0;
        switch (w.getWeatherId()){
            case 100: ImageId=R.drawable.m100;break;
            case 101: ImageId=R.drawable.m101;break;
            case 102: ImageId=R.drawable.m102;break;
            case 103: ImageId=R.drawable.m103;break;
            case 104: ImageId=R.drawable.m104;break;
            case 200: ImageId=R.drawable.m200;break;
            case 201: ImageId=R.drawable.m201;break;
            case 202: ImageId=R.drawable.m202;break;
            case 203: ImageId=R.drawable.m203;break;
            case 204: ImageId=R.drawable.m204;break;
            case 205: ImageId=R.drawable.m205;break;
            case 206: ImageId=R.drawable.m206;break;
            case 207: ImageId=R.drawable.m207;break;
            case 208: ImageId=R.drawable.m208;break;
            case 209: ImageId=R.drawable.m209;break;
            case 211: ImageId=R.drawable.m209;break;
            case 212: ImageId=R.drawable.m212;break;
            case 213: ImageId=R.drawable.m213;break;
            case 300: ImageId=R.drawable.m300;break;
            case 301: ImageId=R.drawable.m301;break;
            case 302: ImageId=R.drawable.m302;break;
            case 303: ImageId=R.drawable.m303;break;
            case 304: ImageId=R.drawable.m304;break;
            case 305: ImageId=R.drawable.m305;break;
            case 306: ImageId=R.drawable.m306;break;
            case 307: ImageId=R.drawable.m307;break;
            case 308: ImageId=R.drawable.m308;break;
            case 309: ImageId=R.drawable.m309;break;
            case 310: ImageId=R.drawable.m310;break;
            case 311: ImageId=R.drawable.m311;break;
            case 312: ImageId=R.drawable.m312;break;
            case 313: ImageId=R.drawable.m313;break;
            case 314: ImageId=R.drawable.m314;break;
            case 315: ImageId=R.drawable.m315;break;
            case 316: ImageId=R.drawable.m316;break;
            case 317: ImageId=R.drawable.m317;break;
            case 318: ImageId=R.drawable.m318;break;
            case 399: ImageId=R.drawable.m399;break;
            case 400: ImageId=R.drawable.m400;break;
            case 401: ImageId=R.drawable.m401;break;
            case 402: ImageId=R.drawable.m402;break;
            case 403: ImageId=R.drawable.m403;break;
            case 404: ImageId=R.drawable.m404;break;
            case 405: ImageId=R.drawable.m405;break;
            case 406: ImageId=R.drawable.m406;break;
            case 407: ImageId=R.drawable.m407;break;
            case 408: ImageId=R.drawable.m408;break;
            case 409: ImageId=R.drawable.m409;break;
            case 410: ImageId=R.drawable.m410;break;
            case 499: ImageId=R.drawable.m499;break;
            case 500: ImageId=R.drawable.m500;break;
            case 501: ImageId=R.drawable.m501;break;
            case 502: ImageId=R.drawable.m502;break;
            case 503: ImageId=R.drawable.m503;break;
            case 504: ImageId=R.drawable.m504;break;
            case 507: ImageId=R.drawable.m507;break;
            case 508: ImageId=R.drawable.m508;break;
            case 509: ImageId=R.drawable.m509;break;
            case 510: ImageId=R.drawable.m510;break;
            case 511: ImageId=R.drawable.m511;break;
            case 512: ImageId=R.drawable.m512;break;
            case 513: ImageId=R.drawable.m513;break;
            case 514: ImageId=R.drawable.m514;break;
            case 515: ImageId=R.drawable.m515;break;
            case 900: ImageId=R.drawable.m900;break;
            case 901: ImageId=R.drawable.m901;break;
            case 999: ImageId=R.drawable.m999;break;
        }weather_image.setImageResource(ImageId);
    }

}

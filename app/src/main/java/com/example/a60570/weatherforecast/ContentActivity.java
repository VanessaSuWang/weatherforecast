package com.example.a60570.weatherforecast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

public class ContentActivity extends AppCompatActivity {
    public static final String TAG="ContentActivity";
    private WeatherF w;
    public static void actionStart(Context context, WeatherF w) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra("weather", w);
        context.startActivity(intent);
    }


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        w= (WeatherF) getIntent().getSerializableExtra("weather");
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container2);
        Log.d(TAG,"on Create");
        if (fragment == null) {
            fragment = RightFragment.newInstance(w);
            fm.beginTransaction()
                    .add(R.id.fragment_container2, fragment)
                    .commit();
        }
    }
    private String get_share_message(){
        String msg;
        SharedPreferences pref=getSharedPreferences("file",0);
        String unit=pref.getString("units","Metric");
        if(unit.equals("Metric")) {
             msg = w.getdate() + "\n" + w.getLocation() + "'s weather:" + w.getWeather() + "\ntemperature:" + String.valueOf(w.getMin_temp()) +
                    "°C-" + String.valueOf(w.getMax_temp()) + "°C\nhumidity:" + String.valueOf(w.getHumidity()) + "%     \nwind:" + String.valueOf(w.getWind()) + " km/s SE";
        }else{
            msg = w.getdate() + "\n" + w.getLocation() + "'s weather:" + w.getWeather() + "\ntemperature:" + String.valueOf(w.getMin_temp()) +
                    "°F-" + String.valueOf(w.getMax_temp()) + "°F \nhumidity" + String.valueOf(w.getHumidity()) + "%     \nwind:" + String.valueOf(w.getWind()) + " mile/s SE";

        }
        return msg;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.share_buttton:
                Log.d(TAG,"Click here");
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,get_share_message());
                i.putExtra(Intent.EXTRA_SUBJECT,"Weather Forecast");
                startActivity(i);
                return true;
                default:return super.onOptionsItemSelected(item);
        }

    }
}

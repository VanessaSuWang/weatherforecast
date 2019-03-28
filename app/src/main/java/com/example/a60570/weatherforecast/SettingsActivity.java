package com.example.a60570.weatherforecast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SettingsActivity  extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG="SettingsActivity:";
    private TextView location;
    private TextView units;
    private TextView notifications;
    private CheckBox noti;
    private String loc;
    private String unit;
    private boolean notification;
    private Context mContext;
    private boolean checkLocation=false;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext=this;
        findViewById(R.id.location_layout).setOnClickListener(this);
        findViewById(R.id.units_layout).setOnClickListener(this);
        findViewById(R.id.checkBox_noti).setOnClickListener(this);
        location=(TextView)findViewById(R.id.location);
        units=(TextView)findViewById(R.id.units);
        notifications=(TextView)findViewById(R.id.notifications);
        noti=(CheckBox)findViewById(R.id.checkBox_noti);
//读取sharedpreferences的数据
        SharedPreferences pref=getSharedPreferences("file",0);
        loc=pref.getString("location","Changsha");
        Log.d(TAG,loc);
        unit=pref.getString("units","Metric");
        Log.d(TAG,unit);
        notification=pref.getBoolean("notification",true);
        if(notification==true){
        PollService.setServiceAlarm(this,true);
        }
        location.setText(loc);
        units.setText(unit);
        noti.setChecked(notification);

    }

    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
        SharedPreferences.Editor editor= getSharedPreferences("file",MODE_PRIVATE)
                .edit();
        editor.putBoolean("notification",notification);
        editor.putString("units",unit);
        editor.putString("location",loc);
        editor.commit();

    }
    public void onClick(View v) {
        Log.d(TAG, "onClick runned");
        switch(v.getId()){
            case R.id.location_layout:
                alert = null;
                builder = new AlertDialog.Builder(mContext);
                final EditText et=new EditText(this);
                alert=builder.setTitle("Enter the location:")
                        .setView(et)
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mContext, "you clicked cancel", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("commit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                 new FetchItemsTask().execute(et.getText().toString());
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                       if(checkLocation){
                                           loc=et.getText().toString();
                                           location.setText(loc);
                                       }else {
                                           Log.d(TAG,"here");
                                           Toast.makeText(mContext, "Enter Wrong", Toast.LENGTH_SHORT).show();
                                       }
                                    }
                                }, 3000);//3秒后执行Runnable中的run方法


                            }
                        })
                        .create();
                alert.show();
                break;
            case R.id.units_layout:
                alert = null;
                builder = new AlertDialog.Builder(mContext);
                int checkitem=0;
                final String[] unitsss = new String[]{"Metric", "Inch"};
                if(unit.equals(unitsss[0]))
                { }else {
                    checkitem=1;
                }
                alert = builder
                        .setTitle("Choose the unit")
                        .setSingleChoiceItems(unitsss, checkitem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "你选择了" + unitsss[which], Toast.LENGTH_SHORT).show();
                                unit=unitsss[which];
                                units.setText(unit);
                            }
                        }).create();
                alert.show();
                break;

            case R.id.checkBox_noti:
                notification=noti.isChecked();

                if(noti.isChecked()){
                    SharedPreferences pref=getSharedPreferences("file",0);
                    String stored_loc=pref.getString("location","Changsha");
                    Log.d(TAG,stored_loc);
                    Log.d(TAG,loc);
                    if(stored_loc.equals(loc)){
                        //latest不需要更新 直接用
                    }else {
                        SharedPreferences.Editor editor=pref.edit();
                        editor.putString("latest_weather",null);
                        editor.putString("location",loc);
                        editor.apply();

                    }
                    notifications.setText("Enabled");
                   PollService.setServiceAlarm(this,noti.isChecked());

                    Log.d(TAG,"here");


                }else {
                    notifications.setText("Disabled");
                    PollService.setServiceAlarm(this,noti.isChecked());
                    //记得取消通知


                }
                //   PollService.setServiceAlarm(this, noti.isChecked());
                break;



        }

    }
  // new FetchItemsTask().execute();



    private class FetchItemsTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String...params) {
            String loc = params[0];
            String ans=new HeFetchr().fetchLocation(loc);
            Log.d(TAG,ans);
            return ans;
        }
       // AsyncTask<Params,Progress,Result>是一个抽象类,通常用于被继承.继承AsyncTask需要指定如下三个泛型参数:

       // Params:启动任务时输入的参数类型.

          //      Progress:后台任务执行中返回进度值的类型.

           //     Result:后台任务执行完成后返回结果的类型.
        @Override
        protected void onPostExecute(String status) {
           if(status.equals("ok")){
            checkLocation =true;
        }else {
               checkLocation=false;
               Log.d(TAG,String.valueOf(checkLocation));
           }
    }
    }
}



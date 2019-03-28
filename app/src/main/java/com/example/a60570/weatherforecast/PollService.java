package com.example.a60570.weatherforecast;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollService  extends IntentService {
    private static final String TAG = "PollService";
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);
    public static final String ACTION_SHOW_NOTIFICATION =
            "com.example.a60570.weatherforecast.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE =
            "com.example.a60570.weatherforecast.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Log.d(TAG,"setServiceAlarm");
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"onHandleIntent");
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        SharedPreferences pref=getSharedPreferences("file",0);
        String weather = pref.getString("latest_weather",null);
        int min_tep=pref.getInt("latest_min",0);
        int max_tep=pref.getInt("latest_max",0);
        String unit=pref.getString("units","Metric");
        String location=pref.getString("latest_location","Changsha");
        List<WeatherF> items;

        if (weather == null) {
            items = new HeFetchr().fetchItems(this);
            weather=items.get(0).getWeather();
            min_tep=items.get(0).getMin_temp();
            max_tep=items.get(0).getMax_temp();
            location=items.get(0).getLocation();
        }

            Intent i = MainActivity.newIntent(this);
            PendingIntent pi = PendingIntent
                    .getActivity(this, 0, i, 0);

           /* Notification notification = new NotificationCompat.Builder(this,"default")
                    .setTicker("WeatherForecast")
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle("WeatherForecast")
                    .setContentText("Forecast:"+weather+" High:"+max_tep+" Low:"+min_tep+"    "+unit)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
*/
        String id = "my_channel_01";
        String name="渠道名字";
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            Toast.makeText(this, mChannel.toString(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, mChannel.toString());
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this,id)
                    .setChannelId(id)
                    .setContentTitle("WeatherForecast")
                    .setContentText(location+"'s Forecast:"+weather+" High:"+max_tep+"° Low:"+min_tep+"°    "+unit)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        }  notificationManager.notify(111123, notification);
          // showBackgroundNotification(0, notification);
        }



   private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }
//chap 29检查后台网络的可用性
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}




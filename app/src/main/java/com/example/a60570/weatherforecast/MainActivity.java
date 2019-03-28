package com.example.a60570.weatherforecast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SettingsSlicesContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LeftFragment.Callbacks{
    public static final String TAG="MainActivity";
    private Context context;
    public static Intent newIntent(Context context) {
        return new Intent(context,MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterdetail);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
        FragmentManager fm =getSupportFragmentManager();
        Fragment leftFragment=fm.findFragmentById(R.id.fragment_container1);
        if(leftFragment==null){
            leftFragment=new LeftFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container1,leftFragment)
                    .commit();
        }

    }
    @Override
    public void onItemSelected(WeatherF w){
       if(findViewById(R.id.fragment_container2)==null){
           ContentActivity.actionStart(this,w);
       }else {
           RightFragment right=RightFragment.newInstance(w);
           getSupportFragmentManager().beginTransaction()
                   .replace(R.id.fragment_container2, right)
                   .commit();
       }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        Log.d(TAG,"here");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch ((id)){
            case R.id.settings:
                Log.d(TAG,"start activity");
                SettingsActivity.actionStart(this);
                return true;
            case R.id.map_location:
                SharedPreferences pref=getSharedPreferences("file",0);
                String lat =pref.getString("lat","28.19408989");
                String lon=pref.getString("lon","112.98227692");
               double latt=Double.parseDouble(lat);
               long lattt=new Double(latt).longValue();
               double lonn=Double.parseDouble(lon);
               long lonnn=new Double(lonn).longValue();
               Log.d(TAG,String.valueOf(lat)+"    "+String.valueOf(lon));
               Intent intent=null;
                if (isAvilible(context, "com.autonavi.minimap")) {
                    try{
                        intent = Intent.getIntent("androidamap://viewMap?poiname=我的目的地&lat="+lattt+"&lon="+lonnn+"&dev=0");
                        context.startActivity(intent);
                    } catch (URISyntaxException e)
                    {e.printStackTrace(); }
                }else{
                    Toast.makeText(context, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
               return true;
                default:return super.onOptionsItemSelected(item);
        }

    }
    public static boolean isAvilible(Context context, String packageName){
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
}

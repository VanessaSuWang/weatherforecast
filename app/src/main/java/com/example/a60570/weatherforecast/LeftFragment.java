package com.example.a60570.weatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class LeftFragment extends Fragment {
    private static final String TAG = "LeftFragment";
    private boolean isTwoPane;

    private RecyclerView mweatherRecycle;
    private List<WeatherF> mItems = new ArrayList<>();
    private Callbacks mCallbacks;
    private SQLiteDatabase db;
    private MyDatabaseHelper dbHelper;
    //17.2
    //。fragment天生是个独立的开发构件。如果要开发fragment用来
    //添加其他fragment到activity的FragmentManager，那么这个fragment就必须知道托管activity是如
    //何工作的。结果，该fragment就再也无法作为独立的开发构件使用了。
   //    为了让fragment独立，我们可以在fragment中定义回调接口，委托托管activity来完成那些不
   //    应由fragment处理的任务。托管activity将实现回调接口，履行托管fragment的任务。
    public interface Callbacks{
        void onItemSelected(WeatherF w);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        dbHelper=new MyDatabaseHelper(getContext(),"WeatherStore.db",null,1);
        db=dbHelper.getWritableDatabase();
        Log.d(TAG,String.valueOf(isNetworkAvailableAndConnected()));
        if(isNetworkAvailableAndConnected()){
            new FetchItemsTask().execute();
            //默认删除所有行
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    db.delete("weather",null,null);
                    ContentValues values=new ContentValues();
                    Log.d(TAG,String.valueOf(mItems.size()));
                    //异步 输出0
                    for (int i=0;i<7;i++){
                        Log.d(TAG,"here");
                        values.put("date",mItems.get(i).getdate());
                        values.put("min_temp",mItems.get(i).getMin_temp());
                        values.put("max_temp",mItems.get(i).getMax_temp());
                        values.put("weather",mItems.get(i).getWeather());
                        values.put("weatherId",mItems.get(i).getWeatherId());
                        values.put("humidity",mItems.get(i).getHumidity());
                        values.put("pressure",mItems.get(i).getPressure());
                        values.put("wind",mItems.get(i).getWind());
                        values.put("location",mItems.get(i).getLocation());
                        db.insert("weather",null,values);
                        values.clear();}
                }
            }, 3000);//3秒后执行Runnable中的run方法

        }else {
            //查询所有数据
            Cursor cursor = db.query("weather", null, null, null, null, null, null); // 查询Book表中所有的数据
            if (cursor.moveToFirst()) {
                do {// 遍历Cursor对象，取出数据并打印
                    String date = cursor.getString(cursor. getColumnIndex("date"));
                    Log.d(TAG, date);
                    String weather = cursor.getString(cursor. getColumnIndex("weather"));
                    Log.d(TAG,weather );
                    int min_temp = cursor.getInt(cursor.getColumnIndex ("min_temp"));
                    Log.d(TAG,String.valueOf(min_temp) );
                    int max_temp = cursor.getInt(cursor.getColumnIndex ("max_temp"));
                    Log.d(TAG,String.valueOf(max_temp));
                    int weatherId = cursor.getInt(cursor.getColumnIndex ("weatherId"));
                    Log.d(TAG,String.valueOf(weatherId) );
                    int humidity = cursor.getInt(cursor.getColumnIndex ("humidity"));
                    Log.d(TAG,String.valueOf(humidity) );
                    int pressure = cursor.getInt(cursor.getColumnIndex ("pressure"));
                    Log.d(TAG,String.valueOf(pressure) );
                    int wind = cursor.getInt(cursor.getColumnIndex ("wind"));
                    Log.d(TAG,String.valueOf(wind) );
                    String location = cursor.getString(cursor. getColumnIndex("location"));
                    Log.d(TAG, location);
                    WeatherF w=new WeatherF();
                    w.setdate(date);
                    w.setWeather(weather);
                    w.setWeatherId(weatherId);
                    w.setMin_temp(min_temp);
                    w.setMax_temp(max_temp);
                    w.setHumidity(humidity);
                    w.setPressure(pressure);
                    w.setWind(wind);
                    w.setLocation(location);
                    mItems.add(w);
                } while (cursor.moveToNext());
            }

            cursor.close();
            setupAdapter();
        }
    }//获取网络内容
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_fragment, container, false);
        mweatherRecycle=view.findViewById(R.id.weather_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        mweatherRecycle.setLayoutManager(layoutManager);

        setupAdapter();
        return view;
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(getActivity().findViewById(R.id.content_layout)!=null){
            isTwoPane=true;
        }else {
            isTwoPane=false;
        }
    }


    private class FetchItemsTask extends AsyncTask<Void,Void,List<WeatherF>> {
        @Override
        protected List<WeatherF> doInBackground(Void... params) {
            return new HeFetchr().fetchItems(getContext());
        }

        @Override
        protected void onPostExecute(List<WeatherF> items) {
            mItems = items;
            setupAdapter();
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            mweatherRecycle.setAdapter(new WeatherAdapter(mItems));
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder
    implements  View.OnClickListener {
        WeatherF mweatherf;
        TextView mweek;
        TextView mmax;
        TextView mmin;
        TextView mweather;
        ImageView mweather_image;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.weather_item, parent, false));
            itemView.setOnClickListener(this);
            //itemview是Viewholder自带的=inflater.inflate(R.layout.weather_item, parent, false)返回的view
            mweek=(TextView)itemView.findViewById(R.id.weekday);
            mmax=(TextView)itemView.findViewById(R.id.max_tem);
            mmin=(TextView)itemView.findViewById(R.id.min_tem);
            mweather=(TextView)itemView.findViewById(R.id.weather);
            mweather_image=(ImageView)itemView.findViewById(R.id.weather_image);

        }
        public void bind(WeatherF w,int position){
            SharedPreferences pref=getContext().getSharedPreferences("file",0);
            String unit=pref.getString("units","Metric");
            mweatherf=w;
            Date d=w.parseServerTime(w.getdate(),null);
            switch (position){
                case 0:mweek.setText("Today");break;
                case 1:mweek.setText("Tomorrow");break;
                default: mweek.setText(w.parseDate(d));
            }
            mweather.setText(w.getWeather());
            if(unit.equals("Metric")) {
                mmax.setText(w.getMax_temp() + "°C");
                mmin.setText(w.getMin_temp() + "°C");
            }else {
                mmax.setText(w.getMax_temp() + "°F");
                mmin.setText(w.getMin_temp() + "°F");
            }
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
            }
            mweather_image.setImageResource(ImageId);
        }
        public void onClick(View v) {
            Toast.makeText(getActivity(),
                    mweatherf.getdate() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
            mCallbacks.onItemSelected(mweatherf);
        }

    }
    private class  WeatherAdapter extends RecyclerView.Adapter<ViewHolder>{
        private List<WeatherF> mweather;
        public WeatherAdapter(List<WeatherF> list){
            mweather=list;
        }
        public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater layoutInflater= LayoutInflater.from(getActivity());
            final ViewHolder holder=new ViewHolder(layoutInflater,parent);
            return holder;
        }
        public void onBindViewHolder(ViewHolder holder,int position){
            WeatherF w=mweather.get(position);
            holder.bind(w,position);

        }
        public int getItemCount(){
            return mweather.size();
        }
    }


}

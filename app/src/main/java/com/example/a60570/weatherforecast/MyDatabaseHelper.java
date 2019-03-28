package com.example.a60570.weatherforecast;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_WEATHER = "create table weather (id integer primary key autoincrement, "
            + " date text,min_temp integer, max_temp integer, weather text,weatherId integer,humidity integer," +
            "pressure integer,wind integer,location text)";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEATHER);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }
    @Override
    //只要传入一个比1大的数就可以执行更新方法、dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 2);
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists weather");
        onCreate(db);
    }




}

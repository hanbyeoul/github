package com.example.jaey.month.Month;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MonthDB extends SQLiteOpenHelper {
    public MonthDB(Context context) {
        super(context, "monthAddPlan.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table monthAddPlan " +
        "(_id integer primary key autoincrement, content text not null, sdate text not null, stime text not null, fdate text not null, ftime text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists monthAddPlan;");
        onCreate(db);
    }
}

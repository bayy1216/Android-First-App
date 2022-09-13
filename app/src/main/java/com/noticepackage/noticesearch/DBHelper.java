package com.noticepackage.noticesearch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context,String name){
        super(context,name,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("test","데이터베이스가 생성되었습니다");

        String sql="create table SearchDataTable("
                + "title text primary key, "
                + "time text not null, "
                + "site text not null, "
                + "siteCode text not null, "
                + "views text not null, "
                + "siteaddress text not null, "
                + "star integer default 0"
                + ")";

        db.execSQL(sql);





        String sql3="CREATE TABLE AlarmTable("
                +"word text primary key, "
                +"date text not null"
                + ")";
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                String sql="ALTER TABLE SearchDataTable ADD COLUMN alarmCheck integer default 0";
                //1에서 2로 테이블구조를 변경

                db.execSQL(sql);
            case 2:
                //2에서 3로
            case 3:
                //3에서 4로
        }

        Log.d("test","old:"+oldVersion+", new:"+newVersion);
    }
}

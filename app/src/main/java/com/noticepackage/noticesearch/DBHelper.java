package com.noticepackage.noticesearch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context,"Test.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("test","데이터베이스가 생성되었습니다");

        String sql="create table SearchDataTable("
                + "idx integer primary key autoincrement, "
                + "title text not null, "
                + "time text not null, "
                + "site text not null, "
                + "views text not null, "
                + "siteaddress text not null"
                + ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                //1에서 2로 테이블구조를 변경
            case 2:
                //2에서 3로
            case 3:
                //3에서 4로
        }

        Log.d("test","old:"+oldVersion+", new:"+newVersion);
    }
}

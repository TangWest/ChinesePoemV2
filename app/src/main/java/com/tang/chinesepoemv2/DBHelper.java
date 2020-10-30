package com.tang.chinesepoemv2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "chinesepoemdb";
    public static final String TB_NAME_1 = "poems";//被收藏的诗才会记录进数据库
    public static final String TB_NAME_2 = "notes";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context){
        this(context,DB_NAME,null,VERSION);
    }

    public DBHelper(){
        this(null,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE "+TB_NAME_1+"(POEMID TEXT PRIMARY KEY,POEMTITLE TEXT,POEMCONTENT TEXT)";
        String sql2 = "CREATE TABLE "+TB_NAME_2+"(POEMID TEXT,NOTECONTENT TEXT,NOTETIME TEXT,PRIMARY KEY(POEMID,NOTETIME))";

        db.execSQL(sql1);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}


package com.tang.chinesepoemv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NotesManager {
    private DBHelper dbHelper;
    private String tb_name;
    private String TAG = "NotesManager";

    public NotesManager(Context context){
        dbHelper = new DBHelper(context);
        tb_name = DBHelper.TB_NAME_2;
    }

    public void add(NotesItem item){
        Log.i(TAG,"add "+item.getPoemid()+" "+item.getNotecontent()+" "+item.getNotetime());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("POEMID", item.getPoemid());
        values.put("NOTECONTENT", item.getNotecontent());
        values.put("NOTETIME", item.getNotetime());
        db.insert(tb_name, null, values);
        db.close();
    }

    public void delete(String poemid,String notetime){
        Log.i(TAG,"delete "+poemid+" "+notetime);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(tb_name, "POEMID=? AND NOTETIME=?", new String[]{poemid,notetime});
        db.close();
    }

    public List<NotesItem> listAll(String poemid){
        List<NotesItem> notesList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tb_name, null,"POEMID="+poemid, null, null, null, "NOTETIME DESC");
        if(cursor!=null){
            notesList = new ArrayList<NotesItem>();
            while(cursor.moveToNext()){
                NotesItem item = new NotesItem();
                item.setPoemid(cursor.getString(cursor.getColumnIndex("POEMID")));
                item.setNotecontent(cursor.getString(cursor.getColumnIndex("NOTECONTENT")));
                item.setNotetime(cursor.getString(cursor.getColumnIndex("NOTETIME")));

                notesList.add(item);
            }
            cursor.close();
        }
        db.close();
        return notesList;
    }
}

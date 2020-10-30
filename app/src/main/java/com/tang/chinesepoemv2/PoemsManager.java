package com.tang.chinesepoemv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PoemsManager {

        private DBHelper dbHelper;
        private String tb_name;
        private String TAG = "PoemsManager";

        public PoemsManager(Context context){
            dbHelper = new DBHelper(context);
            tb_name = DBHelper.TB_NAME_1;
        }

//        public PoemsManager(){
//            dbHelper = new DBHelper();
//            tb_name = DBHelper.TB_NAME_1;
//        }

        public void add(PoemsItem item){
            Log.i(TAG,"add "+item.getPoemid()+" "+item.getPoemtitle()+" "+item.getPoemcontent());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("POEMID", item.getPoemid());
            values.put("POEMTITLE", item.getPoemtitle());
            values.put("POEMCONTENT", item.getPoemcontent());
            db.insert(tb_name, null, values);
            db.close();
        }

        public void delete(String poemid){
            Log.i(TAG,"delete "+poemid);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(tb_name, "POEMID=?", new String[]{poemid});
            db.close();
        }

        public int isInTable(String poemid){
            int mark=0;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(tb_name, null, "POEMID="+poemid, null, null, null, null);
            if(cursor!=null){
                if(cursor.getCount()>0){
                    mark=1;
                    cursor.moveToNext();
                    String title = cursor.getString(cursor.getColumnIndex("POEMTITLE"));
                    Log.i(TAG, title+" is in table.");
                }
                cursor.close();
            }
            db.close();
            return mark;
        }

        public List<PoemsItem> listAll(){
            List<PoemsItem> notesList = null;
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(tb_name, null, null, null, null, null, null);
            if(cursor!=null){
                notesList = new ArrayList<PoemsItem>();
                while(cursor.moveToNext()){
                    PoemsItem item = new PoemsItem();
                    item.setPoemid(cursor.getString(cursor.getColumnIndex("POEMID")));
                    item.setPoemtitle(cursor.getString(cursor.getColumnIndex("POEMTITLE")));
                    item.setPoemcontent(cursor.getString(cursor.getColumnIndex("POEMCONTENT")));

                    notesList.add(item);
                }
                cursor.close();
            }
            db.close();
            return notesList;
        }
}

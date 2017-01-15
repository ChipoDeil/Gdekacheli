package com.example.yaroslav.gdekacheli;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by chipodeil on 14.01.2017.
 */

public class MiniSqlHelper extends SQLiteOpenHelper {
    public static final Integer version = 5;
    public static final String DATABASE_NAME = "Main.db";
    public static final String COORDS_TABLE = "COORDS";
    public static final String COL_MAIN_1 = "ID";
    public static final String COL_MAIN_2 = "ASSOC_ID";
    public static final String COL_MAIN_3 = "TITLE";
    public static final String COL_MAIN_4 = "DESC";
    public static final String COL_MAIN_5 = "IMG";
    public static final String COL_MAIN_6 = "LATITUDE";
    public static final String COL_MAIN_7 = "LONGITUDE";
    public static final String COL_MAIN_8 = "RATING";
    public static final String COORDS_LOC_TABLE = "COORDS_LOC";
    public static final String COL_LOC_1 = "ID";
    public static final String COL_LOC_2 = "TITLE";
    public static final String COL_LOC_3 = "DESC";
    public static final String COL_LOC_4 = "IMG";
    public static final String COL_LOC_5 = "LATITUDE";
    public static final String COL_LOC_6 = "LONGITUDE";
    public static final String COL_LOC_7 = "RATING";

    public MiniSqlHelper(Context context){
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table " + COORDS_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, ASSOC_ID TEXT, TITLE TEXT, DESC TEXT, IMG TEXT, LATITUDE TEXT, LONGITUDE TEXT, RATING TEXT)");
        db.execSQL("CREATE table " + COORDS_LOC_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DESC TEXT, IMG BLOB, LATITUDE TEXT, LONGITUDE TEXT, RATING TEXT)");
    }
    public void reCreate(SQLiteDatabase db){
        db.execSQL("DROP table IF EXISTS " + COORDS_TABLE);
        db.execSQL("CREATE table " + COORDS_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, ASSOC_ID TEXT, TITLE TEXT, DESC TEXT, IMG TEXT, LATITUDE TEXT, LONGITUDE TEXT, RATING TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP table IF EXISTS " + COORDS_TABLE);
        db.execSQL("DROP table IF EXISTS " + COORDS_LOC_TABLE);
        onCreate(db);
    }

    public boolean insertLocData(String[] mass, byte[] b){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOC_2, mass[0]);
        values.put(COL_LOC_3, mass[1]);
        values.put(COL_LOC_4, b);
        values.put(COL_LOC_5, mass[2]);
        values.put(COL_LOC_6, mass[3]);
        values.put(COL_LOC_7, mass[4]);
        long a = db.insert(COORDS_LOC_TABLE, null, values);
        if (a == -1)
            return false;
        values.clear();
        return true;
    }

    public boolean insertData(ArrayList<String[]>  ar){
        SQLiteDatabase db = this.getWritableDatabase();
        reCreate(db);
        ContentValues values = new ContentValues();
        for (int i = 0; i < ar.size(); i++){
            String[] mass = ar.get(i);
            values.put(COL_MAIN_2, mass[0]);
            values.put(COL_MAIN_3, mass[1]);
            values.put(COL_MAIN_4, "");
            values.put(COL_MAIN_5, "");
            values.put(COL_MAIN_6, mass[2]);
            values.put(COL_MAIN_7, mass[3]);
            values.put(COL_MAIN_8, "");
            long a = db.insert(COORDS_TABLE, null, values);
            if (a == -1)
                return false;
            values.clear();

        }
        return true;
    }

    public Cursor getLocCoords(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + COORDS_LOC_TABLE, null);
        return res;
    }
    public Cursor getLocCoords(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + COORDS_LOC_TABLE + " WHERE ID = " + id, null);
        return res;
    }

    public Cursor getCoords(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + COORDS_TABLE, null);
        return res;
    }

    public Integer delLocCoords(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(COORDS_LOC_TABLE, "ID = ?", new String[] { id });
    }

}

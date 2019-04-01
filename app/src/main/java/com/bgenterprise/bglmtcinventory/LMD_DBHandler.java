package com.bgenterprise.bglmtcinventory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class LMD_DBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "lmd.db";
    private static final int DATABASE_VERSION = 1;
    Context context;


    public LMD_DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    public List<LMD> getLMTCLMDs(String LMTCID){
        //Gets a list of all the LMDs assigned to that LMTC.
        List<LMD> lmdList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDT WHERE LMTC_ID = '"+ LMTCID + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            lmdList.add(new LMD(cursor.getString(cursor.getColumnIndex("LMD_ID")), cursor.getString(cursor.getColumnIndex("LMD_Name")), cursor.getString(cursor.getColumnIndex("LMTC_ID"))));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return lmdList;
    }
}

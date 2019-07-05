package com.bgenterprise.bglmtcinventory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class LMDMapDBHandler extends SQLiteAssetHelper {

    private static final String database = "LMDMap.db";

    private static final String table = "LMDMapT";
    private static final String lmdid = "lmdid";
    private static final String lmdname = "lmdname";
    private static final String loginid = "loginid";
    private static final String timestamp = "timestamp";

    //Context context;

    LMDMapDBHandler(Context context){
        super(context, database, null, 1);
        //this.context = context;
    }

    /*@Override
    public void onCreate(SQLiteDatabase db) {
        //Create the LMDMap table.
        String create = "create table " + table + " ( "
                + lmdid + " text primary key, "
                + lmdname + " text, "
                + loginid + " text, "
                + timestamp + " text);";

        db.execSQL(create);
    }*/


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        super.onUpgrade(db, i, i1);
    }

    public String getLMDID(String loginID){
        SQLiteDatabase db = getWritableDatabase();
        String stringLmdID = "";

        String selectQuery = "SELECT * FROM " + table + " WHERE " + loginid + " = '"+ loginID +"'";
        Cursor cursor =  db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){ return stringLmdID; }

        stringLmdID = cursor.getString(cursor.getColumnIndex(lmdid));

        cursor.close();
        db.close();
        return stringLmdID;
    }

    public String lastLMDMapSyncDate(){
        SQLiteDatabase db = getWritableDatabase();

        String all = "SELECT * FROM " + table + " ORDER BY " + timestamp + " DESC";
        Cursor c = db.rawQuery(all, null);
        c.moveToFirst();

        if(c.getCount() < 1){ return "0"; }

        String lastUpdated = c.getString(c.getColumnIndex(timestamp));
        c.close();
        db.close();

        return lastUpdated;
    }

    public void LMDMapUpdate(String lmdID, String lmdName, String loginID, String timeStamp){
        SQLiteDatabase db = getWritableDatabase();

        String query = "REPLACE INTO " + table + " ("+ lmdid +","+ lmdname +","+ loginid +","+ timestamp +") VALUES " +
                "('"+ lmdID +"','"+ lmdName +"','"+ loginID +"','"+ timeStamp +"')";
        db.execSQL(query);
        Log.d("CHECK", query);

        db.close();
    }
}

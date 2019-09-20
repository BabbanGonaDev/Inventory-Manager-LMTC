package com.bgenterprise.bglmtcinventory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestockDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "restocks.db";
    private static final int DATABASE_VERSION = 1;

    //Context context;

    public RestockDBHandler(Context context){
        //Class constructor.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }


    public void recreateRestockT() {
        SQLiteDatabase db = getWritableDatabase();
        String RestockT_sql = "CREATE TABLE IF NOT EXISTS RestockT ( " +
                " UniqueID	TEXT PRIMARY KEY," +
                " LMDID	TEXT," +
                " ItemID	TEXT," +
                " RestockValue	TEXT," +
                " LMDKey	TEXT," +
                " Count	TEXT," +
                " RequestDate	TEXT," +
                " SyncStatus	TEXT," +
                " SyncDate	TEXT," +
                " Staff_ID	TEXT)";

        db.execSQL(RestockT_sql);
        ////db.close();
    }

    public boolean onAdd_RestockT(String UniqueID, String LMDID, String ItemID, String RestockValue,
                                  String LMDKey, String Count, String RequestDate, String Staff_ID, String SyncStatus) {

        //Insert entries into the RestockT table.
        recreateRestockT();
        try{
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO RestockT(UniqueID, LMDID, ItemID, RestockValue, LMDKey, Count, RequestDate,Staff_ID,SyncStatus) VALUES ('"+ UniqueID + "','" + LMDID +
                    "','" + ItemID + "','" + RestockValue + "','" + LMDKey + "','" + Count + "','" + RequestDate + "','" + Staff_ID + "','" + SyncStatus + "')";

            db.execSQL(insertQ);
            //db.close();
            return true;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Restocks> getAllRestocks(){
        //Gets a list of all the restocks existing in the restockT table.
        List<Restocks> restocks = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT LMDID, ItemID FROM RestockT ORDER BY RequestDate DESC", null);
        if(cursor.moveToFirst()){
            do{
                restocks.add(new Restocks(cursor.getString(cursor.getColumnIndex("LMDID")), cursor.getString(cursor.getColumnIndex("ItemID"))));
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }

        cursor.close();
        //db.close();
        return restocks;
    }

    ArrayList<Map<String, String>> uploadRestockT() {
        Map<String, String> map;
        ArrayList<Map<String, String>> wordList = new ArrayList<>();
        recreateRestockT();
        try{
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT UniqueID, LMDID, ItemID, RestockValue, LMDKey, Count, RequestDate, Staff_ID FROM RestockT WHERE SyncStatus = 'no' OR 'No'", null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                map = new HashMap<>();
                map.put("UniqueID", cursor.getString(cursor.getColumnIndex("UniqueID")));
                map.put("LMDID", cursor.getString(cursor.getColumnIndex("LMDID")));
                map.put("ItemID", cursor.getString(cursor.getColumnIndex("ItemID")));
                map.put("RestockValue", cursor.getString(cursor.getColumnIndex("RestockValue")));
                map.put("LMDKey", cursor.getString(cursor.getColumnIndex("LMDKey")));
                map.put("Count", cursor.getString(cursor.getColumnIndex("Count")));
                map.put("RequestDate", cursor.getString(cursor.getColumnIndex("RequestDate")));
                map.put("Staff_ID", cursor.getString(cursor.getColumnIndex("Staff_ID")));

                wordList.add(map);
                cursor.moveToNext();
            }
            cursor.close();
            //db.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return wordList;
    }

    void updateRestockTSyncStatus(JSONArray jsonArray) {
        JSONObject jsonObject;
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                db.execSQL("UPDATE RestockT SET SyncStatus = '" + jsonObject.getString("SyncStatus") + "', SyncDate = '" + jsonObject.getString("SyncDate") + "' WHERE UniqueID" +
                        " = '" + jsonObject.getString("UniqueID") + "'");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("HERE", e + "");
            }

        }
        //db.close();

    }



}

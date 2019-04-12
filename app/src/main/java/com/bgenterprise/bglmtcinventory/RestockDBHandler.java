package com.bgenterprise.bglmtcinventory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bgenterprise.bglmtcinventory.InvoiceDbContract.RestockT;
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

    Context context;

    public RestockDBHandler(Context context){
        //Class constructor.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    public boolean onAdd_RestockT(String LMDID, String ItemID, String RestockValue, String LMDKey, String Count, String RequestDate, String Staff_ID,
                                  String SyncStatus) {

        //Insert entries into the RestockT table.
        try{
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO RestockT(LMDID, ItemID, RestockValue, LMDKey, Count, RequestDate,Staff_ID,SyncStatus) VALUES ('" + LMDID +
                    "','" + ItemID + "','" + RestockValue + "','" + LMDKey + "','" + Count + "','" + RequestDate + "','" + Staff_ID + "','" + SyncStatus + "')";

            db.execSQL(insertQ);
            db.close();
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
        db.close();
        return restocks;
    }

    ArrayList<Map<String, String>> uploadRestockT() {
        Map<String, String> map;
        ArrayList<Map<String, String>> wordList = new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase db = getWritableDatabase();

        cursor = db.rawQuery("SELECT " + RestockT.COLUMN_LMD_ID + "," + RestockT.COLUMN_ITEM_ID + "," + RestockT.COLUMN_RESTOCK_VALUE + "," +
                RestockT.COLUMN_LMD_KEY + "," + RestockT.COLUMN_COUNT + "," + RestockT.COLUMN_REQUEST_DATE + "," + RestockT.COLUMN_STAFF_ID +
                " FROM " + RestockT.TABLE_NAME + " WHERE " + RestockT.COLUMN_SYNC_STATUS + "= 'no'", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            map = new HashMap<>();
            map.put("LMDID", cursor.getString(0));
            map.put("ItemID", cursor.getString(1));
            map.put("RestockValue", cursor.getString(2));
            map.put("LMDKey", cursor.getString(3));
            map.put("Count", cursor.getString(4));
            map.put("RequestDate", cursor.getString(5));
            map.put("Staff_ID", cursor.getString(6));

            wordList.add(map);
            cursor.moveToNext();
        }
        cursor.close();
        return wordList;
    }

    void updateRestockTSyncStatus(JSONArray jsonArray) {
        JSONObject jsonObject;
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                db.execSQL("UPDATE " + RestockT.TABLE_NAME + " SET " + RestockT.COLUMN_SYNC_STATUS + " = '" + jsonObject.getString("SyncStatus")
                        + "', " + RestockT.COLUMN_SYNC_DATE + " = \"" + jsonObject.getString("SyncDate") + "\" WHERE " + RestockT.COLUMN_LMD_ID +
                        "= \"" + jsonObject.getString("LMDID") + "\" AND " + RestockT.COLUMN_LMD_KEY + "= \"" + jsonObject.getString("LMDKey") +
                        "\" AND " + RestockT.COLUMN_ITEM_ID + "=\"" + jsonObject.getString("ItemID") + "\"");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("HERE", e + "");
            }

        }

    }



}

package com.bgenterprise.bglmtcinventory;

/*
  Database handler class for the Inventory Database.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bgenterprise.bglmtcinventory.InventoryDbContract.HoldingCostT;
import com.bgenterprise.bglmtcinventory.InventoryDbContract.Inventory03T;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InventoryDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    Context context;

    InventoryDBHandler(Context context) {
        //DBHandler constructor.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        super.onUpgrade(database, i, i2);
    }

    boolean onAdd_Inventory03T(String UniqueID, String TxnDate, String LMDID, String ItemID, String ItemName, String Unit, String Type, String UnitPrice, String Notes, String SyncDate, String SyncStatus) {
        try {
            //Inserts records into the Inventory03T table.
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO Inventory03T (UniqueID, TxnDate, LMDID, ItemID, ItemName, Unit, Type, UnitPrice, Notes, SyncDate, SyncStatus) " +
                    "VALUES (" + "'" + UniqueID + "','" + TxnDate + "','" + LMDID + "','" + ItemID + "','" + ItemName + "','" + Unit + "','" + Type +
                    "','" + UnitPrice + "','" + Notes + "','" + SyncDate + "','" + SyncStatus + "')";

            Log.d("CHECK", "Inventory03T InsertQ: " + insertQ);

            db.execSQL(insertQ);
            db.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    void updateInventory03T(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                int check = 0;
                Cursor cursor = db.rawQuery("SELECT COUNT(" + Inventory03T.COLUMN_UNIQUE_ID + ") FROM " + Inventory03T.TABLE_NAME + " WHERE " +
                        Inventory03T.COLUMN_UNIQUE_ID + "=\"" + jsonObject.get("UniqueID") + "\"", null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(Inventory03T.COLUMN_UNIQUE_ID, jsonObject.getString("UniqueID"));
                    contentValues.put(Inventory03T.COLUMN_TXN_DATE, jsonObject.getString("TxnDate"));
                    contentValues.put(Inventory03T.COLUMN_LMD_ID, jsonObject.getString("LMDID"));
                    contentValues.put(Inventory03T.COLUMN_ITEM_ID, jsonObject.getString("ItemID"));
                    contentValues.put(Inventory03T.COLUMN_ITEM_NAME, jsonObject.getString("ItemName"));
                    contentValues.put(Inventory03T.COLUMN_UNIT, jsonObject.getString("Unit"));
                    contentValues.put(Inventory03T.COLUMN_TYPE, jsonObject.getString("Type"));
                    contentValues.put(Inventory03T.COLUMN_UNIT_PRICE, jsonObject.getString("UnitPrice"));
                    contentValues.put(Inventory03T.COLUMN_NOTES, jsonObject.getString("Notes"));
                    contentValues.put(Inventory03T.COLUMN_SYNC_DATE, jsonObject.getString("SyncDate"));
                    contentValues.put(Inventory03T.COLUMN_SYNC_STATUS, jsonObject.getString("SyncStatus"));

                    db.insert(Inventory03T.TABLE_NAME, null, contentValues);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    Integer getNumberOf_FODEntries(String LMDid, String Itemid, String transDate) {
        //Gets the number of times that FOD count entries have been made in the database for a specific LMD.
        int FODEntry_count;

        //Get today's date.
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String todays_date = dateFormat.format(date);

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Inventory03T WHERE (LMDID = '" + LMDid + "') AND ItemID = '" + Itemid + "' AND(TxnDate <= '" + todays_date + "' AND TxnDate >= '" + transDate + "') AND Type = 'FOD'", null);
        cursor.moveToFirst();

        FODEntry_count = cursor.getCount();

        cursor.close();
        db.close();
        return FODEntry_count;
    }

    Integer getLMLTotalDeliveries(String LMDid, String item_id) {
        //Gets the number of LML Delivery entries have been made in the database for the LMD.
        int totalDeliveries;

        SQLiteDatabase database = getWritableDatabase();
        Cursor c = database.rawQuery("SELECT SUM(Unit) FROM Inventory03T WHERE LMDID = '" + LMDid + "' AND ItemID = '" + item_id + "' AND Type = 'LML'", null);
        c.moveToFirst();

        totalDeliveries = c.getInt(c.getColumnIndex("SUM(Unit)"));

        c.close();
        database.close();
        return totalDeliveries;
    }

    Integer DeliveriesSinceLastCount(String lmdID, String item, String lastDate) {

        int lastDeliveries = 0;

        //Get today's date.
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String todays_date = dateFormat.format(date);

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(Unit) FROM Inventory03T WHERE LMDID = '" + lmdID + "' AND ItemID = '" + item + "' AND Type = 'LML' AND (TxnDate <= '" + todays_date + "' AND TxnDate >= '" + lastDate + "')", null);
        cursor.moveToFirst();

        if (cursor.getCount() < 1) {
            return lastDeliveries;
        }

        while (!cursor.isAfterLast()) {
            lastDeliveries = cursor.getInt(cursor.getColumnIndex("SUM(Unit)"));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return lastDeliveries;
    }

    String GetLastLMLSyncDate() {
        String lastDate = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Inventory03T WHERE Type = 'LML' ORDER BY SyncDate DESC LIMIT 1", null);
        cursor.moveToFirst();
        if (cursor.getCount() < 1) {
            return "2018-10-01";
        }

        while (!cursor.isAfterLast()) {
            lastDate = cursor.getString(cursor.getColumnIndex("SyncDate"));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return lastDate;
    }

    ArrayList<Map<String, String>> getInventory03TRecords() {
        Map<String, String> map;
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Map<String, String>> wordList;
        wordList = new ArrayList<>();

        Log.d("CHECK", "Entered getInventory03TRecords");

        String getQ = "SELECT * FROM " + Inventory03T.TABLE_NAME + " WHERE " + Inventory03T.COLUMN_SYNC_STATUS + " = 'no'";
        Cursor cursor = db.rawQuery(getQ, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            map = new HashMap<>();
            map.put("UniqueID", cursor.getString(cursor.getColumnIndex("UniqueID")));
            map.put("TxnDate", cursor.getString(cursor.getColumnIndex("TxnDate")));
            map.put("LMDID", cursor.getString(cursor.getColumnIndex("LMDID")));
            map.put("ItemID", cursor.getString(cursor.getColumnIndex("ItemID")));
            map.put("ItemName", cursor.getString(cursor.getColumnIndex("ItemName")));
            map.put("Unit", cursor.getString(cursor.getColumnIndex("Unit")));
            map.put("Type", cursor.getString(cursor.getColumnIndex("Type")));
            map.put("UnitPrice", cursor.getString(cursor.getColumnIndex("UnitPrice")));
            map.put("Notes", cursor.getString(cursor.getColumnIndex("Notes")));

            wordList.add(map);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return wordList;

    }

    void updateSyncStatus(JSONArray jsonArray) {
        JSONObject jsonObject;
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                db.execSQL("UPDATE " + Inventory03T.TABLE_NAME + " SET " + Inventory03T.COLUMN_SYNC_STATUS + "=\"" + jsonObject.getString("SyncStatus") +
                        "\", " + Inventory03T.COLUMN_SYNC_DATE + "=\"" + jsonObject.getString("SyncDate") + "\" WHERE " + Inventory03T.COLUMN_UNIQUE_ID + "=\"" +
                        jsonObject.getString("UniqueID") + "\"");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("HERE", e + "");
            }

        }

    }

    public boolean emptyInventory03T() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM Inventory03T");
            db.close();
            Log.d("CHECK", "Emptied the Inventory03T table");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    void updateHoldingCostT(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                int check;
                Cursor cursor = db.rawQuery("SELECT COUNT(" + HoldingCostT.COLUMN_ID + ") FROM " + HoldingCostT.TABLE_NAME + " WHERE " +
                        HoldingCostT.COLUMN_ITEM_ID + " = \"" + jsonObject.getString("ItemID") + "\"", null);

                cursor.moveToFirst();
                check = cursor.getInt(0);
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(HoldingCostT.COLUMN_ITEM_ID, jsonObject.getString("ItemID"));
                    contentValues.put(HoldingCostT.COLUMN_HOLDING_COST, jsonObject.getString("HoldingCost"));
                    contentValues.put(HoldingCostT.COLUMN_LAST_CHANGE_DATE, jsonObject.getString("LastDateChange"));

                    db.insert(HoldingCostT.TABLE_NAME, null, contentValues);
                } else {
                    contentValues.put(HoldingCostT.COLUMN_ITEM_ID, jsonObject.getString("ItemID"));
                    contentValues.put(HoldingCostT.COLUMN_HOLDING_COST, jsonObject.getString("HoldingCost"));
                    contentValues.put(HoldingCostT.COLUMN_LAST_CHANGE_DATE, jsonObject.getString("LastDateChange"));
                    String where = HoldingCostT.COLUMN_ITEM_ID + " =?";
                    String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("ItemID"))};

                    db.update(HoldingCostT.TABLE_NAME, contentValues, where, whereArgs);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}

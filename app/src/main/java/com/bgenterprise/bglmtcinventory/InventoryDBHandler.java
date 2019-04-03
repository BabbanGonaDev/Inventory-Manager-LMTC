package com.bgenterprise.bglmtcinventory;

/**
 * Database handler class for the Inventory Database.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class InventoryDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    Context context;

    public InventoryDBHandler(Context context){
        //DBHandler constructor.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    public boolean onAdd_Inventory03T(String UniqueID, String TxnDate, String LMDID, String ItemID, String ItemName, String Unit, String Type, String UnitPrice, String Notes, String SyncDate, String SyncStatus){
        try{
            //Inserts records into the Inventory03T table.

            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO Inventory03T (UniqueID, TxnDate, LMDID, ItemID, ItemName, Unit, Type, UnitPrice, Notes, SyncDate, SyncStatus) VALUES (" +
                    "'" + UniqueID + "','" + TxnDate + "','" + LMDID + "','" + ItemID + "','" + ItemName + "','" + Unit + "','" + Type + "','" + UnitPrice + "','" + Notes+ "','" + SyncDate +"','"+ SyncStatus + "')";

            Log.d("CHECK", "Inventory03T InsertQ: " + insertQ);

            db.execSQL(insertQ);
            db.close();
            return true;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public Integer getNumberOf_FODEntries(String LMDid, String Itemid, String transDate){
        //Gets the number of times that FOD count entries have been made in the database for a specific LMD.
        int FODEntry_count = 0;

        //Get today's date.
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String todays_date = dateFormat.format(date);

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Inventory03T WHERE (LMDID = '"+ LMDid +"') AND ItemID = '"+ Itemid +"' AND(TxnDate <= '"+ todays_date +"' AND TxnDate >= '"+ transDate +"') AND Type = 'FOD'", null);
        cursor.moveToFirst();

        FODEntry_count = cursor.getCount();

        cursor.close();
        db.close();
        return  FODEntry_count;
    }

    public Integer getLMLTotalDeliveries(String LMDid, String item_id){
        //Gets the number of LML Delivery entries have been made in the database for the LMD.
        int totalDeliveries = 0;

        SQLiteDatabase database = getWritableDatabase();
        Cursor c = database.rawQuery("SELECT SUM(Unit) FROM Inventory03T WHERE LMDID = '" + LMDid + "' AND ItemID = '" + item_id + "' AND Type = 'LML'", null);
        c.moveToFirst();

        totalDeliveries = c.getInt(c.getColumnIndex("SUM(Unit)"));

        c.close();
        database.close();
        return totalDeliveries;
    }

    public Integer DeliveriesSinceLastCount(String lmdID, String item, String lastDate){

        int lastDeliveries = 0;

        //Get today's date.
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String todays_date = dateFormat.format(date);

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(Unit) FROM Inventory03T WHERE LMDID = '"+ lmdID +"' AND ItemID = '"+ item +"' AND Type = 'LML' AND (TxnDate <= '"+ todays_date +"' AND TxnDate >= '"+ lastDate +"')", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            return lastDeliveries;
        }

        while(!cursor.isAfterLast()){
            lastDeliveries = cursor.getInt(cursor.getColumnIndex("SUM(Unit)"));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return lastDeliveries;
    }

    public String GetLastLMLSyncDate(){
        String lastDate = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Inventory03T WHERE Type = 'LML' ORDER BY SyncDate DESC LIMIT 1", null);
        cursor.moveToFirst();
        if(cursor.getCount() < 1){
            return "2018-10-01";
        }

        while(!cursor.isAfterLast()){
            lastDate = cursor.getString(cursor.getColumnIndex("SyncDate"));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return lastDate;
    }

    public String getInventory03TRecords(){

            SQLiteDatabase db = getWritableDatabase();
            ArrayList<HashMap<String, String>> wordList;
            wordList = new ArrayList<HashMap<String, String>>();

            Log.d("CHECK", "Entered getInventory03TRecords");

            String getQ = "SELECT * FROM Inventory03T WHERE Type = 'FOD' AND SyncStatus = 'no'";
            Cursor cursor = db.rawQuery(getQ, null);
            cursor.moveToFirst();
            if(cursor.moveToFirst()){
                HashMap<String, String> map;
                do{
                    map = new HashMap<String, String>();
                    map.put("UniqueID", cursor.getString(cursor.getColumnIndex("UniqueID")));
                    map.put("TxnDate", cursor.getString(cursor.getColumnIndex("TxnDate")));
                    map.put("LMDID", cursor.getString(cursor.getColumnIndex("LMDID")));
                    map.put("ItemID", cursor.getString(cursor.getColumnIndex("ItemID")));
                    map.put("ItemName", cursor.getString(cursor.getColumnIndex("ItemName")));
                    map.put("Unit", cursor.getString(cursor.getColumnIndex("Unit")));
                    map.put("Type", cursor.getString(cursor.getColumnIndex("Type")));
                    map.put("UnitPrice", cursor.getString(cursor.getColumnIndex("UnitPrice")));
                    map.put("Notes", cursor.getString(cursor.getColumnIndex("Notes")));
                    map.put("SyncDate", cursor.getString(cursor.getColumnIndex("SyncDate")));
                    wordList.add(map);
                }while(cursor.moveToNext());
                cursor.close();
                db.close();
                Log.d("CHECK", "Wordlist generated");
            }

            Gson gson = new GsonBuilder().create();
            System.out.print(gson.toJson(wordList));
            return gson.toJson(wordList);

    }

    public boolean updateSyncStatus(String UniqueID, String status){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("UPDATE Inventory03T SET SyncStatus = '"+ status +"' WHERE UniqueID = '"+ UniqueID +"'");
            db.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean emptyInventory03T(){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM Inventory03T");
            db.close();
            Log.d("CHECK", "Emptied the Inventory03T table");
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }



}

package com.bgenterprise.bglmtcinventory;

/**
 * Database handler class for the Inventory Database.
 */

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.SQLInput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import com.bgenterprise.bglmtcinventory.InventoryDbContract.HoldingCostT;
import com.bgenterprise.bglmtcinventory.InventoryDbContract.Inventory03T;
import com.bgenterprise.bglmtcinventory.InvoiceDbContract.LMDInvoiceValueT;

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

    public void updateInvoiceT(String ItemId, String LmdId) throws ParseException {

        Map<String, String> map;
        ArrayList<Map<String, String>> wordList = new ArrayList<>();
        String dateAfterZero= null;
        String lastZeroDate = null;
        String hc = "0.00";
        double holdingCost;
        String lastDateWithHC = null;
        Cursor cursor = null;
        SQLiteDatabase database = getWritableDatabase();

//        getting the holding cost
        try {
            cursor = database.rawQuery("SELECT " + HoldingCostT.COLUMN_HOLDING_COST + " FROM " + HoldingCostT.TABLE_NAME +
                    " WHERE " + HoldingCostT.COLUMN_ITEM_ID + " =\"" + ItemId + "\"", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                hc = cursor.getString(0);
                cursor.moveToNext();
            }

            Log.d("hc",""+hc);
        } catch (Exception e) {
            Log.d("HERE", "" + e);
        }

        holdingCost = Double.valueOf(hc);
        Log.d("HC ", "" + holdingCost);

//        getting the last occurrence of zero balance
        try {
            cursor = database.rawQuery("SELECT " + Inventory03T.COLUMN_TXN_DATE + " FROM " + Inventory03T.TABLE_NAME + " WHERE " +
                    Inventory03T.COLUMN_UNIT + " = 0 AND " + Inventory03T.COLUMN_LMD_ID + "='" + LmdId + "'"
                    + " AND " + Inventory03T.COLUMN_ITEM_ID + "='" + ItemId + "' AND "+ Inventory03T.COLUMN_TYPE +" = 'FOD'"+ " ORDER BY " +
                    Inventory03T.COLUMN_TXN_DATE + " DESC LIMIT 1", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                lastZeroDate = cursor.getString(0);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.d("HERE", "" + e);
        }

        Log.d("lastZdate",""+lastZeroDate);

//        getting the next date after the lastZeroDate
        try {
            cursor = database.rawQuery("SELECT " + Inventory03T.COLUMN_TXN_DATE + " FROM " + Inventory03T.TABLE_NAME + " WHERE " +
                    Inventory03T.COLUMN_TXN_DATE + ">" + "'" + lastZeroDate + "'" + " AND " + Inventory03T.COLUMN_LMD_ID + "='" + LmdId + "'"
                    + " AND " + Inventory03T.COLUMN_ITEM_ID + "='" + ItemId + "' AND "+Inventory03T.COLUMN_TYPE+" = 'FOD'" +
                    " ORDER BY " + Inventory03T.COLUMN_TXN_DATE + " ASC LIMIT 1", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                dateAfterZero = cursor.getString(0);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.d("HERE", "" + e);
        }

        Log.d("DateAfterZero",""+dateAfterZero);

//        Converting the dateAFterZero to dd/mm/yyyy format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format_ = new SimpleDateFormat("dd/MM/yyyy");
        Date dateAfterZero_ = format_.parse(dateAfterZero);
        Log.d("date1",""+dateAfterZero_);

//        getting all instances of dates after dateAfterZero
        cursor = database.rawQuery("SELECT " + Inventory03T.COLUMN_TXN_DATE + ", " + Inventory03T.COLUMN_UNIT + " FROM " +
                Inventory03T.TABLE_NAME + " WHERE " + Inventory03T.COLUMN_TXN_DATE + ">" + "'" + dateAfterZero + "'" + " AND " +
                Inventory03T.COLUMN_LMD_ID + "='" + LmdId + "'" + " AND " + Inventory03T.COLUMN_ITEM_ID + "='"
                + ItemId + "' AND "+Inventory03T.COLUMN_TYPE +" = 'FOD'" + " ORDER BY " + Inventory03T.COLUMN_TXN_DATE, null);

//        looping through the table to calculate invoice values
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            map = new HashMap<>();
            map.put("date", cursor.getString(0));
            map.put("balance", cursor.getString(1));

            Log.d("date,bal",""+map.get("date")+" "+map.get("balance"));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date date = format.parse(map.get("date"));

//                checking for difference in date between stocking date and current date and whether it is <= 14
            long diffInMillies = Math.abs(date.getTime() - dateAfterZero_.getTime());
            Log.d("today",""+date);
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            Log.d("diff",""+diff);


            if (diff <= 14) {

            } else {
                Date today = new Date();
                SimpleDateFormat formatter_ = new SimpleDateFormat("dd/MM/yyyy");
                String today_string = formatter_.format(today);
//                    getting the count of entries with holding cost in the InvoiceValueT satisfying the WHERE conditions
                Cursor cursor1;
                cursor1 = database.rawQuery("SELECT * FROM " + LMDInvoiceValueT.TABLE_NAME +
                        " WHERE " + LMDInvoiceValueT.COLUMN_HOLDING_COST + "= 'Yes' AND " + LMDInvoiceValueT.COLUMN_LMD_ID + "='" +
                        LmdId + "'" + " AND " + LMDInvoiceValueT.COLUMN_ITEM_ID + "='" + ItemId + "' AND " +
                        LMDInvoiceValueT.COLUMN_TXN_DATE + " BETWEEN '" + dateAfterZero + "' AND '"+today_string+"'", null);

                Log.d("count",""+cursor1.getCount());

                int count = cursor1.getCount();
                cursor1.close();
                if (count == 0) {
//                        checking for difference in date between stocking date and current date
                    Date date1 = format.parse(map.get("date"));
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String today_string_ = formatter.format(date1);
                    long diffInMillies_ = Math.abs(date1.getTime() - dateAfterZero_.getTime());
                    long diff_ = TimeUnit.DAYS.convert(diffInMillies_, TimeUnit.MILLISECONDS);
                    long total_diff = diff_ - 14;
                    Log.d("total_diff", ""+total_diff);
                    double invoiceVal = Integer.valueOf(Objects.requireNonNull(map.get("balance"))) * total_diff * holdingCost;

//                        inserting invoice value details into InvoiceValueT
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(LMDInvoiceValueT.COLUMN_LMD_ID, LmdId);
                    contentValues.put(LMDInvoiceValueT.COLUMN_ITEM_ID, ItemId);
                    contentValues.put(LMDInvoiceValueT.COLUMN_TXN_DATE, String.valueOf(today_string));
                    contentValues.put(LMDInvoiceValueT.COLUMN_INVOICE_VALUE, String.valueOf(invoiceVal));
                    contentValues.put(LMDInvoiceValueT.COLUMN_HOLDING_COST, "Yes");

                    database.insert(LMDInvoiceValueT.TABLE_NAME, null, contentValues);

                } else {
                    Date today__ = new Date();
                    SimpleDateFormat formatter__ = new SimpleDateFormat("dd/MM/yyyy");
                    String today_string__ = formatter__.format(today__);
                    Cursor cursor2;
                    cursor2 = database.rawQuery("SELECT " + LMDInvoiceValueT.COLUMN_TXN_DATE + " FROM " + LMDInvoiceValueT.TABLE_NAME
                            + " WHERE " + LMDInvoiceValueT.COLUMN_TXN_DATE + " < '30/03/19' AND " + LMDInvoiceValueT.COLUMN_HOLDING_COST +
                            "= 'Yes' AND " + LMDInvoiceValueT.COLUMN_LMD_ID + "='" + LmdId + "'" + " AND " + LMDInvoiceValueT.COLUMN_ITEM_ID +
                            "='" + ItemId + "' ORDER BY " + LMDInvoiceValueT.COLUMN_TXN_DATE + " DESC LIMIT 1", null);
                    cursor2.moveToFirst();
                    while(!cursor2.isAfterLast()){
                        lastDateWithHC = cursor2.getString(0);
                        cursor2.moveToNext();
                    }
                    Log.d("lastDatewithHC",""+lastDateWithHC);
                    cursor2.close();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                    Date lastDateWithHC_ = format2.parse(lastDateWithHC);
                    Date today_ = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String today_str = formatter.format(today_);
                    long diffInMillies2 = Math.abs(today_.getTime() - lastDateWithHC_.getTime());
                    long diff2 = TimeUnit.DAYS.convert(diffInMillies2, TimeUnit.MILLISECONDS);
                    Log.d("diff2", ""+diff2);
                    double invoiceVal2 = Integer.valueOf(Objects.requireNonNull(map.get("balance"))) * diff2 * holdingCost;

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(LMDInvoiceValueT.COLUMN_LMD_ID, LmdId);
                    contentValues.put(LMDInvoiceValueT.COLUMN_ITEM_ID, ItemId);
                    contentValues.put(LMDInvoiceValueT.COLUMN_TXN_DATE, String.valueOf(today_str));
                    contentValues.put(LMDInvoiceValueT.COLUMN_INVOICE_VALUE, String.valueOf(invoiceVal2));
                    contentValues.put(LMDInvoiceValueT.COLUMN_HOLDING_COST, "Yes");

                    database.insert(LMDInvoiceValueT.TABLE_NAME, null, contentValues);
                }
            }
            cursor.moveToNext();
        }

    }


}

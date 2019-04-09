package com.bgenterprise.bglmtcinventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bgenterprise.bglmtcinventory.InvoiceDbContract.LMDInvoiceValueT;
import com.bgenterprise.bglmtcinventory.InvoiceDbContract.PriceGroupT;
import com.bgenterprise.bglmtcinventory.InvoiceDbContract.PriceT;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Database handler class for the Invoice Database.
 */

public class InvoiceDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "invoices.db";
    private static final int DATABASE_VERSION = 1;

    Context context;

    InvoiceDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    //Insert into the Invoice Database.
    boolean onAdd_LMDInvoiceValueT(String LMDID, String ItemID, String FODPhysicalCount, String TxnDate, String Type, String UnitPrice,
                                   String InvoiceQty, String InvoiceValue, String LastFODCount, String LastFODDate, String DeliverySinceLastCount) {

        try{
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO LMDInvoiceValueT (LMDID, ItemID, FODPhysicalCount, TxnDate, Type, UnitPrice, InvoiceQty, InvoiceValue, LastFODCount, LastFODdate, DeliverySinceLastCount) VALUES (" +
                    "'" + LMDID + "','" + ItemID + "','" + FODPhysicalCount + "','" + TxnDate + "','" + Type + "','" + UnitPrice + "','" + InvoiceQty + "','" + InvoiceValue+ "','" + LastFODCount + "','"+ LastFODDate +"','"+ DeliverySinceLastCount + "')";
            Log.d("CHECK", "Insert into LMDInvoiceValueT Query: " + insertQ);
            db.execSQL(insertQ);
            db.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //Get the price group for an LMD from the PriceGroupT table.
    String getPriceGroup(String lmdID) {
        String group;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT PGName FROM PriceGroupT WHERE LMDID = '" + lmdID + "'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            return "";
        }

        group = cursor.getString(cursor.getColumnIndex("PGName"));

        cursor.close();
        db.close();
        return group;
    }

    //Get the number of times, a price change has been recorded for a specific item in a price group.
    Integer getPriceChangeCount(String pgName, String itemID) {
        int count;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PriceT WHERE PriceGroup = '" + pgName + "' AND ItemID = '" + itemID + "'",null);
        cursor.moveToFirst();

        count = cursor.getCount();

        cursor.close();
        db.close();
        return count;
    }

    //Get the most recent price change for an Item in a priceGroup.
    Double getSinglePrice(String PG, String Itemid) {
        Double ItemPrice;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PriceT WHERE PriceGroup = '" + PG + "' AND ItemID = '" + Itemid + "' ORDER BY ChngDate DESC LIMIT 1", null);
        cursor.moveToFirst();

        ItemPrice = cursor.getDouble(cursor.getColumnIndex("Price"));

        cursor.close();
        db.close();
        return ItemPrice;
    }

    //Get the 2nd to the most recent price change in the Price table.
    Double getFormerPrice(String PG, String Itemid) {
        List<Double> formerPrice = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PriceT WHERE PriceGroup = '" + PG + "' AND ItemID = '" + Itemid + "' ORDER BY ChngDate DESC LIMIT 2", null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            formerPrice.add(cursor.getDouble(cursor.getColumnIndex("Price")));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return formerPrice.get(1);
    }

    //Get the latest price change date for an Item.
    String getLatestDate(String PG, String Itemid) {
        String date;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PriceT WHERE PriceGroup = '" + PG + "' AND ItemID = '" + Itemid + "' ORDER BY ChngDate DESC LIMIT 1", null);
        cursor.moveToFirst();

        date = cursor.getString(cursor.getColumnIndex("ChngDate"));

        cursor.close();
        db.close();
        return date;
    }

    //Get the sum of total Invoices for an LMD and Item.
    Integer getTotalInvoices(String LMDid, String item_id) {
        int totalInvoices;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + LMDid + "' AND ItemID = '" + item_id + "'", null);
        c.moveToFirst();

        totalInvoices = c.getInt(c.getColumnIndex("SUM(InvoiceValue)"));

        c.close();
        db.close();
        return totalInvoices;
    }

    //Get total LMD's Invoices.
    Double getLMDsInvoices(String lmdid) {
        double amount = 0.0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + lmdid + "'", null);
        c.moveToFirst();

        if(c.getCount() < 1){
            return amount;
        }

        amount = c.getDouble(c.getColumnIndex("SUM(InvoiceValue)"));


        c.close();
        db.close();
        return amount;
    }

    //Get a list of all the Invoices.
    List<Invoices> getAllInvoices() {
        List<Invoices> invoices = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT *, COUNT(LMDID), SUM(InvoiceValue) FROM LMDInvoiceValueT GROUP BY TxnDate, LMDID ORDER BY TxnDate DESC", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            invoices.add(new Invoices(cursor.getString(cursor.getColumnIndex("LMDID")), cursor.getString(cursor.getColumnIndex("TxnDate")),
                    cursor.getString(cursor.getColumnIndex("COUNT(LMDID)")), cursor.getString(cursor.getColumnIndex("SUM(InvoiceValue)"))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return invoices;
    }

    //Get a list of all the Invoices based on LMD.
    List<Invoices> getAllInvoices(String lmdid) {
        List<Invoices> invoices = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT LMDID, TxnDate, COUNT(LMDID), SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '"+ lmdid +"' GROUP BY TxnDate, LMDID ORDER BY TxnDate DESC", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            invoices.add(new Invoices(cursor.getString(cursor.getColumnIndex("LMDID")), cursor.getString(cursor.getColumnIndex("TxnDate")),
                    cursor.getString(cursor.getColumnIndex("COUNT(LMDID)")), cursor.getString(cursor.getColumnIndex("SUM(InvoiceValue)"))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return invoices;
    }

    //Get a list of the specific invoices attributed to an LMD on a specific date.
    List<Invoices> getSpecificLMDInvoices(String lmd_id, String txn_date) {
        List<Invoices> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '" + lmd_id + "' AND TxnDate = '" + txn_date + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            list.add(new Invoices(cursor.getString(cursor.getColumnIndex("ItemID")), cursor.getString(cursor.getColumnIndex("FODPhysicalCount")),
                    cursor.getString(cursor.getColumnIndex("UnitPrice")), cursor.getString(cursor.getColumnIndex("InvoiceQty")), cursor.getString(cursor.getColumnIndex("InvoiceValue"))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }


    //Gets the 2nd to the most recent invoice entry for the LMD and Item for the restock calc..
    String getFormerInvDate(String LMDID, String ItemID) {

        List<String> formerDate = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '"+ LMDID + "' AND ItemID = '"+ ItemID + "' ORDER BY TxnDate DESC LIMIT 2", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            formerDate.add(cursor.getString(cursor.getColumnIndex("TxnDate")));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        //Try to return the second to most recent date and if there isn't any, return 0000.

        try{

            return formerDate.get(1);

        }catch(Exception e){

            return "0000-00-00";
        }

    }

    Double getInvoiceSum(String lmd_id) {
        double sum = 0.00;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + lmd_id + "'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            return sum;
        }

        do{
            sum = cursor.getDouble(cursor.getColumnIndex("SUM(InvoiceValue)"));
            cursor.moveToNext();
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();

        return sum;
    }

    Double latestInvoice(String lmd_id) {
        double latest = 0.00;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String today = dateFormat.format(date);

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + lmd_id + "' AND TxnDate = '"+ today +"'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            return latest;
        }

        do{
            latest = cursor.getDouble(cursor.getColumnIndex("SUM(InvoiceValue)"));
            cursor.moveToNext();
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();

        return latest;
    }

    HashMap<String, String> LastCountDetails(String lmdID, String item) {

        HashMap<String, String> lastCount = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '"+ lmdID + "' AND ItemID = '"+ item + "' ORDER BY TxnDate DESC LIMIT 1", null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            lastCount = new HashMap<>();
            lastCount.put("FODPhysicalCount", cursor.getString(cursor.getColumnIndex("FODPhysicalCount")));
            lastCount.put("TxnDate", cursor.getString(cursor.getColumnIndex("TxnDate")));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return lastCount;
    }

    HashMap<String, String> getInvoiceDetails(String LMDID, String TxnDate, String ItemID) {
        HashMap<String, String> invoiceDetails = null;
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM LMDInvoiceValueT WHERE LMDID = '"+ LMDID +"' AND ItemID = '"+ ItemID +"' AND TxnDate = '"+ TxnDate +"'";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            invoiceDetails = new HashMap<>();
            invoiceDetails.put("LastFODCount", c.getString(c.getColumnIndex("LastFODCount")));
            invoiceDetails.put("LastFODDate", c.getString(c.getColumnIndex("LastFODdate")));
            invoiceDetails.put("DeliverySinceLastCount", c.getString(c.getColumnIndex("DeliverySinceLastCount")));
            invoiceDetails.put("CurrentCount", c.getString(c.getColumnIndex("FODPhysicalCount")));
            invoiceDetails.put("CurrentInvoice", c.getString(c.getColumnIndex("InvoiceValue")));
            c.moveToNext();
        }

        c.close();
        db.close();
        return invoiceDetails;
    }

    void updatePriceGroupT(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                int check = 0;
                Cursor cursor = db.rawQuery("SELECT COUNT(" + PriceGroupT.COLUMN_LMD_ID + ") from " + PriceGroupT.TABLE_NAME +
                        " WHERE " + PriceGroupT.COLUMN_PG_NAME + " =\"" + jsonObject.get("PGName") + "\"", null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(PriceGroupT.COLUMN_LMD_ID, jsonObject.getString("LMDID"));
                    contentValues.put(PriceGroupT.COLUMN_PG_NAME, jsonObject.getString("PGName"));

                    db.insert(PriceGroupT.TABLE_NAME, null, contentValues);
                } else {
                    contentValues.put(PriceGroupT.COLUMN_LMD_ID, jsonObject.getString("LMDID"));
                    contentValues.put(PriceGroupT.COLUMN_PG_NAME, jsonObject.getString("PGName"));
                    String where = PriceGroupT.COLUMN_LMD_ID + "=?";
                    String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("LMDID"))};
                    db.update(PriceGroupT.TABLE_NAME, contentValues, where, whereArgs);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void updatePriceT(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                int check = 0;
                Cursor cursor = db.rawQuery("SELECT COUNT(" + PriceT.COLUMN_ITEM_ID + ") FROM " + PriceT.TABLE_NAME + " WHERE " +
                        PriceT.COLUMN_PRICE_GROUP + " =\"" + jsonObject.get("PriceGroup") + "\" AND " + PriceT.COLUMN_ITEM_ID +
                        " = \"" + jsonObject.get("ItemID") + "\"", null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(PriceT.COLUMN_PRICE_GROUP, jsonObject.getString("PriceGroup"));
                    contentValues.put(PriceT.COLUMN_ITEM, jsonObject.getString("Item"));
                    contentValues.put(PriceT.COLUMN_ITEM_ID, jsonObject.getString("ItemID"));
                    contentValues.put(PriceT.COLUMN_PRICE, jsonObject.getString("Price"));
                    contentValues.put(PriceT.COLUMN_CHANGE_DATE, jsonObject.getString("ChngDate"));

                    db.insert(PriceT.TABLE_NAME, null, contentValues);
                } else {
                    contentValues.put(PriceT.COLUMN_PRICE, jsonObject.getString("Price"));
                    contentValues.put(PriceT.COLUMN_CHANGE_DATE, jsonObject.getString("ChngDate"));
                    String where = PriceT.COLUMN_ITEM_ID + " =? AND " + PriceT.COLUMN_PRICE_GROUP + " =?";
                    String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("ItemID")),
                            String.valueOf(jsonObject.getString("PriceGroup"))};

                    db.update(PriceT.TABLE_NAME, contentValues, where, whereArgs);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    void RefreshLMDInvoiceValueT(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                int check = 0;
                jsonObject = jsonArray.getJSONObject(i);
                Cursor cursor;
                cursor = db.rawQuery("SELECT COUNT(" + LMDInvoiceValueT.COLUMN_ID + ") FROM " + LMDInvoiceValueT.TABLE_NAME + " WHERE " +
                        LMDInvoiceValueT.COLUMN_LMD_ID + " = \"" + jsonObject.getString("LMDID") + "\" AND " + LMDInvoiceValueT.COLUMN_ITEM_ID +
                        " = \"" + jsonObject.getString("ItemID") + "\" AND " + LMDInvoiceValueT.COLUMN_FOD_PHYSICAL_COUNT + " = \"" +
                        jsonObject.getString("FODPhysicalCount") + "\" AND " + LMDInvoiceValueT.COLUMN_TXN_DATE + " = \"" + jsonObject.getString("TxnDate")
                        + "\"", null);

                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(LMDInvoiceValueT.COLUMN_ITEM_ID, jsonObject.getString("ItemID"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_LMD_ID, jsonObject.getString("LMDID"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_FOD_PHYSICAL_COUNT, jsonObject.getString("FODPhysicalCount"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_TXN_DATE, jsonObject.getString("TxnDate"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_TYPE, jsonObject.getString("Type"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_UNIT_PRICE, jsonObject.getString("UnitPrice"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_INVOICE_QTY, jsonObject.getString("InvoiceQty"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_INVOICE_VALUE, jsonObject.getString("InvoiceValue"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_LAST_FOD_COUNT, jsonObject.getString("LastFODCount"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_LAST_FOD_DATE, jsonObject.getString("LastFODdate"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_DELIVERY_SINCE_LAST_COUNT, jsonObject.getString("DeliverySinceLastCount"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_HOLDING_COST, jsonObject.getString("HoldingCost"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_SYNC_DATE, jsonObject.getString("SyncDate"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_SYNC_STATUS, jsonObject.getString("SyncStatus"));

                    db.insert(LMDInvoiceValueT.TABLE_NAME, null, contentValues);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    ArrayList<Map<String, String>> uploadLMDInvValueT() {
        Map<String, String> map;
        ArrayList<Map<String, String>> wordList = new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase db = getWritableDatabase();
        cursor = db.rawQuery("SELECT " + LMDInvoiceValueT.COLUMN_ID + "," + LMDInvoiceValueT.COLUMN_LMD_ID + "," + LMDInvoiceValueT.COLUMN_ITEM_ID + "," +
                LMDInvoiceValueT.COLUMN_FOD_PHYSICAL_COUNT + "," + LMDInvoiceValueT.COLUMN_TXN_DATE + "," + LMDInvoiceValueT.COLUMN_TYPE + "," +
                LMDInvoiceValueT.COLUMN_UNIT_PRICE + "," + LMDInvoiceValueT.COLUMN_INVOICE_QTY + "," + LMDInvoiceValueT.COLUMN_INVOICE_VALUE + "," +
                LMDInvoiceValueT.COLUMN_LAST_FOD_COUNT + "," + LMDInvoiceValueT.COLUMN_LAST_FOD_DATE + "," + LMDInvoiceValueT.COLUMN_DELIVERY_SINCE_LAST_COUNT
                + "," + LMDInvoiceValueT.COLUMN_HOLDING_COST + " FROM " + LMDInvoiceValueT.TABLE_NAME + " WHERE " + LMDInvoiceValueT.COLUMN_SYNC_STATUS +
                " = 'no'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            map = new HashMap<>();
            map.put("ID", cursor.getString(0));
            map.put("LMDID", cursor.getString(1));
            map.put("ItemID", cursor.getString(2));
            map.put("FODPhysicalCount", cursor.getString(3));
            map.put("TxnDate", cursor.getString(4));
            map.put("Type", cursor.getString(5));
            map.put("UnitPrice", cursor.getString(6));
            map.put("InvoiceQty", cursor.getString(7));
            map.put("InvoiceValue", cursor.getString(8));
            map.put("LastFODCount", cursor.getString(9));
            map.put("LastFODdate", cursor.getString(10));
            map.put("DeliverySinceLastCount", cursor.getString(11));
            map.put("HoldingCost", cursor.getString(12));

            wordList.add(map);
            cursor.moveToNext();
        }
        cursor.close();
        return wordList;
    }

    void updateLMDInvValueTSyncStatus(JSONArray jsonArray) {
        JSONObject jsonObject;
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                db.execSQL("UPDATE " + LMDInvoiceValueT.TABLE_NAME + " SET " + LMDInvoiceValueT.COLUMN_SYNC_STATUS + " = '" + jsonObject.getString("SyncStatus")
                        + "', " + LMDInvoiceValueT.COLUMN_SYNC_DATE + " = \"" + jsonObject.getString("SyncDate") + "\" WHERE " + LMDInvoiceValueT.COLUMN_LMD_ID +
                        " = \"" + jsonObject.getString("LMDID") + "\" AND " + LMDInvoiceValueT.COLUMN_ITEM_ID + "=\"" + jsonObject.getString("ItemID")
                        + "\" AND " + LMDInvoiceValueT.COLUMN_TXN_DATE + "=\"" + jsonObject.getString("TxnDate") + "\" AND " + LMDInvoiceValueT.COLUMN_INVOICE_VALUE +
                        "=\"" + jsonObject.getString("InvoiceValue") + "\"");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("HERE", e + "");
            }
        }
    }

}